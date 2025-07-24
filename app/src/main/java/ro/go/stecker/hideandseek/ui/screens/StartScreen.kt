package ro.go.stecker.hideandseek.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ro.go.stecker.hideandseek.viewmodel.AppViewModelProvider
import ro.go.stecker.hideandseek.R
import ro.go.stecker.hideandseek.data.UiState
import ro.go.stecker.hideandseek.data.firestore.Player
import ro.go.stecker.hideandseek.data.firestore.PlayerType
import ro.go.stecker.hideandseek.getActivity
import ro.go.stecker.hideandseek.network.NetworkStatus
import ro.go.stecker.hideandseek.ui.HideAndSeekTopAppBar
import ro.go.stecker.hideandseek.ui.infraFontFamily
import ro.go.stecker.hideandseek.ui.navigation.HideAndSeekScreen
import ro.go.stecker.hideandseek.viewmodel.HideAndSeekViewModel

@Composable
fun StartScreen(
    onGameStart: () -> Unit,
    viewModel: HideAndSeekViewModel,
    uiState: UiState,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var dismissDialog by remember { mutableStateOf(false) }
    var newGameDialog by remember { mutableStateOf(false) }
    var tempPlayerType by remember { mutableStateOf(PlayerType.NotSet) }

    var listener = ListenerRegistration {}

    BackHandler() {
        context.getActivity()?.finish()
    }


    if(dismissDialog) {
        AlertDialog(
            onDismissRequest = { dismissDialog = false },
            title = { Text(stringResource(R.string.abandon_game), ) },
            text = { Text(stringResource(R.string.abandon_game_dialog)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        dismissDialog = false
                        newGameDialog = false
                        listener.remove()
                        viewModel.exitGame(tempPlayerType)
                    }
                ) { Text(stringResource(R.string.confirm), color = discardRed) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        dismissDialog = false
                    }
                ) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    if(newGameDialog) {
        if(uiState.player.name.isEmpty()) {
            // Show dialog for entering player name
            var name by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { newGameDialog = false },
                title = {
                    Text(
                        text = stringResource(R.string.you_need_a_name, name),
                        fontSize = 18.sp
                    )
                },
                text = {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(stringResource(R.string.enter_your_name_here)) },
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(
                        enabled = !name.isEmpty(),
                        onClick = {
                            viewModel.createPlayer(
                                name = name,
                                type = tempPlayerType
                            )
                        }
                    ) {
                        Text(stringResource(R.string.done))
                    }
                }
            )
        } else {
            // Show new game dialog

            var players: List<Player> by remember { mutableStateOf(emptyList()) }

            LaunchedEffect(Unit) {
                if(tempPlayerType == PlayerType.Hider) {
                    val id = viewModel.newGame(tempPlayerType)
                    listener = viewModel.addPlayerListener(
                        gameId = id,
                        onChange = { it ->
                            val newPlayers = it.toMutableList()
                            newPlayers.removeIf { item -> item.uuid == uiState.player.uuid }
                            players = newPlayers
                        }
                    )
                }
            }

            Dialog(
                onDismissRequest = {
                    if(tempPlayerType == PlayerType.Hider) dismissDialog = true
                    else newGameDialog = false
                }
            ) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth()
                    ) {
                        //New game dialog
                        if (tempPlayerType == PlayerType.Hider) {
                            Text(
                                text = stringResource(R.string.new_game),
                                textAlign = TextAlign.Center,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                            )
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(stringResource(R.string.access_code))
                                Text(
                                    text = uiState.gameId.toString(),
                                    fontSize = 32.sp
                                )
                            }

                            Text(text = stringResource(R.string.new_game_text))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                Text(stringResource(R.string.waiting_for_players))
                            }

                            players.forEach {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(stringResource(R.string.player_joined, it.name))
                                }
                            }

                            DialogButtons(
                                dismissText = stringResource(R.string.cancel),
                                onDismissClick = { dismissDialog = true },
                                confirmText = stringResource(R.string.start),
                                confirmEnabled = players.isNotEmpty(),
                                onConfirmClick = {
                                    coroutineScope.launch {
                                        listener.remove()
                                        viewModel.startGame()
                                        viewModel.initAtGameStart(tempPlayerType)
                                        onGameStart()
                                    }
                                }
                            )
                        }

                        //Join game dialog
                        if (tempPlayerType == PlayerType.Seeker) {
                            var gameId by remember { mutableStateOf("") }
                            var gameNotFound by remember { mutableStateOf(false) }
                            val isCodeInvalid = !gameId.isDigitsOnly() || gameId.length != 6

                            Column(
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(R.string.join_game),
                                    textAlign = TextAlign.Center,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = gameId,
                                    onValueChange = {
                                        gameId = it
                                        gameNotFound = false
                                    },
                                    label = { Text(stringResource(R.string.enter_access_code)) },
                                    isError = isCodeInvalid && !gameId.isEmpty(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                )
                                if (gameNotFound) {
                                    Text(
                                        text = stringResource(R.string.couldnt_find_game),
                                        fontSize = 14.sp,
                                        color = discardRed
                                    )
                                }
                                DialogButtons(
                                    dismissText = stringResource(R.string.cancel),
                                    onDismissClick = { newGameDialog = false },
                                    confirmText = stringResource(R.string.join),
                                    confirmEnabled = !isCodeInvalid,
                                    onConfirmClick = {
                                        viewModel.joinGame(
                                            gameId = gameId.toInt(),
                                            playerType = tempPlayerType,
                                            onSuccess = {
                                                coroutineScope.launch {
                                                    newGameDialog = false
                                                    viewModel.initAtGameStart(tempPlayerType)
                                                    onGameStart()
                                                }
                                            },
                                            onFail = { gameNotFound = true }
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            HideAndSeekTopAppBar(
                title = stringResource(R.string.app_name),
                canNavigateBack = false,
                currentScreen = HideAndSeekScreen.StartScreen,
                viewModel = viewModel
            )
        }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = stringResource(R.string.welcome_1),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 32.dp)
            )
            Text(
                text = stringResource(R.string.welcome_2),
                textAlign = TextAlign.Justify,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if(uiState.networkStatus == NetworkStatus.Available)
                            coroutineScope.launch {
                                delay(200)
                                tempPlayerType = PlayerType.Hider
                                newGameDialog = true
                            }
                    }
                ) {
                    Text(stringResource(R.string.im_a_hider), fontFamily = infraFontFamily)
                }

                Button(
                    onClick = {
                        if(uiState.networkStatus == NetworkStatus.Available)
                            coroutineScope.launch {
                                delay(200)
                                tempPlayerType = PlayerType.Seeker
                                newGameDialog = true
                            }
                    }
                ) {
                    Text(stringResource(R.string.im_a_seeker), fontFamily = infraFontFamily)
                }
            }
            Spacer(modifier = Modifier.height(64.dp))
        }

    }
}

@Composable
private fun DialogButtons(
    dismissText: String,
    onDismissClick: () -> Unit,
    confirmText: String,
    confirmEnabled: Boolean,
    onConfirmClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextButton(onClick = onDismissClick) {
            Text(text = dismissText, color = discardRed)
        }

        Button(
            enabled = confirmEnabled,
            onClick = onConfirmClick
        ) {
            Text(text = confirmText)
        }
    }
}

@Composable
@Preview
fun StartScreenPreview() {
    StartScreen({}, viewModel(factory = AppViewModelProvider.Factory), UiState())
}