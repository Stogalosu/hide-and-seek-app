package ro.go.stecker.hideandseek.data

import ro.go.stecker.hideandseek.ui.DrawType

data class HideAndSeekUiState(
    val selectedDrawType: DrawType = DrawType.Pick1,
    val isDeleteMenuActive: Boolean = false,
    val idToDelete: Int = 0,
    val cardsList: MutableList<Card> = mutableListOf<Card>(),
    val cardDeck: MutableList<Card> = mutableListOf<Card>(),
    val drawnTempCards: MutableList<Card> = mutableListOf<Card>(),
    val selectCard: Boolean = false,
    val overflowingChalice: Int = 0
)

data class DeckUiState(
    val cardDeck: List<Card> = listOf()
)

fun DeckUiState.getCardWithId(cardId: Int): Card {
    return cardDeck[cardDeck.indexOf(CardsRepository[cardId])]
}

data class PreferencesUiState(
    val isGameStarted: GameState = GameState.Loading
)