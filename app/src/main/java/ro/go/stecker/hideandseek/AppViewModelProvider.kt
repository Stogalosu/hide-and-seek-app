package ro.go.stecker.hideandseek

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ro.go.stecker.hideandseek.data.HideAndSeekViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for HideAndSeekViewModel
        initializer {
            HideAndSeekViewModel(hideAndSeekApplication().container.deckRepository, hideAndSeekApplication().container.preferenceRepository)
        }
    }
}

fun CreationExtras.hideAndSeekApplication(): HideAndSeekApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as HideAndSeekApplication)