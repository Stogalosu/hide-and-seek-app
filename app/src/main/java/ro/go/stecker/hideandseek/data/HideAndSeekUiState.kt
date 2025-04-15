package ro.go.stecker.hideandseek.data

import ro.go.stecker.hideandseek.ui.DrawType

data class HideAndSeekUiState(
    val selectedDrawType: DrawType = DrawType.Pick1,
    val isDeleteMenuActive: Boolean = false,
    val idToDelete: Int = 0,
    val cardDeck: MutableList<Card> = mutableListOf<Card>(),
    val drawnTempCards: List<Card> = listOf<Card>(),
    val selectCard: Boolean = false,
    val overflowingChalice: Int = 0
)

data class DeckUiState(
    val playerDeck: List<Card> = listOf(),
    val cardDeck: List<Card> = listOf()
)

fun DeckUiState.getCardWithId(cardId: Int): Card {
    return playerDeck[playerDeck.indexOf(CardsRepository[cardId])]
}

data class PreferencesUiState(
    val isGameStarted: GameState = GameState.Loading
)