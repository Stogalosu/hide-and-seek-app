package ro.go.stecker.hideandseek.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ro.go.stecker.hideandseek.data.firestore.Player
import ro.go.stecker.hideandseek.data.firestore.PlayerType

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")

enum class GameState() {
    Hider, Seeker,  NotStarted, Loading
}

fun String.toGameState(): GameState {
    return when(this) {
        "Hider" -> GameState.Hider
        "Seeker" -> GameState.Seeker
        "NotStarted" -> GameState.NotStarted
        "Loading" -> GameState.Loading

        else -> GameState.NotStarted
    }
}

fun GameState.playerType(): PlayerType {
    return when(this) {
        GameState.Hider -> PlayerType.Hider
        GameState.Seeker -> PlayerType.Seeker
        else -> PlayerType.NotSet
    }
}

interface DataStoreRepository {
    suspend fun startGame(type: GameState)
    suspend fun endGame()
    suspend fun updateGameId(newId: Int)
    suspend fun updatePlayer(name: String = "", uuid: String = "")
    val gameId: Flow<Int>
    val gameState: Flow<GameState>
    val player: Flow<Player>
}

class PreferencesRepository(val context: Context): DataStoreRepository {
    private val GAME_STATE = stringPreferencesKey("game_state")
    private val GAME_ID = intPreferencesKey("game_id")
    private val PLAYER_NAME = stringPreferencesKey("player_name")
    private val PLAYER_UUID = stringPreferencesKey("player_uuid")

    override suspend fun startGame(type: GameState) {
        context.dataStore.edit { preferences ->
            preferences[GAME_STATE] = type.name
        }
    }

    override suspend fun endGame() {
        context.dataStore.edit { preferences ->
            preferences[GAME_STATE] = GameState.NotStarted.name
            preferences[GAME_ID] = 0
        }
    }

    override suspend fun updateGameId(newId: Int) {
        context.dataStore.edit { preferences ->
            preferences[GAME_ID] = newId
        }
    }

    override suspend fun updatePlayer(name: String, uuid: String) {
        context.dataStore.edit { preferences ->
            if(!name.isEmpty()) preferences[PLAYER_NAME] = name
            if(!uuid.isEmpty()) preferences[PLAYER_UUID] = uuid
        }
    }

    override val gameState: Flow<GameState> = context.dataStore.data
        .map { preferences ->
            (preferences[GAME_STATE] ?: GameState.NotStarted.name).toGameState()
        }

    override val gameId: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[GAME_ID] ?: 0
        }

    override val player: Flow<Player> = context.dataStore.data
        .map { preferences ->
            Player(
                uuid = preferences[PLAYER_UUID] ?: "",
                name = preferences[PLAYER_NAME] ?: "",
                type = (preferences[GAME_STATE] ?: GameState.NotStarted.name).toGameState().playerType())
        }
}