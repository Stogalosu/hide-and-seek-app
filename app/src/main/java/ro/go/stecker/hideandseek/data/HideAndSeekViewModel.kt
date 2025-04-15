package ro.go.stecker.hideandseek.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ro.go.stecker.hideandseek.data.database.DeckRepository
import ro.go.stecker.hideandseek.ui.DrawType
import kotlin.random.Random.Default.nextInt

class HideAndSeekViewModel(val deckRepository: DeckRepository, val preferencesRepository: PreferencesRepository): ViewModel() {

    private val _uiState = MutableStateFlow(HideAndSeekUiState())
    val uiState: StateFlow<HideAndSeekUiState> = _uiState.asStateFlow()

    var deckUiState: StateFlow<DeckUiState> =
        combine(
            deckRepository.getPlayerDeckStream(),
            deckRepository.getCardDeckStream()
        ) { playerDeck, cardDeck ->
            DeckUiState(playerDeck = playerDeck, cardDeck = cardDeck)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = DeckUiState()
        )

    private val _preferencesUiState = MutableStateFlow(PreferencesUiState())
    val preferencesUiState: StateFlow<PreferencesUiState> = _preferencesUiState.asStateFlow()

    fun init() {
        viewModelScope.launch {
            preferencesRepository.isGameStarted
                .map { PreferencesUiState(it) }
                .collect { value -> _preferencesUiState.value = value}
        }
    }

    suspend fun initAtGameStart() {
        if(preferencesUiState.value.isGameStarted == GameState.NotStarted) {
            deckRepository.clearDeck()
            preferencesRepository.startGame()
        }
        deckRepository.setCardList(CardsRepository)
    }

    fun endGame() {
        viewModelScope.launch {
            preferencesRepository.endGame()
        }
    }

    fun updateDrawType(drawType: DrawType) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedDrawType = drawType
            )
        }
    }

    fun updateDeleteMenu() {
        _uiState.update { currentState ->
            currentState.copy(
                isDeleteMenuActive = !currentState.isDeleteMenuActive
            )
        }
    }

    fun setIdToDelete(cardId: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                idToDelete = cardId
            )
        }
    }

    suspend fun deleteCard(cardId: Int) = deckRepository.deleteDrawnCard(cardId)


    fun updateSelectCardText(state: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                selectCard = state
            )
        }
    }

    fun updateOverflowingChalice() {
        _uiState.update { currentState ->
            currentState.copy(
                overflowingChalice = currentState.overflowingChalice + 1
            )
        }
    }

    suspend fun pickRandomCard(): Card {
        var totalWeight = deckUiState.value.cardDeck.sumOf { it.probability }
        var random: Int = 0
        try {
            random = nextInt(1, totalWeight)
        } catch (e: IllegalArgumentException) {

        }
        var cumulative = 0
        for(card in deckUiState.value.cardDeck) {
            cumulative += card.probability
            if(random <= cumulative) {
                deckRepository.updateCardProbability(card)
                return CardsRepository[card.id]
            }
        }

        throw IllegalStateException("No cards left!")
    }

    suspend fun drawTempCards() {
        var newCards = _uiState.value.drawnTempCards
        repeat(_uiState.value.selectedDrawType.draw) {
            newCards = newCards + pickRandomCard()
        }

        if (_uiState.value.overflowingChalice in 1..3) {
            newCards = newCards + pickRandomCard()
            updateOverflowingChalice()
        }

        _uiState.update {
            it.copy(drawnTempCards = newCards)
        }
    }

    suspend fun addCardToDeck(card: Card) = deckRepository.insertDrawnCard(card.toDrawnCard())

    fun clearTempCards() {
        _uiState.update { currentState ->
            currentState.copy(drawnTempCards = listOf())
        }
    }
}