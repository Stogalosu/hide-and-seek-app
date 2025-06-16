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
    @Ignore @StringRes
    val name: Int = 0,
    @Ignore @StringRes
    val description: Int = 0,
    @Ignore @DrawableRes
    val image: Int = 0,
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

fun Card.toSentCard(context: Context): SentCard {
    return SentCard(name = context.getString(this.getName()))
}

data class SentCard(
    @SerializedName("name")
    val name: String,
    @SerializedName("token")
    val token: String = "",
    @SerializedName("time")
    val time: String = ""
)