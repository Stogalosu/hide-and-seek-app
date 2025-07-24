package ro.go.stecker.hideandseek.data.firestore

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject


interface CloudRepo {
    fun createPlayer(player: Player)
    fun updatePlayerName(player: Player, newName: String)
    fun updatePlayerType(player: Player, newType: PlayerType)
    fun newGame(game: Game)
    fun addPlayerToGame(gameId: Int, player: Player, onSuccess: () -> Unit, onFail: () -> Unit)
    fun removePlayerFromGame(gameId: Int, player: Player)
    fun startGame(gameId: Int)
    fun isGameStarted(gameId: Int, onSuccess: (Boolean) -> Unit, onFail: () -> Unit)
    fun deleteGame(id: Int)
    fun addPlayerListener(gameId: Int, onChange: (List<Player>) -> Unit): ListenerRegistration
    fun addGameStartListener(gameId: Int, onChange: (Boolean) -> Unit): ListenerRegistration
}

class CloudRepository: CloudRepo {
    val db = Firebase.firestore

    override fun createPlayer(player: Player) {
        db.collection("players").document(player.uuid).set(player)
    }

    override fun updatePlayerName(player: Player, newName: String) {
        db.collection("players").document(player.uuid).update("name", newName)
    }

    override fun updatePlayerType(player: Player, newType: PlayerType) {
        db.collection("players").document(player.uuid).update("type", newType)
    }

    override fun newGame(game: Game) {
        db.collection("games").document(game.id.toString()).set(game)
    }

    override fun addPlayerToGame(gameId: Int, player: Player, onSuccess: () -> Unit, onFail: () -> Unit) {
        db.collection("games").document(gameId.toString()).update("players", FieldValue.arrayUnion(player))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFail() }
    }

    override fun removePlayerFromGame(gameId: Int, player: Player) {
        db.collection("games").document(gameId.toString()).update("players", FieldValue.arrayRemove(player))
            .addOnSuccessListener { Log.d("test", "SUCCESS") }
            .addOnFailureListener { Log.d("test", "FAIL") }
    }

    override fun startGame(gameId: Int) {
        db.collection("games").document(gameId.toString()).update("started", true)
    }

    override fun isGameStarted(gameId: Int, onSuccess: (Boolean) -> Unit, onFail: () -> Unit) {
        db.collection("games").document(gameId.toString()).get()
            .addOnSuccessListener { doc ->
                doc.toObject<Game>()?.started?.let { onSuccess(it) }
            }
            .addOnFailureListener { onFail() }
    }

    override fun deleteGame(id: Int) {
        db.collection("games").document(id.toString()).delete()
    }

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
}
