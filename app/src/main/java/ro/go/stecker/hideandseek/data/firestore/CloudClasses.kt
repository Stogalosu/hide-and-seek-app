package ro.go.stecker.hideandseek.data.firestore

import ro.go.stecker.hideandseek.data.GameState


enum class PlayerType {
    Hider, Seeker, NotSet
}

fun PlayerType.toGameState(): GameState {
    return when(this) {
        PlayerType.Hider -> GameState.Hider
        PlayerType.Seeker -> GameState.Seeker
        PlayerType.NotSet -> GameState.NotStarted
    }
}

data class Game(
    val id: Int = 0,
    val players: List<Player> = emptyList(),
    val started: Boolean = false
)

data class Player(
    val uuid: String = "",
    val name: String = "",
    var type: PlayerType = PlayerType.NotSet
)