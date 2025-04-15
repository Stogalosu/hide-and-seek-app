package ro.go.stecker.hideandseek.ui.navigation

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import ro.go.stecker.hideandseek.AppViewModelProvider
import ro.go.stecker.hideandseek.R
import ro.go.stecker.hideandseek.data.HideAndSeekViewModel
import ro.go.stecker.hideandseek.ui.DrawCardsScreen
import ro.go.stecker.hideandseek.ui.HiderDeckScreen
import ro.go.stecker.hideandseek.ui.StartScreen

enum class HideAndSeekScreen(@StringRes val title: Int) {
    StartScreen(R.string.app_name),
    LoadingScreen(R.string.loading),
    HiderDeck(R.string.hider_deck),
    DrawCards(R.string.draw_cards)
}

@Composable
fun HideAndSeekNavHost(
    navController: NavHostController,
    hideAndSeekViewModel: HideAndSeekViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier,
) {
    val uiState by hideAndSeekViewModel.uiState.collectAsState()
    val deckUiState by hideAndSeekViewModel.deckUiState.collectAsState()
    val preferencesUiState by hideAndSeekViewModel.preferencesUiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = HideAndSeekScreen.HiderDeck.name,
        enterTransition = { slideInVertically(initialOffsetY = { it / 2 }) },
        exitTransition = { slideOutVertically(targetOffsetY = { -80 }, animationSpec = tween()) },
        popEnterTransition = { slideInVertically(initialOffsetY = { -80 }, animationSpec = tween(durationMillis = 150)) },
        popExitTransition = { slideOutVertically(targetOffsetY = { it / 2 })+ fadeOut(tween(durationMillis = 200, delayMillis = 100)) },
        modifier = modifier
    ) {
        composable(route = HideAndSeekScreen.StartScreen.name) {
            StartScreen(
                onButtonClick = {
                    navController.navigate(HideAndSeekScreen.HiderDeck.name)
                                },
                viewModel = hideAndSeekViewModel,
                uiState = uiState
            )
        }

        composable(route = HideAndSeekScreen.HiderDeck.name) {
            HiderDeckScreen(
                onDrawCards = {
                    hideAndSeekViewModel.updateSelectCardText(false)
                    navController.navigate(HideAndSeekScreen.DrawCards.name)
                              },
                onNavigateToStartScreen = { navController.navigate(HideAndSeekScreen.StartScreen.name) },
                viewModel = hideAndSeekViewModel,
                uiState = uiState,
                deckUiState = deckUiState,
                preferencesUiState = preferencesUiState
            )
        }

        composable(route = HideAndSeekScreen.DrawCards.name) {
            DrawCardsScreen(
                viewModel = hideAndSeekViewModel,
                uiState = uiState,
                deckUiState = deckUiState,
                navigateUp = { navController.popBackStack(route = HideAndSeekScreen.HiderDeck.name, inclusive = false) }
            )
        }

    }
}