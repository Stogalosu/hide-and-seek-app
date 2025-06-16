package ro.go.stecker.hideandseek.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ro.go.stecker.hideandseek.data.Card
import ro.go.stecker.hideandseek.data.CardDetails

@Dao
interface CardDao {
    /*
     *  Methods for "card_deck" table
     */
    @Insert(entity = CardDetails::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCardDeck(cardList: List<CardDetails>)

    @Query("UPDATE card_deck SET probability = :probability WHERE id = :id")
    suspend fun updateCardProbability(id: Int, probability: Int)

    @Query("DELETE FROM card_deck")
    suspend fun clearCardDeck()

//    @Query("SELECT id FROM card_list ORDER BY id DESC LIMIT 1")
//    suspend fun getMaxId(): Int

    @Query("SELECT probability FROM card_deck WHERE id = :id LIMIT 1")
    suspend fun getCardProbability(id: Int): Int

    @Query("SELECT * FROM card_deck")
    fun getCardDeckStream(): Flow<List<CardDetails>>

    /*
     *  Methods for "player_deck" table
     */
    @Insert(entity = Card::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCard(card: Card)

//    @Update(entity = DrawnCard::class)
//    suspend fun updateDrawnCard(card: DrawnCard)

    @Query("DELETE FROM player_deck WHERE uuid = :uuid")
    suspend fun deleteCard(uuid: String)

    @Query("DELETE FROM player_deck")
    suspend fun clearPlayerDeck()

//    @Query("SELECT * FROM deck WHERE id = :id")
//    fun getDrawnCardStream(id: Int): Flow<DrawnCard>

    @Query("SELECT * FROM player_deck")
    fun getPlayerDeckStream(): Flow<List<Card>>
}