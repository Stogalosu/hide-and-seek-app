package ro.go.stecker.hideandseek.data

import ro.go.stecker.hideandseek.data.firestore.Player
import ro.go.stecker.hideandseek.data.firestore.PlayerType
import ro.go.stecker.hideandseek.network.NetworkStatus

enum class SelectMode(val howMany: Int) {
    Duplicate(1),
    Discard1Draw2(1),
    Discard2Draw3(2),
    NotActive(0)
}

data class HiderUiState(
    val tempUuid: String = "",
    val drawnTempCards: List<Card> = listOf<Card>(),
    val overflowingChalice: Int = 0,
    var selectCardMode: SelectMode = SelectMode.NotActive,
    var selectedCards: MutableList<Card> = mutableListOf<Card>(),
    val playerDeck: List<Card> = listOf(),
    val cardDeck: List<CardDetails> = listOf()
)

fun HiderUiState.getCardWithUuid(uuid: String): Card {
    return playerDeck.first { it.uuid == uuid }
}

data class SeekerUiState(
    val curses: List<SentCard> = listOf(),
    val tempUuid: String = ""
)

data class UiState(
    val gameState: GameState = GameState.Loading,
    val gameId: Int = 0,
    val player: Player = Player("", "", PlayerType.NotSet),
    val networkStatus: NetworkStatus = NetworkStatus.Available
)