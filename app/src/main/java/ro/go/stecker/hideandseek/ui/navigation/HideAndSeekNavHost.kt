package ro.go.stecker.hideandseek.ui.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import kotlinx.coroutines.flow.distinctUntilChanged
import ro.go.stecker.hideandseek.data.firestore.PlayerType
import ro.go.stecker.hideandseek.data.toCard
import ro.go.stecker.hideandseek.network.NetworkStatus
import ro.go.stecker.hideandseek.ui.dialogs.NoInternetDialog
import ro.go.stecker.hideandseek.viewmodel.AppViewModelProvider
import ro.go.stecker.hideandseek.viewmodel.HiderViewModel
import ro.go.stecker.hideandseek.ui.screens.DetailsScreen
import ro.go.stecker.hideandseek.ui.screens.DrawCardsScreen
import ro.go.stecker.hideandseek.ui.screens.MainScreen
import ro.go.stecker.hideandseek.ui.screens.StartScreen
import ro.go.stecker.hideandseek.viewmodel.HideAndSeekViewModel
import ro.go.stecker.hideandseek.viewmodel.SeekerViewModel

enum class HideAndSeekScreen {
    StartScreen,
    LoadingScreen,
    MainScreen,
    DetailsScreen,
    DrawCards,
    NoInternetDialog
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HideAndSeekNavHost(
    navController: NavHostController,
    hideAndSeekViewModel: HideAndSeekViewModel = viewModel(factory = AppViewModelProvider.Factory),
    hiderViewModel: HiderViewModel = viewModel(factory = AppViewModelProvider.Factory),
    seekerViewModel: SeekerViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier,
) {
    val hiderUiState by hiderViewModel.hiderUiState.collectAsState()
    val seekerUiState by seekerViewModel.seekerUiState.collectAsState()
    val uiState by hideAndSeekViewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        snapshotFlow { uiState.networkStatus }
            .distinctUntilChanged()
            .collect { value ->
                if(value == NetworkStatus.Unavailable) navController.navigate(HideAndSeekScreen.NoInternetDialog.name)
            }
    }

    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = HideAndSeekScreen.MainScreen.name,
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
                    onGameStart = {
                        navController.navigate(HideAndSeekScreen.MainScreen.name)
                    },
                    viewModel = hideAndSeekViewModel,
                    uiState = uiState,
                    snackbarHostState = snackbarHostState
                )
            }

            composable(
                route = HideAndSeekScreen.MainScreen.name,
                exitTransition = {
                    fadeOut(
                        tween(
                            durationMillis = 200,
                            delayMillis = 400
                    ))
                }
            ) {
                MainScreen(
                    onDrawCards = { navController.navigate(HideAndSeekScreen.DrawCards.name) },
                    onNavigateToStartScreen = { navController.navigate(HideAndSeekScreen.StartScreen.name) },
                    onDetailsClick = { navController.navigate(HideAndSeekScreen.DetailsScreen.name + "/$it") },
                    viewModel = hideAndSeekViewModel,
                    hiderViewModel = hiderViewModel,
                    seekerViewModel = seekerViewModel,
                    hiderUiState = hiderUiState,
                    seekerUiState = seekerUiState,
                    uiState = uiState,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable,
                    snackbarHostState = snackbarHostState
                )
            }

            composable(
                route = HideAndSeekScreen.DetailsScreen.name + "/{cardUuid}",
                arguments = listOf(navArgument("cardUuid") { type = NavType.StringType })
            ) { backStackEntry ->
                val cardUuid = backStackEntry.arguments?.getString("cardUuid") ?: ""

                DetailsScreen(
                    card =
                        if(uiState.player.type == PlayerType.Hider)
                            hiderUiState.playerDeck.first { it.uuid == cardUuid }
                        else seekerUiState.curses.first { it.uuid == cardUuid }.toCard(),
                    onBackClick = { navController.popBackStack() },
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable
                )
            }

            composable(route = HideAndSeekScreen.DrawCards.name) {
                DrawCardsScreen(
                    viewModel = hideAndSeekViewModel,
                    hiderViewModel = hiderViewModel,
                    uiState = hiderUiState,
                    navigateUp = {
                        navController.popBackStack(
                            route = HideAndSeekScreen.MainScreen.name,
                            inclusive = false
                        )
                    },
                    snackbarHostState = snackbarHostState
                )
            }

            dialog(route = HideAndSeekScreen.NoInternetDialog.name) {
                NoInternetDialog(
                    onDismissRequest = { navController.popBackStack() }
                )
            }

        }
    }
}