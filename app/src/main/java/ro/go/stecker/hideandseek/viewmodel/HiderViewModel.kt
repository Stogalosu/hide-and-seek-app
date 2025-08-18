package ro.go.stecker.hideandseek.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ro.go.stecker.hideandseek.data.Card
import ro.go.stecker.hideandseek.data.HiderUiState
import ro.go.stecker.hideandseek.data.PreferencesRepository
import ro.go.stecker.hideandseek.data.SelectMode
import ro.go.stecker.hideandseek.data.UiState
import ro.go.stecker.hideandseek.data.database.DeckRepository
import ro.go.stecker.hideandseek.data.firestore.CloudRepository
import ro.go.stecker.hideandseek.data.isPlayable
import ro.go.stecker.hideandseek.ui.screens.DrawType
import kotlin.random.Random

class HiderViewModel(private val deckRepository: DeckRepository, private val preferencesRepository: PreferencesRepository, private val cloudRepository: CloudRepository): ViewModel() {

    /*
        StateFlow declarations
     */
    private val _uiState = MutableStateFlow(UiState())

    private val _hiderUiState = MutableStateFlow(HiderUiState())

    var hiderUiState: StateFlow<HiderUiState> =
        combine(
            deckRepository.getPlayerDeckStream(),
            deckRepository.getCardDeckStream(),
            _hiderUiState
        ) { playerDeck, cardDeck, _hiderState ->
            _hiderState.copy(playerDeck = playerDeck, cardDeck = cardDeck)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = HiderUiState()
        )

    init {
        viewModelScope.launch {
            combine(
                preferencesRepository.gameState.shareIn(viewModelScope, SharingStarted.Eagerly),
                preferencesRepository.gameId.shareIn(viewModelScope, SharingStarted.Eagerly),
                preferencesRepository.player.shareIn(viewModelScope, SharingStarted.Eagerly)
            ) { gameState, gameId, playerName ->
                UiState(
                    gameState = gameState,
                    gameId = gameId,
                    player = playerName
                )
            }.collect { value -> _uiState.value = value }
        }
    }

    /*
        UI methods
     */

    fun setUuidToDelete(uuid: String) {
        _hiderUiState.update { currentState ->
            currentState.copy(
                tempUuid = uuid
            )
        }
    }

    fun selectCard(card: Card) {
        _hiderUiState.update {
            it.copy(selectedCards = (it.selectedCards + card).toMutableList())
        }
    }

    fun deselectCard(card: Card) {
        _hiderUiState.update {
            it.copy(selectedCards = (it.selectedCards - card).toMutableList())
        }
    }

    suspend fun endCardSelection(confirm: Boolean) {
        if(_hiderUiState.value.selectedCards.isNotEmpty() && confirm)
            when (_hiderUiState.value.selectCardMode) {
                SelectMode.Duplicate -> {
                    deleteCard(_hiderUiState.value.tempUuid)
                    addCardToDeck(Card(id = _hiderUiState.value.selectedCards.first().id))
                }

                SelectMode.NotActive -> return

                else -> {
                    _hiderUiState.value.selectedCards.forEach { deleteCard(it.uuid) }
                    repeat(_hiderUiState.value.selectCardMode.howMany + 1) {
                        addCardToDeck(pickRandomCard())
                    }
                }
            }

        _hiderUiState.update { it.copy(selectCardMode = SelectMode.NotActive) }
        _hiderUiState.value.selectedCards.clear()
    }

    /*
        Card deck and player deck methods
     */

    suspend fun pickRandomCard(): Card {
        delay(50)
        val totalWeight = hiderUiState.value.cardDeck.sumOf { it.probability }
        var random = 0
        try {
            random = Random.nextInt(1, totalWeight)
        } catch (e: IllegalArgumentException) {

        }
        var cumulative = 0
        for(card in hiderUiState.value.cardDeck) {
            cumulative += card.probability
            if(random <= cumulative && card.probability > 0) {
                deckRepository.updateCardProbability(card)
                return Card(id = card.id)
            }
        }

        throw IllegalStateException("No cards left!")
    }

    suspend fun drawTempCards(drawType: DrawType) {
        var newCards = _hiderUiState.value.drawnTempCards
        repeat(drawType.draw) {
            newCards = newCards + pickRandomCard()
        }

        if (_hiderUiState.value.overflowingChalice in 1..3) {
            newCards = newCards + pickRandomCard()
            updateOverflowingChalice()
        }

        _hiderUiState.update {
            it.copy(drawnTempCards = newCards)
        }
    }

    fun clearTempCards() {
        _hiderUiState.update { currentState ->
            currentState.copy(drawnTempCards = listOf())
        }
    }

    fun updateOverflowingChalice() {
        _hiderUiState.update { currentState ->
            currentState.copy(
                overflowingChalice = currentState.overflowingChalice + 1
            )
        }
    }

    suspend fun addCardToDeck(card: Card) = deckRepository.insertCard(card)

    suspend fun deleteCard(uuid: String) = deckRepository.deleteCard(uuid)

    fun playCard(card: Card, onDone: (Boolean) -> Unit) {
        if(card.id == 17) updateOverflowingChalice() /*If card is "Curse of the overflowing chalice"*/

        if(card.isPlayable()) {
            cloudRepository.playCard(
                gameId = _uiState.value.gameId,
                sender = _uiState.value.player,
                card = card,
                onDone = onDone
            )
        }
    }


    fun playSpecialCard(card: Card): Boolean {
        when(card.id) {
            //Duplicate Card
            27 -> {
                _hiderUiState.update {
                    it.copy(selectCardMode = SelectMode.Duplicate, tempUuid = card.uuid)
                }
                return true
            }

            //Discard 1, draw 2
            29 -> {
                _hiderUiState.update {
                    it.copy(selectCardMode = SelectMode.Discard1Draw2, tempUuid = card.uuid)
                }
                return true
            }

            //Discard 2, draw 3
            30 -> {
                _hiderUiState.update {
                    it.copy(selectCardMode = SelectMode.Discard2Draw3, tempUuid = card.uuid)
                }
                return true
            }

            else -> return false
        }
    }
}