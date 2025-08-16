package ro.go.stecker.hideandseek.ui.screens

import androidx.compose.ui.graphics.Color
import androidx.activity.compose.BackHandler
import androidx.annotation.IntRange
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Clear
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
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import kotlinx.coroutines.launch
import ro.go.stecker.hideandseek.data.Card
import ro.go.stecker.hideandseek.data.HiderUiState
import ro.go.stecker.hideandseek.viewmodel.HiderViewModel
import ro.go.stecker.hideandseek.data.DeckUiState
import ro.go.stecker.hideandseek.getActivity
import ro.go.stecker.hideandseek.ui.navigation.HideAndSeekScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay
import ro.go.stecker.hideandseek.data.SelectMode
import ro.go.stecker.hideandseek.data.UiState
import ro.go.stecker.hideandseek.data.getCardWithUuid
import ro.go.stecker.hideandseek.data.getName
import ro.go.stecker.hideandseek.data.isPlayable
import ro.go.stecker.hideandseek.network.NetworkStatus
import ro.go.stecker.hideandseek.ui.CardImage
import ro.go.stecker.hideandseek.ui.HideAndSeekTopAppBar
import ro.go.stecker.hideandseek.ui.infraFontFamily
import ro.go.stecker.hideandseek.viewmodel.HideAndSeekViewModel

val discardRed = Color(224, 65, 65)
val confirmGreen = Color(87, 201, 90)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HiderDeckScreen(
    onDrawCards: () -> Unit,
    onDetailsClick: (String) -> Unit,
    viewModel: HideAndSeekViewModel,
    hiderViewModel: HiderViewModel,
    uiState: UiState,
    hiderUiState: HiderUiState,
    deckUiState: DeckUiState,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    snackbarHostState: SnackbarHostState
) {
    LaunchedEffect(Unit) {
        delay(500)
        hiderViewModel.clearTempCards()
    }

    val context = LocalContext.current
    var fabHeight by remember { mutableStateOf(0.dp) }
    val coroutineScope = rememberCoroutineScope()
    val selectCardsText = stringResource(R.string.please_select_2_cards)

    BackHandler {
        if(hiderUiState.selectCardMode == SelectMode.NotActive) context.getActivity()?.finish()
        else coroutineScope.launch { hiderViewModel.endCardSelection(false) }
    }

    Scaffold(
        topBar = {
            HideAndSeekTopAppBar(
                title =
                    if(hiderUiState.selectCardMode != SelectMode.NotActive) stringResource(R.string.select_n_cards, hiderUiState.selectCardMode.howMany.toString())
                    else stringResource(R.string.hider_deck),
                canNavigateBack = hiderUiState.selectCardMode != SelectMode.NotActive,
                navigateUp = { coroutineScope.launch { hiderViewModel.endCardSelection(false) } },
                currentScreen = HideAndSeekScreen.MainScreen,
                doneButton = hiderUiState.selectCardMode != SelectMode.NotActive,
                onDoneButtonClick = {
                    coroutineScope.launch {
                        if(hiderUiState.selectedCards.size == 2) discardCardDialog = true
                        else snackbarHostState.showSnackbar(selectCardsText)
                    }
                },
                viewModel = viewModel,
                snackbarHostState = snackbarHostState
            )
        },
        floatingActionButton = {
            val current = LocalDensity.current
            ExtendedFloatingActionButton(
                onClick = {
                    if (deckUiState.playerDeck.size >= 6) tooManyCardsDialog = true
                    else if (deckUiState.cardDeck.sumOf { it.probability } <= 3) noCardsDialog = true
                    else onDrawCards()
                },
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
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) {innerPadding ->
        HiderDeck(
            hiderViewModel = hiderViewModel,
            uiState = uiState,
            hiderUiState = hiderUiState,
            deckUiState = deckUiState,
            fabHeight = fabHeight,
            contentPadding = innerPadding,
            onDetailsClick = onDetailsClick,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope,
            snackbarHostState = snackbarHostState
        )
    }
}

var discardCardDialog by mutableStateOf(false)
var noCardsDialog by mutableStateOf(false)
var tooManyCardsDialog by mutableStateOf(false)
var playCardDialog by mutableStateOf(Pair(false, Card()))
var failedCardPlay by mutableStateOf(false)
var duplicateCardDialog by mutableStateOf(false)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HiderDeck(
    hiderViewModel: HiderViewModel,
    uiState: UiState,
    hiderUiState: HiderUiState,
    deckUiState: DeckUiState,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    fabHeight: Dp,
    onDetailsClick: (String) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    snackbarHostState: SnackbarHostState
) {
    val coroutineScope = rememberCoroutineScope()

    //Dialog for deleting a card
    if(discardCardDialog) {
        AlertDialog(
            onDismissRequest = { discardCardDialog = false },
            icon = { Icon(Icons.Rounded.Delete, contentDescription = stringResource(R.string.discard_card)) },
            title = { Text(stringResource(R.string.discard_card)) },
            text = {
                Text(
                    when (hiderUiState.selectCardMode) {
                        SelectMode.Discard1Draw2 ->
                            stringResource(R.string.discard_card_dialog, stringResource(hiderUiState.selectedCards.first().getName()))
                        SelectMode.Discard2Draw3 ->
                            stringResource(R.string.discard_2_draw_3_card_dialog)
                        else ->
                            stringResource(R.string.discard_card_dialog, stringResource(deckUiState.getCardWithUuid(hiderUiState.uuidToDelete).getName()))
                    }
                )
                   },
            confirmButton = {
                TextButton(
                    onClick = {
                        discardCardDialog = false
                        coroutineScope.launch {
                            if (hiderUiState.selectCardMode != SelectMode.NotActive) {
                                hiderViewModel.endCardSelection(true)
                            }
                            hiderViewModel.deleteCard(hiderUiState.uuidToDelete)
                        }
                    }
                ) {
                    Text(text = stringResource(R.string.delete), color = discardRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { discardCardDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    //Dialog for when there are no cards left to draw
    if(noCardsDialog) {
        AlertDialog(
            onDismissRequest = { noCardsDialog = false },
            icon = { Icon(Icons.Rounded.Warning, contentDescription = stringResource(R.string.no_cards_left)) },
            title = { Text(stringResource(R.string.no_cards_left)) },
            text = { Text(stringResource(R.string.no_cards_dialog)) },
            confirmButton = {
                TextButton(onClick = { noCardsDialog = false }) {
                    Text(stringResource(R.string.got_it))
                }
            }
        )
    }

    //Dialog for when you have the maximum number of cards (6)
    if(tooManyCardsDialog) {
        AlertDialog(
            onDismissRequest = { tooManyCardsDialog = false },
            icon = { Icon(Icons.Rounded.Warning, contentDescription = stringResource(R.string.no_cards_left)) },
            title = { Text(stringResource(R.string.too_many_cards), textAlign = TextAlign.Center) },
            text = { Text(stringResource(R.string.too_many_cards_dialog)) },
            confirmButton = {
                TextButton(onClick = { tooManyCardsDialog = false }) {
                    Text(stringResource(R.string.got_it))
                }
            }
        )
    }

    //Dialog for playing a card
    if(playCardDialog.first) {
        AlertDialog(
            onDismissRequest = { playCardDialog = Pair(false, playCardDialog.second) },
            icon = { Icon(painterResource(R.drawable.ic_playing_cards), contentDescription = null) },
            title = { Text(stringResource(R.string.play_card)) },
            text = { Text(stringResource(R.string.play_card_question)) },
            dismissButton = {
                TextButton(onClick = { playCardDialog = Pair(false, playCardDialog.second) }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            hiderViewModel.playCard(
                                card = playCardDialog.second,
                                onDone = { success ->
                                    if(success)
                                        coroutineScope.launch {
                                            hiderViewModel.deleteCard(hiderUiState.uuidToDelete)
                                            snackbarHostState.showSnackbar("Successfully played card!")
                                        }
                                    else failedCardPlay = true
                                }
                            )
                        }
                        playCardDialog = Pair(false, Card())
                    }
                ) { Text(text = stringResource(R.string.play), color = confirmGreen) }
            }
        )
    }

    //Dialog for failed card play
    if(failedCardPlay) {
        AlertDialog(
            onDismissRequest = { failedCardPlay = false },
            icon = { Icon(Icons.Rounded.Warning, contentDescription = null) },
            title = { Text(stringResource(R.string.failed_to_play_card)) },
            text = { Text(stringResource(R.string.failed_to_play_card_text)) },
            confirmButton = {
                TextButton(onClick = { failedCardPlay = false }) {
                    Text(stringResource(R.string.got_it))
                }
            }
        )
    }

    //Dialog for duplicating a card
    if(duplicateCardDialog) {
        AlertDialog(
            onDismissRequest = { duplicateCardDialog = false },
            icon = { Icon(Icons.Rounded.Check, contentDescription = stringResource(R.string.duplicate_card)) },
            title = { Text(stringResource(R.string.duplicate_card)) },
            text = { Text(stringResource(R.string.duplicate_card_dialog, stringResource(hiderUiState.selectedCards.first().getName()))) },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            hiderViewModel.endCardSelection(true)
                        }
                        duplicateCardDialog = false
                    }
                ) {
                    Text(
                        text = stringResource(R.string.yes),
                        color = confirmGreen
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { duplicateCardDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    //The actual deck
    if(!deckUiState.playerDeck.isEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 380.dp),
            modifier = Modifier
                .padding(4.dp)
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            items(items = deckUiState.playerDeck, key = { it.uuid }) { card ->
                Box(modifier = Modifier.animateItem()) {
                    CardItem(
                        card = card,
                        uiState = uiState,
                        hiderUiState = hiderUiState,
                        hiderViewModel = hiderViewModel,
                        onDetailsClick = onDetailsClick,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CardItem(
    card: Card,
    uiState: UiState,
    hiderUiState: HiderUiState,
    hiderViewModel: HiderViewModel,
    onDetailsClick: (String) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val coroutineScope = rememberCoroutineScope()

    with(sharedTransitionScope) {
        Card(
            modifier = Modifier
                .sharedElement(
                    sharedTransitionScope.rememberSharedContentState(key = "card-${card.uuid}"),
                    animatedVisibilityScope = animatedVisibilityScope
                )
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                with(sharedTransitionScope) {
                    CardImage(
                        card = card,
                        onClick = {
                            onDetailsClick(card.uuid)
                        },
                        clickable = true,
                        imageModifier = Modifier
                            .padding(5.dp)
                            .size(height = 192.dp, width = 137.dp)
                            .sharedElement(
                                sharedTransitionScope.rememberSharedContentState(key = "image-${card.uuid}"),
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                            .clip(RoundedCornerShape(6)),
                        cardModifier = Modifier
                            .sharedElement(
                                sharedTransitionScope.rememberSharedContentState(key = "border-${card.uuid}"),
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    modifier = Modifier.padding(32.dp)
                ) {
                    when(hiderUiState.selectCardMode) {
                        SelectMode.Duplicate -> {
                            if(card.uuid != hiderUiState.uuidToDelete) {
                                ButtonWithIcon(
                                    icon = Icons.Rounded.Check,
                                    text = R.string.select,
                                    color = confirmGreen,
                                    onClick = {
                                        hiderViewModel.selectCard(card)
                                        duplicateCardDialog = true
                                    }
                                )
                            }
                        }

                        SelectMode.Discard1Draw2 -> {
                            if(card.uuid != hiderUiState.uuidToDelete) {
                                ButtonWithIcon(
                                    icon = Icons.Rounded.Clear,
                                    text = R.string.select,
                                    color = discardRed,
                                    onClick = {
                                        hiderViewModel.selectCard(card)
                                        discardCardDialog = true
                                    }
                                )
                            }
                        }

                        SelectMode.Discard2Draw3 -> {
                            if(card.uuid != hiderUiState.uuidToDelete) {
                                Checkbox(
                                    checked = hiderUiState.selectedCards.contains(card),
                                    onCheckedChange = { isNowChecked ->
                                        if(isNowChecked) hiderViewModel.selectCard(card)
                                        else hiderViewModel.deselectCard(card)
                                    }
                                )
                            }
                        }

                        SelectMode.NotActive -> {
                            if(card.isPlayable()) {
                                ButtonWithIcon(
                                    icon = Icons.Rounded.PlayArrow,
                                    text = R.string.play,
                                    color = confirmGreen,
                                    onClick = {
                                        if(!hiderViewModel.playSpecialCard(card))
                                            if(uiState.networkStatus == NetworkStatus.Available) {
                                                hiderViewModel.setUuidToDelete(card.uuid)
                                                playCardDialog = Pair(true, card)
                                            }
                                    }
                                )
                            }
                            ButtonWithIcon(
                                icon = Icons.Rounded.Delete,
                                text = R.string.discard,
                                color = discardRed,
                                size = 25,
                                onClick = {
                                    hiderViewModel.setUuidToDelete(card.uuid)
                                    discardCardDialog = true
                                }
                            )
                            ButtonWithIcon(
                                icon = Icons.Rounded.Info,
                                text = R.string.details,
                                color = Color(207, 207, 207),
                                size = 25,
                                onClick = {
                                    onDetailsClick(card.uuid)
                                }
                            )
                        }
                    }
                }
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

//@Preview
//@Composable
//fun HiderDeckScreenPreview() {
//    HiderDeckScreen({ CardsRepository[0] }, {}, {}, viewModel(factory = AppViewModelProvider.Factory), HideAndSeekUiState(), DeckUiState(), PreferencesUiState())
//}