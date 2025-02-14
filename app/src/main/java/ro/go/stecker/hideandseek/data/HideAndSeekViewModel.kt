package ro.go.stecker.hideandseek.data

import androidx.compose.runtime.currentRecomposeScope
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ro.go.stecker.hideandseek.ui.DrawType
import kotlin.random.Random.Default.nextInt

class HideAndSeekViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(HideAndSeekUiState())
    val uiState: StateFlow<HideAndSeekUiState> = _uiState.asStateFlow()

    fun init() {
        _uiState.update { currentState ->
            currentState.copy(
                cardsList = CardsRepository.toMutableList()
            )
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

    fun setIndexToDelete(card: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                cardToDelete = card
            )
        }
    }

    fun deleteCardAtIndex(index: Int) {
        _uiState.value.cardDeck.removeAt(index)
    }

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

    fun getRandomCard(): Card {
        if(_uiState.value.cardsList.last().probability > 1) {
            val random = nextInt(1, _uiState.value.cardsList.last().probability)
            var index = 0
            while (_uiState.value.cardsList[index].probability < random && index < _uiState.value.cardsList.lastIndex) index++
            if (_uiState.value.cardsList[index].probability >= random) return _uiState.value.cardsList[index]
            else return getRandomCard()
        } else return Card()
    }
    
    fun drawTempCards() {
        updateSelectCardText(true)
        var previousCard: Card = Card()
        var card: Card = Card()
        for(c in 1.._uiState.value.selectedDrawType.draw) {
            card = getRandomCard()
            while(card == previousCard) card = getRandomCard()
            _uiState.value.drawnTempCards.add(card)
            previousCard = card
        }
        if(1 <= _uiState.value.overflowingChalice && _uiState.value.overflowingChalice <= 3) {
            _uiState.value.drawnTempCards.add(getRandomCard())
            updateOverflowingChalice()
        }
    }
    
    fun addCardToDeck(card: Card) {
        _uiState.value.cardDeck.add(card)
        for(card in _uiState.value.drawnTempCards) {
            for (i in _uiState.value.cardsList.indexOf(card).._uiState.value.cardsList.lastIndex) {
                if (_uiState.value.cardsList[i].probability > 0)
                    _uiState.value.cardsList[i].probability--
            }
        }

    }
}