package ro.go.stecker.hideandseek.data.database

import kotlinx.coroutines.flow.Flow
import ro.go.stecker.hideandseek.data.Card
import ro.go.stecker.hideandseek.data.CardDetails

interface DeckRepository {
    suspend fun setCardDeck(cardList: List<CardDetails>)
    suspend fun updateCardProbability(card: CardDetails)
    fun getCardDeckStream(): Flow<List<CardDetails>>

    suspend fun insertCard(card: Card)
    suspend fun deleteCard(uuid: String)
    suspend fun clearDeck()
//    fun getDrawnCardStream(id: Int): Flow<DrawnCard>
    fun getPlayerDeckStream(): Flow<List<Card>>
}

class OfflineDeckRepository(private val cardDao: CardDao): DeckRepository {
    /*
     *  Methods for "card_list" table
     */
    override suspend fun setCardDeck(cardList: List<CardDetails>) {
        cardDao.clearCardDeck()
        cardDao.insertCardDeck(cardList)
    }

    override suspend fun updateCardProbability(card: CardDetails) {
        val cardId = card.id
        val cardProbability = cardDao.getCardProbability(cardId)

        cardDao.updateCardProbability(cardId, cardProbability - 1)
    }

    override fun getCardDeckStream(): Flow<List<CardDetails>> = cardDao.getCardDeckStream()

    /*
     *  Methods for "deck" table
     */
    override suspend fun insertCard(card: Card) = cardDao.insertCard(card)

    override suspend fun deleteCard(uuid: String) = cardDao.deleteCard(uuid)

    override suspend fun clearDeck() = cardDao.clearPlayerDeck()

//    override fun getDrawnCardStream(id: Int): Flow<DrawnCard> = cardDao.getDrawnCardStream(id)

    override fun getPlayerDeckStream(): Flow<List<Card>> = cardDao.getPlayerDeckStream()
}