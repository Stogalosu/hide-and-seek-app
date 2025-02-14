package ro.go.stecker.hideandseek.data

import ro.go.stecker.hideandseek.ui.DrawType

data class HideAndSeekUiState(
    val selectedDrawType: DrawType = DrawType.Pick1,
    val isDeleteMenuActive: Boolean = false,
    val cardToDelete: Int = 0,
    val cardsList: MutableList<Card> = mutableListOf<Card>(),
    val cardDeck: MutableList<Card> = mutableListOf<Card>(),
    val drawnTempCards: MutableList<Card> = mutableListOf<Card>(),
    val selectCard: Boolean = false,
    var tooManyCards: Boolean = false,
    val overflowingChalice: Int = 0
)