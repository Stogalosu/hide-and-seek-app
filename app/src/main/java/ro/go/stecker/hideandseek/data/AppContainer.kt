package ro.go.stecker.hideandseek.data

import android.content.Context
import ro.go.stecker.hideandseek.data.database.DeckRepository
import ro.go.stecker.hideandseek.data.database.HideAndSeekDatabase
import ro.go.stecker.hideandseek.data.database.OfflineDeckRepository

interface AppContainer {
    val deckRepository: DeckRepository
    val preferenceRepository: PreferencesRepository
}

class AppDataContainer(val context: Context): AppContainer {
    override val deckRepository: DeckRepository by lazy {
        OfflineDeckRepository(HideAndSeekDatabase.getDatabase(context).cardDao())
    }

    override val preferenceRepository: PreferencesRepository = PreferencesRepository(context)
}