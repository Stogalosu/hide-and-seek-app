package ro.go.stecker.hideandseek.data

import androidx.annotation.*
import androidx.room.*

enum class CardType() {
    Curse,
    PowerUp,
    TimeBonus
}

@Entity(tableName = "card_list")
data class Card(
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0,
    @Ignore @StringRes
    val name: Int = 0,
    @Ignore
    val type: CardType = CardType.TimeBonus,
    @Ignore @DrawableRes
    val image: Int = 0,
    var probability: Int = 1
)

fun Card.toDrawnCard(): DrawnCard {
    return DrawnCard(cardId = this.id)
}

@Entity(tableName = "deck")
data class DrawnCard(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cardId: Int
)

fun DrawnCard.toCard(): Card {
    return CardsRepository[this.cardId]
}