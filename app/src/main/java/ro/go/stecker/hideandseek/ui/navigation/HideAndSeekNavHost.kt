package ro.go.stecker.hideandseek.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
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
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ro.go.stecker.hideandseek.AppViewModelProvider
import ro.go.stecker.hideandseek.R
import ro.go.stecker.hideandseek.data.HideAndSeekViewModel
import ro.go.stecker.hideandseek.ui.screens.DetailsScreen
import ro.go.stecker.hideandseek.ui.screens.DrawCardsScreen
import ro.go.stecker.hideandseek.ui.screens.HiderDeckScreen
import ro.go.stecker.hideandseek.ui.screens.StartScreen

enum class HideAndSeekScreen(@StringRes val title: Int) {
    StartScreen(R.string.app_name),
    LoadingScreen(R.string.loading),
    HiderDeck(R.string.hider_deck),
    DetailsScreen(0),
    DrawCards(R.string.draw_cards)
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HideAndSeekNavHost(
    navController: NavHostController,
    hideAndSeekViewModel: HideAndSeekViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier,
) {
    val uiState by hideAndSeekViewModel.uiState.collectAsState()
    val deckUiState by hideAndSeekViewModel.deckUiState.collectAsState()
    val preferencesUiState by hideAndSeekViewModel.preferencesUiState.collectAsState()

    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = HideAndSeekScreen.HiderDeck.name,
            enterTransition = { slideInVertically(initialOffsetY = { it / 2 }) },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { -80 },
                    animationSpec = tween()
                )
            },
            popEnterTransition = {
                slideInVertically(
                    initialOffsetY = { -80 },
                    animationSpec = tween(durationMillis = 150)
                )
            },
            popExitTransition = {
                slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut(
                    tween(
                        durationMillis = 200,
                        delayMillis = 100
                    )
                )
            },
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

            composable(
                route = HideAndSeekScreen.HiderDeck.name,
                exitTransition = {
                    fadeOut(
                        tween(
                            durationMillis = 200,
                            delayMillis = 400
                    ))
                }
            ) {
                HiderDeckScreen(
                    onDrawCards = { navController.navigate(HideAndSeekScreen.DrawCards.name) },
                    onNavigateToStartScreen = { navController.navigate(HideAndSeekScreen.StartScreen.name) },
                    onDetailsClick = { navController.navigate(HideAndSeekScreen.DetailsScreen.name + "/$it") },
                    viewModel = hideAndSeekViewModel,
                    uiState = uiState,
                    deckUiState = deckUiState,
                    preferencesUiState = preferencesUiState,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable
                )
            }

            composable(
                route = HideAndSeekScreen.DetailsScreen.name + "/{cardUuid}",
                arguments = listOf(navArgument("cardUuid") { type = NavType.StringType })
            ) { backStackEntry ->
                val cardUuid = backStackEntry.arguments?.getString("cardUuid") ?: ""

                DetailsScreen(
                    card = deckUiState.playerDeck.first { it.uuid == cardUuid },
                    onBackClick = { navController.popBackStack() },
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable
                )
            }

            composable(route = HideAndSeekScreen.DrawCards.name) {
                DrawCardsScreen(
                    viewModel = hideAndSeekViewModel,
                    uiState = uiState,
                    preferencesUiState = preferencesUiState,
                    navigateUp = { navController.popBackStack() },
                    onNavigateToStartScreen = { navController.navigate(HideAndSeekScreen.StartScreen.name) }
                )
            }

        }
    }
}