package ro.go.stecker.hideandseek.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import retrofit2.Response
import ro.go.stecker.hideandseek.data.Card
import ro.go.stecker.hideandseek.data.DeckUiState
import ro.go.stecker.hideandseek.data.HiderUiState
import ro.go.stecker.hideandseek.data.SelectMode
import ro.go.stecker.hideandseek.data.SentCard
import ro.go.stecker.hideandseek.data.database.DeckRepository
import ro.go.stecker.hideandseek.data.isPlayable
import ro.go.stecker.hideandseek.data.toSentCard
import ro.go.stecker.hideandseek.network.CardApi
import ro.go.stecker.hideandseek.ui.screens.DrawType
import java.io.IOException
import kotlin.random.Random

class HiderViewModel(val deckRepository: DeckRepository): ViewModel() {

    /*
        StateFlow declarations
     */

    private val _hiderUiState = MutableStateFlow(HiderUiState())
    val hiderUiState: StateFlow<HiderUiState> = _hiderUiState.asStateFlow()

    var deckUiState: StateFlow<DeckUiState> =
        combine(
            deckRepository.getPlayerDeckStream(),
            deckRepository.getCardDeckStream()
        ) { playerDeck, cardDeck ->
            DeckUiState(playerDeck = playerDeck, cardDeck = cardDeck)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(),
            initialValue = DeckUiState()
        )

    /*
        UI methods
     */

    fun setUuidToDelete(uuid: String) {
        _hiderUiState.update { currentState ->
            currentState.copy(
                uuidToDelete = uuid
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
                    deleteCard(_hiderUiState.value.uuidToDelete)
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
        val totalWeight = deckUiState.value.cardDeck.sumOf { it.probability }
        var random = 0
        try {
            random = Random.Default.nextInt(1, totalWeight)
        } catch (e: IllegalArgumentException) {

        }
        var cumulative = 0
        for(card in deckUiState.value.cardDeck) {
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

    suspend fun playCard(card: Card, context: Context): Response<SentCard>? {
        if(card.id == 17) updateOverflowingChalice() /*If card is "Curse of the overflowing chalice"*/

        if(card.isPlayable()) {
            try {
                return CardApi.retrofitService.newCard(card.toSentCard(context))
            } catch (e: IOException) {
                return null
            }
        } else throw IllegalStateException()
    }


    fun playSpecialCard(card: Card): Boolean {
        when(card.id) {
            //Duplicate Card
            27 -> {
                _hiderUiState.update {
                    it.copy(selectCardMode = SelectMode.Duplicate, uuidToDelete = card.uuid)
                }
                return true
            }

            //Discard 1, draw 2
            29 -> {
                _hiderUiState.update {
                    it.copy(selectCardMode = SelectMode.Discard1Draw2, uuidToDelete = card.uuid)
                }
                return true
            }

            //Discard 2, draw 3
            30 -> {
                _hiderUiState.update {
                    it.copy(selectCardMode = SelectMode.Discard2Draw3, uuidToDelete = card.uuid)
                }
                return true
            }

            else -> return false
        }
    }
}