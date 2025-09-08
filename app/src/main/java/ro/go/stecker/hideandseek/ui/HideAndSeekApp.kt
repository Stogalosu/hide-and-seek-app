package ro.go.stecker.hideandseek.ui

import android.content.ClipData
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ro.go.stecker.hideandseek.ui.navigation.HideAndSeekNavHost
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ro.go.stecker.hideandseek.R
import ro.go.stecker.hideandseek.data.UiState
import ro.go.stecker.hideandseek.data.firestore.Player
import ro.go.stecker.hideandseek.data.firestore.PlayerType
import ro.go.stecker.hideandseek.network.NetworkStatus
import ro.go.stecker.hideandseek.viewmodel.HideAndSeekViewModel
import ro.go.stecker.hideandseek.ui.navigation.HideAndSeekScreen
import ro.go.stecker.hideandseek.ui.screens.discardRed

@Composable
fun HideAndSeekApp(navController: NavHostController = rememberNavController()) {
    HideAndSeekNavHost(navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HideAndSeekTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = {},
    currentScreen: HideAndSeekScreen,
    doneButton: Boolean = false,
    onDoneButtonClick: () -> Unit = {},
    viewModel: HideAndSeekViewModel,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    TopAppBar(
        modifier = modifier,
        title = { Text(text = title, fontFamily = infraFontFamily) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onSecondary
        ),
        navigationIcon = {
            if(canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        },
        actions = {
            if(uiState.networkStatus == NetworkStatus.Unavailable)
                Icon(
                    painter = painterResource(R.drawable.ic_no_internet),
                    tint = discardRed,
                    contentDescription = stringResource(R.string.no_internet),
                    modifier = Modifier.padding(8.dp)
                )

            if(doneButton) {
                IconButton(
                    onClick = onDoneButtonClick
                ) {
                    Icon(Icons.Rounded.Check, contentDescription = stringResource(R.string.done))
                }
            }
            else TopAppBarDropdownMenu(currentScreen, { viewModel.exitGame() }, uiState, viewModel, snackbarHostState)
        }
    )
}

@Composable
fun TopAppBarDropdownMenu(
    currentScreen: HideAndSeekScreen,
    onEndGame: () -> Unit,
    uiState: UiState,
    viewModel: HideAndSeekViewModel,
    snackbarHostState: SnackbarHostState
) {
    var expanded by remember { mutableStateOf(false) }
    var confirmDialog by remember { mutableStateOf(false) }
    var playersDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val copiedCodeText = stringResource(R.string.copied_code)

    var players by remember { mutableStateOf(emptyList<Player>()) }

    if(currentScreen != HideAndSeekScreen.StartScreen) {
        IconButton(
            onClick = { expanded = !expanded }
        ) {
            Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.more_options))
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            val clipboard = LocalClipboard.current
            DropdownMenuItem(
                text = {
                    Column {
                        Text(stringResource(R.string.access_code))
                        Text(text = uiState.gameId.toString())
                    }
                },
                onClick = {
                    val clip = ClipData.newPlainText("Access code", uiState.gameId.toString())
                    clipboard.nativeClipboard.setPrimaryClip(clip)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(copiedCodeText)
                    }
                },
                enabled = currentScreen == HideAndSeekScreen.MainScreen
            )

            DropdownMenuItem(
                text = { Text(stringResource(R.string.players)) },
                leadingIcon = { Icon(imageVector = Icons.Rounded.Person, contentDescription = null) },
                onClick = {
                    if(uiState.networkStatus == NetworkStatus.Available) {
                        viewModel.getAllPlayersInGame {
                            players = it
                            expanded = false
                            playersDialog = true
                        }
                    }
                }
            )

            DropdownMenuItem(
                text = {
                    Text(
                        text =
                            if(uiState.player.type == PlayerType.Hider) stringResource(R.string.end_game)
                            else stringResource(R.string.exit_game)
                    )
                },
                leadingIcon = { Icon(Icons.Rounded.Close, contentDescription = null) },
                onClick = {
                    expanded = false
                    confirmDialog = true
                }
            )
        }
    }

    if(playersDialog) {
        AlertDialog(
            onDismissRequest = { playersDialog = false },
            title = { Text(stringResource(R.string.players)) },
            text = {
               Column(
                   verticalArrangement = Arrangement.spacedBy(12.dp)
               ) {
                   players.forEach { player ->
                       Row(
                           verticalAlignment = Alignment.CenterVertically,
                           modifier = Modifier.fillMaxWidth()
                       ) {
                           Icon(imageVector = Icons.Rounded.Person, contentDescription = null)
                           Spacer(modifier = Modifier.width(4.dp))
                           Text(
                               text = player.name,
                               fontWeight = FontWeight.Bold
                           )
                           Spacer(modifier = Modifier.weight(1f))
                           Text(player.type.name)
                       }
                   }
               }
            },
            confirmButton = {
                TextButton(
                    onClick = { playersDialog = false }
                ) {
                    Text(stringResource(R.string.close))
                }
            }
        )
    }

    if(confirmDialog) {
        AlertDialog(
            onDismissRequest = { confirmDialog = false },
            icon = { Icon(Icons.Rounded.Close, contentDescription = null) },
            title = {
                if(uiState.player.type == PlayerType.Hider) Text(stringResource(R.string.end_game))
                else Text(stringResource(R.string.exit_game))
            },
            text = {
                if(uiState.player.type == PlayerType.Hider) Text(stringResource(R.string.end_game_question))
                else Text(stringResource(R.string.exit_game_question))
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    onClick = {
                        confirmDialog = false
                        onEndGame()
                    }
                ) {
                    if(uiState.player.type == PlayerType.Hider) Text(stringResource(R.string.end_game))
                    else Text(stringResource(R.string.exit_game))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { confirmDialog = false }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

//@Preview
//@Composable
//fun TopAppBarPreview() {
//    HideAndSeekTopAppBar("Hide and Seek", false, {}, HideAndSeekScreen.StartScreen, viewModel(factory = AppViewModelProvider.Factory))
//}