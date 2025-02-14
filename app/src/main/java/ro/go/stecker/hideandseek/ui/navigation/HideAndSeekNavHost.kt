package ro.go.stecker.hideandseek.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.splineBasedDecay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import kotlinx.coroutines.delay
import ro.go.stecker.hideandseek.R
import ro.go.stecker.hideandseek.data.HideAndSeekViewModel
import ro.go.stecker.hideandseek.ui.DrawCardsScreen
import ro.go.stecker.hideandseek.ui.HiderDeckScreen
import ro.go.stecker.hideandseek.ui.StartScreen

enum class HideAndSeekScreen(@StringRes val title: Int) {
    StartRound(R.string.app_name),
    HiderDeck(R.string.hider_deck),
    DrawCards(R.string.draw_cards)
}

@Composable
fun HideAndSeekNavHost(
    navController: NavHostController,
    hideAndSeekViewModel: HideAndSeekViewModel = viewModel(),
    modifier: Modifier = Modifier,
) {
    val uiState by hideAndSeekViewModel.uiState.collectAsState()
    NavHost(
        navController = navController,
        startDestination = HideAndSeekScreen.StartRound.name,
        modifier = modifier
    ) {
        composable(
            route = HideAndSeekScreen.StartRound.name,
            exitTransition = { slideOutVertically(targetOffsetY = { -80 }, animationSpec = tween()) },
            popEnterTransition = { slideInVertically(initialOffsetY = { -80 }, animationSpec = tween(durationMillis = 150)) }
        ) {
            StartScreen(
                navigateToDeck = {
                    navController.navigate(HideAndSeekScreen.HiderDeck.name)
                                 },
                viewModel = hideAndSeekViewModel,
                uiState = uiState
            )
        }
        composable(
            route = HideAndSeekScreen.HiderDeck.name,
            enterTransition = { slideInVertically(initialOffsetY = { it / 2 }) },
            exitTransition = { slideOutVertically(targetOffsetY = { -80 }, animationSpec = tween()) },
            popEnterTransition = { slideInVertically(initialOffsetY = { -80 }, animationSpec = tween(durationMillis = 150)) },
            popExitTransition = { slideOutVertically(targetOffsetY = { it / 2 })+ fadeOut(tween(durationMillis = 200, delayMillis = 100)) },
        ) {
            HiderDeckScreen(
                onDrawCards = {
                    hideAndSeekViewModel.updateSelectCardText(false)
                    if(uiState.cardDeck.size >= 6) uiState.tooManyCards = true
                    else uiState.tooManyCards = false
                    navController.navigate(HideAndSeekScreen.DrawCards.name)
                              },
                viewModel = hideAndSeekViewModel,
                uiState = uiState
            )
        }
        composable(
            route = HideAndSeekScreen.DrawCards.name,
            enterTransition = { slideInVertically(initialOffsetY = { it / 2 }) },
            popExitTransition = {
                slideOutVertically(
                    targetOffsetY = { it / 2 }
                ) + fadeOut(tween(durationMillis = 200, delayMillis = 100))
            }
        ) {
            DrawCardsScreen(
                viewModel = hideAndSeekViewModel,
                uiState = uiState,
                navigateUp = {
                    navController.popBackStack(route = HideAndSeekScreen.HiderDeck.name, inclusive = false)
                    hideAndSeekViewModel.updateSelectCardText(false)
                }
            )
        }

    }
}