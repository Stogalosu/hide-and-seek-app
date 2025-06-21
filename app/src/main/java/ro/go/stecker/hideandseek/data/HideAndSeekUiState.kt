package ro.go.stecker.hideandseek.data


enum class SelectMode(val howMany: Int) {
    Duplicate(1),
    Discard1Draw2(1),
    Discard2Draw3(2),
    NotActive(0)
}

data class HideAndSeekUiState(
    val uuidToDelete: String = "",
    val cardDeck: MutableList<Card> = mutableListOf<Card>(),
    val drawnTempCards: List<Card> = listOf<Card>(),
    val overflowingChalice: Int = 0,
    var selectCardMode: SelectMode = SelectMode.NotActive,
    var selectedCards: MutableList<Card> = mutableListOf<Card>()
)

data class DeckUiState(
    val playerDeck: List<Card> = listOf(),
    val cardDeck: List<CardDetails> = listOf()
)

fun DeckUiState.getCardWithUuid(uuid: String): Card {
    return playerDeck.first { it.uuid == uuid }
}

data class PreferencesUiState(
    val isGameStarted: GameState = GameState.Loading
)