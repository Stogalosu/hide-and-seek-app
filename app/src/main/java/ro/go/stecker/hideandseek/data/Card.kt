package ro.go.stecker.hideandseek.data

import androidx.annotation.*
import androidx.room.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import ro.go.stecker.hideandseek.R
import ro.go.stecker.hideandseek.data.firestore.Player
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

enum class CardType() {
    Curse,
    PowerUp,
    TimeBonus
}

@OptIn(ExperimentalUuidApi::class)
@Entity(tableName = "player_deck")
data class Card(
    @PrimaryKey(autoGenerate = false)
    var uuid: String = Uuid.random().toString(),
    var id: Int = 0
)

@Entity(tableName = "card_deck")
data class CardDetails(
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0,
    @Ignore
    val type: CardType = CardType.TimeBonus,
    @Ignore
    val expiryMins: Int = 0,
    @Ignore @StringRes
    val name: Int = 0,
    @Ignore @StringRes
    val description: Int = R.string.empty,
    @Ignore @DrawableRes
    val image: Int = 0,
    @Ignore
    val isPlayable: Boolean = false,
    var probability: Int = 1
)

fun Card.getName(): Int {
    return CardsRepository[this.id].name
}

fun Card.getDescription(): Int {
    return CardsRepository[this.id].description
}

fun Card.getImage(): Int {
    return CardsRepository[this.id].image
}

fun Card.getType(): CardType {
    return CardsRepository[this.id].type
}

fun Card.getExpiryMins(): Int {
    return CardsRepository[this.id].expiryMins
}

fun Card.isPlayable(): Boolean {
    return CardsRepository[this.id].isPlayable
}

fun Card.toSentCard(gameId: Int, sender: Player): SentCard {
    return SentCard(
        id = this.id,
        uuid = this.uuid,
        gameId = gameId,
        sender = sender,
        expiryMins = this.getExpiryMins()
    )
}

data class SentCard(
    val id: Int = 0,
    val uuid: String = "",
    val gameId: Int = 0,
    val sender: Player = Player(),
    @ServerTimestamp
    val playedAt: Timestamp = Timestamp.now(),
    val expiryMins: Int = 0
)

fun SentCard.toCard(): Card {
    return Card(uuid = this.uuid, id = this.id)
}