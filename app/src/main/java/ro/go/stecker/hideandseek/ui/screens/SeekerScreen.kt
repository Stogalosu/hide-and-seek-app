package ro.go.stecker.hideandseek.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import ro.go.stecker.hideandseek.ui.HideAndSeekTopAppBar
import ro.go.stecker.hideandseek.ui.navigation.HideAndSeekScreen
import ro.go.stecker.hideandseek.viewmodel.HideAndSeekViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ro.go.stecker.hideandseek.R
import ro.go.stecker.hideandseek.viewmodel.SeekerViewModel

@Composable
fun SeekerScreen(
    seekerViewModel: SeekerViewModel,
    viewModel: HideAndSeekViewModel,
    snackbarHostState: SnackbarHostState
) {
    var isGameStarted by remember { mutableStateOf(false) }

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
    ) { innerPaddings ->
        val i = innerPaddings

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
        }
    }
}