package ro.go.stecker.hideandseek.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")

enum class GameState() {
    Started, NotStarted, Loading
}

fun Boolean.toGameState(): GameState {
    return if(this == true) GameState.Started else GameState.NotStarted
}

interface DataStoreRepository {
    suspend fun startGame()
    suspend fun endGame()
    val isGameStarted: Flow<GameState>
}

class PreferencesRepository(val context: Context): DataStoreRepository {
    val GAME_STARTED = booleanPreferencesKey("game_started")

    override suspend fun startGame() {
        context.dataStore.edit { preferences ->
            preferences[GAME_STARTED] = true
        }
    }

    override suspend fun endGame() {
        context.dataStore.edit { preferences ->
            preferences[GAME_STARTED] = false
        }
    }

    override val isGameStarted: Flow<GameState> = context.dataStore.data
        .map { preferences ->
            (preferences[GAME_STARTED] ?: false).toGameState()
        }
}