package ro.go.stecker.hideandseek.data.firestore

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import ro.go.stecker.hideandseek.data.Card
import ro.go.stecker.hideandseek.data.SentCard
import ro.go.stecker.hideandseek.data.toSentCard


interface CloudRepo {
    fun createPlayer(player: Player)
    fun updatePlayerName(player: Player, newName: String)
    fun updatePlayerType(player: Player, newType: PlayerType)
    fun getAllPlayersInGame(gameId: Int, onSuccess: (List<Player>) -> Unit)
    fun newGame(game: Game)
    fun addPlayerToGame(gameId: Int, player: Player, onDone: (Boolean) -> Unit)
    fun removePlayerFromGame(gameId: Int, player: Player)
    fun updateGameState(gameId: Int, started: Boolean)
    fun isGameStarted(gameId: Int, onSuccess: (Boolean) -> Unit, onFailure: () -> Unit)
    fun deleteGame(id: Int)
    fun playCard(gameId: Int, sender: Player, card: Card, onDone: (Boolean) -> Unit)
    fun dismissCurse(uuid: String)
    fun addPlayerListener(gameId: Int, onChange: (List<Player>) -> Unit): ListenerRegistration
    fun addGameStartListener(gameId: Int, onChange: (Boolean) -> Unit): ListenerRegistration
    fun addCardListener(gameId: Int, onChange: (List<SentCard>) -> Unit): ListenerRegistration
}

class CloudRepository: CloudRepo {
    val db = Firebase.firestore

    /*
        Player methods
     */

    override fun createPlayer(player: Player) {
        db.collection("players").document(player.uuid).set(player)
    }

    override fun updatePlayerName(player: Player, newName: String) {
        db.collection("players").document(player.uuid).update("name", newName)
    }

    override fun updatePlayerType(player: Player, newType: PlayerType) {
        db.collection("players").document(player.uuid).update("type", newType)
    }

    override fun getAllPlayersInGame(gameId: Int, onSuccess: (List<Player>) -> Unit) {
        db.collection("games").document(gameId.toString()).get()
            .addOnSuccessListener { doc ->
                doc.toObject<Game>()?.players?.let { onSuccess(it) }
            }
    }

    /*
        Game methods
     */

    override fun newGame(game: Game) {
        db.collection("games").document(game.id.toString()).set(game)
    }

    override fun addPlayerToGame(gameId: Int, player: Player, onDone: (Boolean) -> Unit) {
        db.collection("games").document(gameId.toString()).update("players", FieldValue.arrayUnion(player))
            .addOnSuccessListener { onDone(true) }
            .addOnFailureListener { onDone(false) }
    }

    override fun removePlayerFromGame(gameId: Int, player: Player) {
        db.collection("games").document(gameId.toString()).update("players", FieldValue.arrayRemove(player))
    }

    override fun updateGameState(gameId: Int, started: Boolean) {
        db.collection("games").document(gameId.toString()).update("started", started)
    }

    override fun isGameStarted(gameId: Int, onSuccess: (Boolean) -> Unit, onFailure: () -> Unit) {
        db.collection("games").document(gameId.toString()).get()
            .addOnSuccessListener { doc ->
                doc.toObject<Game>()?.started?.let { onSuccess(it) }
            }
            .addOnFailureListener { onFailure() }
    }

    override fun deleteGame(id: Int) {
        db.collection("games").document(id.toString()).delete()

        db.collection("cards").whereEqualTo("gameId", id).get()
            .addOnSuccessListener { docs ->
                val cards = docs.toObjects<SentCard>().toList()
                cards.forEach {
                    db.collection("cards").document(it.uuid).delete()
                }
            }

    }

    /*
        Cards methods
     */

    override fun playCard(
        gameId: Int,
        sender: Player,
        card: Card,
        onDone: (Boolean) -> Unit
    ) {
        val updates = hashMapOf<String, Any>(
            "playedAt" to FieldValue.serverTimestamp(),
        )

        db.collection("cards").document(card.uuid).set(card.toSentCard(gameId, sender))
            .addOnSuccessListener {
                db.collection("cards").document(card.uuid).update(updates)
                    .addOnSuccessListener { onDone(true) }
                    .addOnFailureListener { onDone(false) }
            }
            .addOnFailureListener { onDone(false) }
            .addOnCanceledListener { onDone(false) }
    }

    override fun dismissCurse(uuid: String) {
        db.collection("cards").document(uuid).delete()
    }

    /*
        Listener methods
     */

    override fun addPlayerListener(gameId: Int, onChange: (List<Player>) -> Unit): ListenerRegistration {
        return db.collection("games").document(gameId.toString()).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                snapshot.toObject<Game>()?.players?.let { onChange(it) }
            }
        }
    }

    override fun addGameStartListener(gameId: Int, onChange: (Boolean) -> Unit): ListenerRegistration {
        return db.collection("games").document(gameId.toString()).addSnapshotListener { snapshot, e ->
            if(e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if(snapshot != null && snapshot.exists()) {
                snapshot.toObject<Game>()?.started?.let { onChange(it) }
            }
        }
    }

    override fun addCardListener(gameId: Int, onChange: (List<SentCard>) -> Unit): ListenerRegistration {
        return db.collection("cards")
            .whereEqualTo("gameId", gameId)
            .addSnapshotListener { value, e ->
                if(e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                onChange(value!!.toObjects<SentCard>())
            }
    }
}
