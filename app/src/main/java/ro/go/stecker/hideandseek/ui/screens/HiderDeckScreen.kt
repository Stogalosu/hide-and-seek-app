package ro.go.stecker.hideandseek.ui.screens

import androidx.compose.ui.graphics.Color
import androidx.activity.compose.BackHandler
import androidx.annotation.IntRange
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import ro.go.stecker.hideandseek.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Warning
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import ro.go.stecker.hideandseek.AppViewModelProvider
import ro.go.stecker.hideandseek.data.Card
import ro.go.stecker.hideandseek.data.HideAndSeekUiState
import ro.go.stecker.hideandseek.data.HideAndSeekViewModel
import ro.go.stecker.hideandseek.data.CardsRepository
import ro.go.stecker.hideandseek.data.DeckUiState
import ro.go.stecker.hideandseek.data.GameState
import ro.go.stecker.hideandseek.data.PreferencesUiState
import ro.go.stecker.hideandseek.data.getCardWithId
import ro.go.stecker.hideandseek.getActivity
import ro.go.stecker.hideandseek.ui.navigation.HideAndSeekScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import ro.go.stecker.hideandseek.data.CardType
import ro.go.stecker.hideandseek.ui.HideAndSeekTopAppBar
import ro.go.stecker.hideandseek.ui.infraFontFamily

val discardRed = Color(224, 65, 65)
val confirmGreen = Color(87, 201, 90)

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
    viewModel.clearTempCards()
//    val configuration = LocalConfiguration.current
//    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val context = LocalContext.current
    var fabHeight by remember { mutableStateOf(0.dp) }

    BackHandler {
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
                    val current = LocalDensity.current
                    ExtendedFloatingActionButton(
                        onClick = onDrawCards,
                        modifier = Modifier
                            .padding(16.dp)
                            .onGloballyPositioned { coordinates ->
                                fabHeight = with(current) { coordinates.size.height.toDp() }
                            }
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
                    fabHeight = fabHeight,
                    contentPadding = innerPadding
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
    fabHeight: Dp,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    if(uiState.deleteCardDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.updateDeleteCardDialog() },
            icon = { Icon(Icons.Rounded.Delete, contentDescription = stringResource(R.string.delete_card)) },
            title = { Text(stringResource(R.string.delete_card)) },
            text = { Text(stringResource(R.string.delete_card_question, stringResource(deckUiState.getCardWithId(uiState.idToDelete).name))) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateDeleteCardDialog()
                        if(deckUiState.getCardWithId(uiState.idToDelete).name == R.string.curse_overflowing_chalice) viewModel.updateOverflowingChalice()
                        coroutineScope.launch {
                            viewModel.deleteCard(uiState.idToDelete)
                        }
                    }
                ) {
                    Text(text = stringResource(R.string.delete), color = discardRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.updateDeleteCardDialog() }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if(uiState.noCardsDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.updateNoCardsDialog() },
            icon = { Icon(Icons.Rounded.Warning, contentDescription = stringResource(R.string.no_cards_left)) },
            title = { Text(stringResource(R.string.no_cards_left)) },
            text = { Text(stringResource(R.string.no_cards_dialog)) },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { viewModel.updateNoCardsDialog() }) {
                    Text(stringResource(R.string.got_it))
                }
            }
        )
    }

    if(uiState.tooManyCardsDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.updateTooManyCardsDialog() },
            icon = { Icon(Icons.Rounded.Warning, contentDescription = stringResource(R.string.no_cards_left)) },
            title = { Text(stringResource(R.string.too_many_cards), textAlign = TextAlign.Center) },
            text = { Text(stringResource(R.string.too_many_cards_dialog)) },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { viewModel.updateTooManyCardsDialog() }) {
                    Text(stringResource(R.string.got_it))
                }
            }
        )
    }

    if(!deckUiState.playerDeck.isEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 380.dp),
            modifier = Modifier
                .padding(4.dp)
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            items(deckUiState.playerDeck) { card ->
                CardItem(card, viewModel)
            }

            item {
                Spacer(modifier = Modifier.height(32.dp + fabHeight + 8.dp))
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

@Composable
fun CardItem(
    card: Card,
    viewModel: HideAndSeekViewModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier
                    .padding(8.dp)
//                    .clickable(
//                        onClick = {
//                            viewModel.updateDeleteCardDialog()
//                            viewModel.setIdToDelete(item.id)
//                        }
//                    )
            ) {
                Image(
                    painterResource(card.image),
                    contentDescription = stringResource(R.string.card),
                    Modifier
                        .padding(5.dp)
                        .clip(RoundedCornerShape(6))
                        .size(height = 192.dp, width = 137.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(
                modifier = Modifier.padding(32.dp)
            ) {
                if(card.type != CardType.TimeBonus) {
                    ButtonWithIcon(
                        icon = Icons.Rounded.PlayArrow,
                        text = R.string.play,
                        color = confirmGreen,
                        onClick = {}
                    )
                }
                ButtonWithIcon(
                    icon = Icons.Rounded.Delete,
                    text = R.string.discard,
                    color = discardRed,
                    size = 25,
                    onClick = {
                        viewModel.setIdToDelete(card.id)
                        viewModel.updateDeleteCardDialog()
                    }
                )
                ButtonWithIcon(
                    icon = Icons.Rounded.Info,
                    text = R.string.details,
                    color = Color(207, 207, 207),
                    size = 25,
                    onClick = {}
                )
            }
        }
    }
}

@Composable
fun ButtonWithIcon(
    icon: ImageVector,
    @StringRes text: Int,
    color: Color = Color.White,
    @IntRange(0, 26) size: Int =  26,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Icon(
                imageVector = icon,
                contentDescription = stringResource(text),
                tint = color,
                modifier = Modifier
                    .size(26.dp)
                    .padding((26 - size).dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = stringResource(text),
                color = color,
                fontSize = 16.sp
            )
        }
    }
}

@Preview
@Composable
fun HiderDeckScreenPreview() {
    HiderDeckScreen({ CardsRepository[0] }, {}, viewModel(factory = AppViewModelProvider.Factory), HideAndSeekUiState(), DeckUiState(), PreferencesUiState())
}