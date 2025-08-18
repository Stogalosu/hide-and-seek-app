package ro.go.stecker.hideandseek.ui.screens

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import ro.go.stecker.hideandseek.data.GameState
import ro.go.stecker.hideandseek.data.HiderUiState
import ro.go.stecker.hideandseek.data.SeekerUiState
import ro.go.stecker.hideandseek.data.UiState
import ro.go.stecker.hideandseek.viewmodel.HideAndSeekViewModel
import ro.go.stecker.hideandseek.viewmodel.HiderViewModel
import ro.go.stecker.hideandseek.viewmodel.SeekerViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainScreen(
    onDrawCards: () -> Unit,
    onNavigateToStartScreen: () -> Unit,
    onDetailsClick: (String) -> Unit,
    viewModel: HideAndSeekViewModel,
    hiderViewModel: HiderViewModel,
    seekerViewModel: SeekerViewModel,
    hiderUiState: HiderUiState,
    seekerUiState: SeekerUiState,
    uiState: UiState,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    snackbarHostState: SnackbarHostState
) {
    LaunchedEffect(uiState.gameState) {
        if(uiState.gameState == GameState.NotStarted)
            onNavigateToStartScreen()
    }

    when(uiState.gameState) {
        GameState.Hider -> {
            HiderDeckScreen(
                onDrawCards = onDrawCards,
                onDetailsClick = onDetailsClick,
                viewModel = viewModel,
                hiderViewModel = hiderViewModel,
                uiState = uiState,
                hiderUiState = hiderUiState,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                snackbarHostState = snackbarHostState
            )
        }

        GameState.Seeker -> {
            SeekerScreen(
                onDetailsClick = onDetailsClick,
                viewModel = viewModel,
                seekerViewModel = seekerViewModel,
                seekerUiState = seekerUiState,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                snackbarHostState = snackbarHostState
            )
        }

        else -> {
            LoadingScreen()
        }
    }
}