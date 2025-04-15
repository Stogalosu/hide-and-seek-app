package ro.go.stecker.hideandseek.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ro.go.stecker.hideandseek.data.Card
import ro.go.stecker.hideandseek.data.DrawnCard

@Dao
interface CardDao {
    /*
     *  Methods for "card_list" table
     */
    @Insert(entity = Card::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCardList(cardList: List<Card>)

    @Query("UPDATE card_list SET probability = :probability WHERE id = :id")
    suspend fun updateCardProbability(id: Int, probability: Int)

    @Query("DELETE FROM card_list")
    suspend fun clearCardList()

//    @Query("SELECT id FROM card_list ORDER BY id DESC LIMIT 1")
//    suspend fun getMaxId(): Int

    @Query("SELECT probability FROM card_list WHERE id = :id LIMIT 1")
    suspend fun getCardProbability(id: Int): Int

    @Query("SELECT * FROM card_list")
    fun getCardDeckStream(): Flow<List<Card>>

    /*
     *  Methods for "deck" table
     */
    @Insert(entity = DrawnCard::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDrawnCard(card: DrawnCard)

//    @Update(entity = DrawnCard::class)
//    suspend fun updateDrawnCard(card: DrawnCard)

    @Query("DELETE FROM deck WHERE cardId = :id")
    suspend fun deleteDrawnCard(id: Int)

    @Query("DELETE FROM deck")
    suspend fun clearDeck()

//    @Query("SELECT * FROM deck WHERE id = :id")
//    fun getDrawnCardStream(id: Int): Flow<DrawnCard>

    @Query("SELECT * FROM deck")
    fun getPlayerDeckStream(): Flow<List<DrawnCard>>
}