package ro.go.stecker.hideandseek.data

import android.content.Context
import ro.go.stecker.hideandseek.data.database.DeckRepository
import ro.go.stecker.hideandseek.data.database.HideAndSeekDatabase
import ro.go.stecker.hideandseek.data.database.OfflineDeckRepository
import ro.go.stecker.hideandseek.data.firestore.CloudRepository
import ro.go.stecker.hideandseek.network.NetworkConnectivityObserver

interface AppContainer {
    val deckRepository: DeckRepository
    val preferencesRepository: PreferencesRepository
    val cloudRepository: CloudRepository
    val connectivityObserver: NetworkConnectivityObserver
}

class AppDataContainer(val context: Context): AppContainer {
    override val deckRepository: DeckRepository by lazy {
        OfflineDeckRepository(HideAndSeekDatabase.getDatabase(context).cardDao())
    }

    override val preferencesRepository: PreferencesRepository = PreferencesRepository(context)

    override val cloudRepository: CloudRepository = CloudRepository()

    override val connectivityObserver: NetworkConnectivityObserver = NetworkConnectivityObserver(context)
}