package ro.go.stecker.hideandseek.data.database

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ro.go.stecker.hideandseek.data.Card
import ro.go.stecker.hideandseek.data.DrawnCard
import ro.go.stecker.hideandseek.data.toCard

interface DeckRepository {
    suspend fun setCardList(cardList: List<Card>)
    suspend fun updateCardProbability(card: Card)
    fun getCardDeckStream(): Flow<List<Card>>

    suspend fun insertDrawnCard(card: DrawnCard)
    suspend fun deleteDrawnCard(id: Int)
    suspend fun clearDeck()
//    fun getDrawnCardStream(id: Int): Flow<DrawnCard>
    fun getPlayerDeckStream(): Flow<List<Card>>
}

class OfflineDeckRepository(private val cardDao: CardDao): DeckRepository {
    /*
     *  Methods for "card_list" table
     */
    override suspend fun setCardList(cardList: List<Card>) {
        cardDao.clearCardList()
        cardDao.insertCardList(cardList)
    }

    override suspend fun updateCardProbability(card: Card) {
        val cardId = card.id
        val cardProbability = cardDao.getCardProbability(cardId)

        cardDao.updateCardProbability(cardId, cardProbability - 1)
    }

    override fun getCardDeckStream(): Flow<List<Card>> = cardDao.getCardDeckStream()

    /*
     *  Methods for "deck" table
     */
    override suspend fun insertDrawnCard(card: DrawnCard) = cardDao.insertDrawnCard(card)

    override suspend fun deleteDrawnCard(id: Int) = cardDao.deleteDrawnCard(id)

    override suspend fun clearDeck() = cardDao.clearDeck()

//    override fun getDrawnCardStream(id: Int): Flow<DrawnCard> = cardDao.getDrawnCardStream(id)

    override fun getPlayerDeckStream(): Flow<List<Card>> =
        cardDao.getPlayerDeckStream().map { drawnCardsList ->
            drawnCardsList.map { drawnCard ->
                drawnCard.toCard()
            }
        }
}