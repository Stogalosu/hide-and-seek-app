package ro.go.stecker.hideandseek.ui

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import ro.go.stecker.hideandseek.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import  androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import ro.go.stecker.hideandseek.AppViewModelProvider
import ro.go.stecker.hideandseek.data.HideAndSeekUiState
import ro.go.stecker.hideandseek.data.HideAndSeekViewModel
import ro.go.stecker.hideandseek.data.CardsRepository
import ro.go.stecker.hideandseek.data.DeckUiState
import ro.go.stecker.hideandseek.data.GameState
import ro.go.stecker.hideandseek.data.PreferencesUiState
import ro.go.stecker.hideandseek.data.getCardWithId
import ro.go.stecker.hideandseek.getActivity
import ro.go.stecker.hideandseek.ui.navigation.HideAndSeekScreen

@Composable
fun HiderDeckScreen(
    onDrawCards: () -> Unit,
    onNavigateToStartScreen: () -> Unit,
    viewModel: HideAndSeekViewModel,
    uiState: HideAndSeekUiState,
    deckUiState: DeckUiState,
    preferencesUiState: PreferencesUiState,
    modifier: Modifier = Modifier
) {
    uiState.drawnTempCards.clear()
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val context = LocalContext.current

    BackHandler() {
        context.getActivity()?.finish()
    }

    LaunchedEffect(preferencesUiState.isGameStarted) {
        if(preferencesUiState.isGameStarted == GameState.NotStarted)
            onNavigateToStartScreen()
    }

    when(preferencesUiState.isGameStarted) {
        GameState.Started -> {
            Scaffold(
                modifier = modifier,
                topBar = {
                    HideAndSeekTopAppBar(
                        title = stringResource(R.string.hider_deck),
                        canNavigateBack = false,
                        currentScreen = HideAndSeekScreen.HiderDeck,
                        viewModel = viewModel
                    )
                },
                floatingActionButton = {
                    ExtendedFloatingActionButton(
                        onClick = onDrawCards,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Add, contentDescription = stringResource(R.string.draw_cards))
                            Text(
                                text = stringResource(R.string.draw_cards),
                                fontFamily = infraFontFamily,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                },
            ) {innerPadding ->
                HiderDeck(
                    viewModel = viewModel,
                    uiState = uiState,
                    deckUiState = deckUiState,
                    contentPadding = if(isPortrait) PaddingValues() else innerPadding
                )
            }
        }

        else -> {
            viewModel.init()
            LoadingScreen()
        }
    }
}


@Composable
fun HiderDeck(
    viewModel: HideAndSeekViewModel,
    uiState: HideAndSeekUiState,
    deckUiState: DeckUiState,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    if(uiState.isDeleteMenuActive) {
        AlertDialog(
            onDismissRequest = { viewModel.updateDeleteMenu() },
            icon = { Icon(Icons.Rounded.Delete, contentDescription = stringResource(R.string.delete_card)) },
            title = { Text(stringResource(R.string.delete_card)) },
            text = { Text(stringResource(R.string.delete_card_question, stringResource(deckUiState.getCardWithId(uiState.idToDelete).name))) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateDeleteMenu()
                        if(deckUiState.getCardWithId(uiState.idToDelete).name == R.string.curse_overflowing_chalice) viewModel.updateOverflowingChalice()
                        coroutineScope.launch {
                            viewModel.deleteCard(uiState.idToDelete)
                        }
                    }
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.updateDeleteMenu() }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if(!deckUiState.cardDeck.isEmpty()) {
        LazyRow(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            items(items = deckUiState.cardDeck) { item ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable(
                            onClick = {
                                viewModel.updateDeleteMenu()
                                viewModel.setIdToDelete(item.id)
                            }
                        )
                ) {
                    Image(
                        painterResource(item.image),
                        contentDescription = stringResource(R.string.card),
                        Modifier
                            .padding(5.dp)
                            .clip(RoundedCornerShape(2))
//                            .size(width = 350.dp, height = 490.dp)
                    )
                }
            }

        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.no_cards),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.weight(1f))
        }

    }
}


@Preview
@Composable
fun HiderDeckScreenPreview() {
    HiderDeckScreen({ CardsRepository[0] }, {}, viewModel(factory = AppViewModelProvider.Factory), HideAndSeekUiState(), DeckUiState(), PreferencesUiState())
}