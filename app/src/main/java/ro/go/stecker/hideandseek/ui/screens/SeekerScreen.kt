package ro.go.stecker.hideandseek.ui.screens

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import ro.go.stecker.hideandseek.ui.HideAndSeekTopAppBar
import ro.go.stecker.hideandseek.ui.navigation.HideAndSeekScreen
import ro.go.stecker.hideandseek.viewmodel.HideAndSeekViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import ro.go.stecker.hideandseek.R
import ro.go.stecker.hideandseek.data.Card
import ro.go.stecker.hideandseek.data.SeekerUiState
import ro.go.stecker.hideandseek.data.toCard
import ro.go.stecker.hideandseek.ui.ButtonWithIcon
import ro.go.stecker.hideandseek.ui.CardItem
import ro.go.stecker.hideandseek.viewmodel.SeekerViewModel

var isGameStarted by mutableStateOf(false)
var dismissCurseDialog by mutableStateOf(false)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SeekerScreen(
    onDetailsClick: (String) -> Unit,
    viewModel: HideAndSeekViewModel,
    seekerViewModel: SeekerViewModel,
    seekerUiState: SeekerUiState,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    snackbarHostState: SnackbarHostState
) {

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            HideAndSeekTopAppBar(
                title = "Seeker",
                canNavigateBack = false,
                currentScreen = HideAndSeekScreen.MainScreen,
                viewModel = viewModel,
                snackbarHostState = snackbarHostState
            )
        }
    ) { contentPadding ->

        LaunchedEffect(Unit) {
            seekerViewModel.isGameStarted(
                onSuccess = { isGameStarted = it },
                onFail = {}
            )
        }

        if(!isGameStarted) {
            LaunchedEffect(Unit) {
                seekerViewModel.addGameStartListener(onChange = { isGameStarted = it })
            }

            Dialog(onDismissRequest = {}) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            CircularProgressIndicator()
                            Text(stringResource(R.string.waiting_to_start_game))
                        }
                        TextButton(
                            onClick = { viewModel.exitGame() }
                        ) {
                            Text(
                                text = stringResource(R.string.exit_game),
                                color = discardRed
                            )
                        }
                    }
                }
            }
        } else {
            LaunchedEffect(Unit) { seekerViewModel.addCardListener() }

            if(dismissCurseDialog) {
                AlertDialog(
                    onDismissRequest = { dismissCurseDialog = false },
                    icon = { Icon(painterResource(R.drawable.ic_check_circle), contentDescription = null) },
                    title = { Text(stringResource(R.string.dismiss_curse)) },
                    text = { Text(stringResource(R.string.dismiss_curse_question)) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                seekerViewModel.dismissCurse(seekerUiState.tempUuid)
                                dismissCurseDialog = false
                            }
                        ) {
                            Text(
                                text = stringResource(R.string.dismiss),
                                color = confirmGreen
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { dismissCurseDialog = false }) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                )
            }

            if(seekerUiState.curses.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 380.dp),
                    modifier = Modifier
                        .padding(4.dp)
                        .padding(contentPadding)
                        .fillMaxSize()
                ) {
                    items(items = seekerUiState.curses, key = { it.uuid }) { sentCard ->
                        Box(modifier = Modifier.animateItem()) {
                            CardItem(
                                card = sentCard.toCard(),
                                buttons = { CardItemButtons(sentCard.toCard(), onDetailsClick, seekerViewModel) },
                                onDetailsClick = onDetailsClick,
                                sharedTransitionScope = sharedTransitionScope,
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                        }
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = stringResource(R.string.no_active_curses),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }

            }
        }
    }
}

@Composable
private fun CardItemButtons(
    card: Card,
    onDetailsClick: (String) -> Unit,
    seekerViewModel: SeekerViewModel
) {
    Column(
        modifier = Modifier.padding(32.dp)
    ) {
        ButtonWithIcon(
            icon = painterResource(R.drawable.ic_check_circle),
            text = R.string.dismiss,
            color = confirmGreen,
            iconSize = 25,
            onClick = {
                seekerViewModel.setUuidToDismiss(card.uuid)
                dismissCurseDialog = true
            }
        )
        ButtonWithIcon(
            icon = Icons.Rounded.Info,
            text = R.string.details,
            color = Color(207, 207, 207),
            iconSize = 25,
            onClick = {
                onDetailsClick(card.uuid)
            }
        )
    }
}