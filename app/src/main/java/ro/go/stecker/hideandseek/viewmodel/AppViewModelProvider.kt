package ro.go.stecker.hideandseek.viewmodel

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ro.go.stecker.hideandseek.HideAndSeekApplication

object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for HideAndSeekViewModel
        initializer {
            HideAndSeekViewModel(hideAndSeekApplication().container.deckRepository, hideAndSeekApplication().container.preferencesRepository, hideAndSeekApplication().container.cloudRepository, hideAndSeekApplication().container.connectivityObserver)
        }

        initializer {
            HiderViewModel(hideAndSeekApplication().container.deckRepository, hideAndSeekApplication().container.preferencesRepository, hideAndSeekApplication().container.cloudRepository)
        }

        initializer {
            SeekerViewModel(hideAndSeekApplication().container.preferencesRepository, hideAndSeekApplication().container.cloudRepository)
        }
    }
}

fun CreationExtras.hideAndSeekApplication(): HideAndSeekApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as HideAndSeekApplication)