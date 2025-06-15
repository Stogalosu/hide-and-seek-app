package ro.go.stecker.hideandseek.data

import android.content.Context
import androidx.annotation.*
import androidx.room.*
import com.google.gson.annotations.SerializedName
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

enum class CardType() {
    Curse,
    PowerUp,
    TimeBonus
}

@Entity(tableName = "card_list")
data class Card @OptIn(ExperimentalUuidApi::class) constructor(
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0,
    @Ignore
    var uuid: String = Uuid.random().toString(),
    @Ignore @StringRes
    val name: Int = 0,
    @Ignore
    val type: CardType = CardType.TimeBonus,
    @Ignore @StringRes
    val description: Int = 0,
    @Ignore @DrawableRes
    val image: Int = 0,
    var probability: Int = 1
)

@OptIn(ExperimentalUuidApi::class)
fun Card.toDrawnCard(): DrawnCard {
    return DrawnCard(uuid = Uuid.random().toString(), cardId = this.id)
}

fun Card.toSentCard(context: Context): SentCard {
    return SentCard(name = context.getString(this.name))
}

@Entity(tableName = "deck")
data class DrawnCard(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uuid: String,
    val cardId: Int
)

@OptIn(ExperimentalUuidApi::class)
fun DrawnCard.toCard(): Card {
    return CardsRepository[this.cardId].copy(uuid = this.uuid)
}

data class SentCard(
    @SerializedName("name")
    val name: String,
    @SerializedName("token")
    val token: String = "",
    @SerializedName("time")
    val time: String = ""
)