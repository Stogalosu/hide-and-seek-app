package ro.go.stecker.hideandseek.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ro.go.stecker.hideandseek.data.CardsRepository
import ro.go.stecker.hideandseek.data.GameState
import ro.go.stecker.hideandseek.data.PreferencesRepository
import ro.go.stecker.hideandseek.data.UiState
import ro.go.stecker.hideandseek.data.database.DeckRepository
import ro.go.stecker.hideandseek.data.firestore.CloudRepository
import ro.go.stecker.hideandseek.data.firestore.Game
import ro.go.stecker.hideandseek.data.firestore.Player
import ro.go.stecker.hideandseek.data.firestore.PlayerType
import ro.go.stecker.hideandseek.data.firestore.toGameState
import ro.go.stecker.hideandseek.network.NetworkConnectivityObserver
import kotlin.random.Random.Default.nextInt
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class HideAndSeekViewModel(
    private val deckRepository: DeckRepository,
    private val preferencesRepository: PreferencesRepository,
    private val cloudRepository: CloudRepository,
    private val connectivityObserver: NetworkConnectivityObserver
): ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                preferencesRepository.gameState,
                preferencesRepository.gameId,
                preferencesRepository.player,
                connectivityObserver.observe()
            ) { gameState, gameId, playerName, networkStatus ->
                UiState(
                    gameState = gameState,
                    gameId = gameId,
                    player = playerName,
                    networkStatus = networkStatus
                )
            }.collect { value -> _uiState.value = value }
        }
    }

    /*
        Game state methods
     */

    suspend fun initAtGameStart(playerType: PlayerType) {
        if(uiState.value.gameState == GameState.NotStarted) {
            deckRepository.clearDeck()
            preferencesRepository.startGame(playerType.toGameState())
        }

        cloudRepository.updatePlayerType(_uiState.value.player, playerType)

        deckRepository.setCardDeck(CardsRepository)
    }

    fun exitGame(playerType: PlayerType = PlayerType.NotSet) {
        val topic =
            when (_uiState.value.gameState) {
                GameState.Hider -> _uiState.value.gameId.toString() + "-hider"
                GameState.Seeker -> _uiState.value.gameId.toString() + "-seeker"
                else -> ""
            }
        Firebase.messaging.unsubscribeFromTopic(topic)

        viewModelScope.launch {
            preferencesRepository.endGame()
        }

        val type =
            if(playerType != PlayerType.NotSet) playerType
            else _uiState.value.player.type


        cloudRepository.getAllPlayersInGame(
            _uiState.value.gameId,
            onSuccess = {
                if(it.size <= 1) cloudRepository.deleteGame(_uiState.value.gameId)
                else {
                    cloudRepository.removePlayerFromGame(_uiState.value.gameId, _uiState.value.player)
                    if(type == PlayerType.Hider)
                        cloudRepository.updateGameState(_uiState.value.gameId, false)
                }
            }
        )
    }

    /*
        Cloud database methods
     */

    @OptIn(ExperimentalUuidApi::class)
    fun createPlayer(name: String, type: PlayerType) {
        val newPlayer = Player(uuid = Uuid.random().toString(), name = name, type = type)

        viewModelScope.launch {
            preferencesRepository.updatePlayer(newPlayer.name, newPlayer.uuid)
        }

        cloudRepository.createPlayer(newPlayer)
    }

    fun updatePlayerName(name: String) {
        viewModelScope.launch {
            preferencesRepository.updatePlayer(name)
        }

        cloudRepository.updatePlayerName(_uiState.value.player, name)
    }

    fun getAllPlayersInGame(onSuccess: (List<Player>) -> Unit) = cloudRepository.getAllPlayersInGame(gameId = _uiState.value.gameId, onSuccess = onSuccess)

    fun newGame(playerType: PlayerType): Int {
        val id = nextInt(100000, 999999)
        val player = _uiState.value.player.copy(type = playerType)
        val newGame = Game(id = id, players = listOf(player))

        viewModelScope.launch {
            preferencesRepository.updateGameId(id)
        }

        cloudRepository.newGame(newGame)

        return id
    }

    fun joinGame(gameId: Int, playerType: PlayerType, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            preferencesRepository.updateGameId(gameId)
        }

        val player = Player(
            uuid = _uiState.value.player.uuid,
            name = _uiState.value.player.name,
            type = playerType
        )

        cloudRepository.addPlayerToGame(gameId = gameId, player = player, onDone = onDone)
    }

    fun addPlayerListener(gameId: Int, onChange: (List<Player>) -> Unit) = cloudRepository.addPlayerListener(gameId, onChange)

    fun startGame() = cloudRepository.updateGameState(_uiState.value.gameId, true)
}