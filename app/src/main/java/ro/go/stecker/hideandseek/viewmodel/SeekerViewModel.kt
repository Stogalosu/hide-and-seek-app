package ro.go.stecker.hideandseek.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import ro.go.stecker.hideandseek.data.PreferencesRepository
import ro.go.stecker.hideandseek.data.UiState
import ro.go.stecker.hideandseek.data.firestore.CloudRepository

class SeekerViewModel(val preferencesRepository: PreferencesRepository, val cloudRepository: CloudRepository): ViewModel() {

    private val _uiState = MutableStateFlow(UiState())

    init {
        viewModelScope.launch {
            combine(
                preferencesRepository.gameState.shareIn(viewModelScope, SharingStarted.Eagerly),
                preferencesRepository.gameId.shareIn(viewModelScope, SharingStarted.Eagerly),
                preferencesRepository.player.shareIn(viewModelScope, SharingStarted.Eagerly)
            ) { gameState, gameId, playerName ->
                UiState(
                    gameState = gameState,
                    gameId = gameId,
                    player = playerName
                )
            }.collect { value -> _uiState.value = value }
        }
    }

    fun isGameStarted(onSuccess: (Boolean) -> Unit, onFail: () -> Unit) = cloudRepository.isGameStarted(_uiState.value.gameId, onSuccess, onFail)

    fun addGameStartListener(onChange: (Boolean) -> Unit) = cloudRepository.addGameStartListener(_uiState.value.gameId, onChange)

}