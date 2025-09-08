package ro.go.stecker.hideandseek.ui.screens

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ro.go.stecker.hideandseek.R
import ro.go.stecker.hideandseek.data.HiderUiState
import ro.go.stecker.hideandseek.viewmodel.HiderViewModel
import ro.go.stecker.hideandseek.ui.CardImage
import ro.go.stecker.hideandseek.ui.HideAndSeekTopAppBar
import ro.go.stecker.hideandseek.ui.infraFontFamily
import ro.go.stecker.hideandseek.ui.navigation.HideAndSeekScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import kotlinx.coroutines.delay
import ro.go.stecker.hideandseek.data.GameState
import ro.go.stecker.hideandseek.data.UiState
import ro.go.stecker.hideandseek.data.getDescription
import ro.go.stecker.hideandseek.data.getName
import ro.go.stecker.hideandseek.viewmodel.HideAndSeekViewModel


enum class DrawType(val draw: Int, val pick: Int) {
    Draw3Pick1(draw = 3, pick = 1),
    Draw2Pick1(draw = 2, pick = 1),
    Pick1(draw = 1, pick = 1)
}

private var currentCardIndex by mutableIntStateOf(0)
private var drawCard by mutableStateOf(true)
private var selectCard by mutableStateOf(false)
private var loseCardsDialog by mutableStateOf(false)

@Composable
fun DrawCardsScreen(
    viewModel: HideAndSeekViewModel,
    hiderViewModel: HiderViewModel,
    hiderUiState: HiderUiState,
    uiState: UiState,
    navigateUp: () -> Unit,
    onNavigateToStartScreen: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val onBackClick = {
        if(selectCard) loseCardsDialog = true
        else navigateUp()
    }

    BackHandler { onBackClick() }

    LaunchedEffect(uiState.gameState) {
        if(uiState.gameState == GameState.NotStarted)
            onNavigateToStartScreen()
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            HideAndSeekTopAppBar(
                title = stringResource(R.string.draw_cards),
                canNavigateBack = true,
                navigateUp = { onBackClick() },
                currentScreen = HideAndSeekScreen.DrawCards,
                viewModel = viewModel,
                snackbarHostState = snackbarHostState
            )
        }
    ) {innerPadding ->
        DrawCards(
            viewModel = hiderViewModel,
            uiState = hiderUiState,
            navigateUp = navigateUp,
            contentPadding = innerPadding
        )
    }
}

@Composable
fun DrawCards(
    viewModel: HiderViewModel,
    uiState: HiderUiState,
    navigateUp: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val screenWidth = LocalWindowInfo.current.containerSize.width
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if(loseCardsDialog) {
        AlertDialog(
            onDismissRequest = { loseCardsDialog = !loseCardsDialog },
            icon = { Icon(imageVector = Icons.Rounded.Warning, contentDescription = stringResource(R.string.go_back_sure)) },
            title = { Text(
                text = stringResource(R.string.go_back_sure),
                textAlign = TextAlign.Center
            ) },
            text = { Text(stringResource(R.string.lose_cards_dialog)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        loseCardsDialog = !loseCardsDialog
                        navigateUp()
                        selectCard = false
                        drawCard = true
                    }
                ) {
                    Text(
                        text = stringResource(R.string.go_back),
                        color = discardRed
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { loseCardsDialog = !loseCardsDialog }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(contentPadding)
            .padding(16.dp)
            .fillMaxSize()
    ) {
        val weight =
            if(isLandscape) 3f
            else 1.75f

        AnimatedContent(
            targetState = currentCardIndex,
            transitionSpec = {
                if(targetState > initialState) {
                    slideInHorizontally(
                        animationSpec = tween(durationMillis = 300),
                        initialOffsetX = { screenWidth }
                    ).togetherWith(
                        slideOutHorizontally(
                            animationSpec = tween(durationMillis = 300),
                            targetOffsetX = { -screenWidth }
                        )
                    )
                } else {
                    slideInHorizontally(
                        animationSpec = tween(durationMillis = 300),
                        initialOffsetX = { -screenWidth }
                    ).togetherWith(
                        slideOutHorizontally(
                            animationSpec = tween(durationMillis = 300),
                            targetOffsetX = { screenWidth }
                        )
                    )
                }
            },
            modifier = Modifier
                .padding(horizontal = 0.dp)
                .weight(weight)
        ) { currentIndex ->
            if(uiState.drawnTempCards.isNotEmpty() && currentIndex <= uiState.drawnTempCards.lastIndex) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CardImage(
                        card = uiState.drawnTempCards[currentIndex],
                        clickable = false,
                        imageModifier = Modifier
                            .padding(5.dp)
                            .clip(RoundedCornerShape(2))
//                            .size(width = 250.dp, height = 350.dp)
                    )
                }
            }
        }

        if(uiState.drawnTempCards.isEmpty()) Spacer(modifier = Modifier.weight(weight))

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if(currentCardIndex > 0) currentCardIndex--
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.previous_card)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            AnimatedContent(
                targetState = currentCardIndex
            ) { targetState ->
                if(uiState.drawnTempCards.isNotEmpty()) {
                    Text(
                        text = stringResource(uiState.drawnTempCards[targetState].getName()),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(256.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    if(currentCardIndex < uiState.drawnTempCards.lastIndex) currentCardIndex++
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = stringResource(R.string.next_card)
                )
            }
        }

        AnimatedVisibility(
            visible = drawCard,
            exit = shrinkHorizontally(shrinkTowards = Alignment.CenterHorizontally) + fadeOut()
        ) {
            DrawTypeSelector(
                viewModel = viewModel,
                uiState = uiState,
                isLandscape = isLandscape
            )
        }

        AnimatedVisibility(
            visible = selectCard,
            enter = fadeIn(),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            AnimatedContent(
                targetState = currentCardIndex
            ) { targetState ->
                Text(
                    text = stringResource(uiState.drawnTempCards[targetState].getDescription()),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .padding(top = 16.dp)
                )
            }
        }

        AnimatedVisibility(
            visible = selectCard,
            enter = fadeIn(),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.addCardToDeck(uiState.drawnTempCards[currentCardIndex])
                        selectCard = false
                        drawCard = true
                    }
                    navigateUp()
                },
                modifier = Modifier
                    .padding(bottom = 32.dp)

            ) {
                Text(stringResource(R.string.pick_card))
            }
        }
    }
}

@Composable
fun DrawTypeSelector(
    viewModel: HiderViewModel,
    uiState: HiderUiState,
    isLandscape: Boolean
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedDrawType by remember { mutableStateOf(DrawType.Pick1) }

    Card(modifier = Modifier.padding(32.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)) {
            if(!isLandscape) {
                Column {
                    RadioButtonWithText(
                        text = R.string.draw_1,
                        drawType = DrawType.Pick1,
                        selectedDrawType = selectedDrawType,
                        onClick = { selectedDrawType = DrawType.Pick1 },
                        modifier = Modifier.fillMaxWidth()
                    )
                    RadioButtonWithText(
                        text = R.string.draw_2_pick_1,
                        drawType = DrawType.Draw2Pick1,
                        selectedDrawType = selectedDrawType,
                        onClick = { selectedDrawType = DrawType.Draw2Pick1 },
                        modifier = Modifier.fillMaxWidth()
                    )
                    RadioButtonWithText(
                        text = R.string.draw_3_pick_1,
                        drawType = DrawType.Draw3Pick1,
                        selectedDrawType = selectedDrawType,
                        onClick = { selectedDrawType = DrawType.Draw3Pick1 },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            else {
                Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                    RadioButtonWithText(
                        text = R.string.draw_1,
                        drawType = DrawType.Pick1,
                        selectedDrawType = selectedDrawType,
                        onClick = { selectedDrawType = DrawType.Pick1 }
                    )
                    RadioButtonWithText(
                        text = R.string.draw_2_pick_1,
                        drawType = DrawType.Draw2Pick1,
                        selectedDrawType = selectedDrawType,
                        onClick = { selectedDrawType = DrawType.Draw2Pick1 }
                    )
                    RadioButtonWithText(
                        text = R.string.draw_3_pick_1,
                        drawType = DrawType.Draw3Pick1,
                        selectedDrawType = selectedDrawType,
                        onClick = { selectedDrawType = DrawType.Draw3Pick1 }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            if(1 <= uiState.overflowingChalice && uiState.overflowingChalice <= 3) {
                Text(
                    text = stringResource(R.string.overflowing_chalice_active),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Button(
                onClick = {
                    coroutineScope.launch {
                        drawCard = false
                        currentCardIndex = 0
                        delay(200)
                        viewModel.drawTempCards(selectedDrawType)
                        selectCard = true
                    }
                }
            ) {
                Text(stringResource(R.string.draw_cards), fontFamily = infraFontFamily)
            }

        }
    }
}

@Composable
fun RadioButtonWithText(
    @StringRes
    text: Int,
    drawType: DrawType,
    selectedDrawType: DrawType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .selectable(
                selected = selectedDrawType == drawType,
                onClick = onClick
            )
    ) {
        RadioButton(
            selected = selectedDrawType == drawType,
            onClick = onClick
        )
        Text(stringResource(text))
    }
}