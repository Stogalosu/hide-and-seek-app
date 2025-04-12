package ro.go.stecker.hideandseek.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import ro.go.stecker.hideandseek.AppViewModelProvider
import ro.go.stecker.hideandseek.R
import ro.go.stecker.hideandseek.data.HideAndSeekUiState
import ro.go.stecker.hideandseek.data.HideAndSeekViewModel
import ro.go.stecker.hideandseek.ui.navigation.HideAndSeekScreen


enum class DrawType(val draw: Int, val pick: Int) {
    Draw3Pick1(draw = 3, pick = 1),
    Draw2Pick1(draw = 2, pick = 1),
    Pick1(draw = 1, pick = 1)
}

@Composable
fun DrawCardsScreen(
    viewModel: HideAndSeekViewModel,
    uiState: HideAndSeekUiState,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            HideAndSeekTopAppBar(
                title = stringResource(R.string.draw_cards),
                canNavigateBack = true,
                navigateUp = navigateUp,
                currentScreen = HideAndSeekScreen.DrawCards,
                viewModel = viewModel
            )
        }
    ) {innerPadding ->
        DrawCards(
            viewModel = viewModel,
            uiState = uiState,
            navigateUp = navigateUp,
            contentPadding = innerPadding
        )

    }
}

@Composable
fun DrawCards(
    viewModel: HideAndSeekViewModel,
    uiState: HideAndSeekUiState,
    navigateUp: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier
            .padding(contentPadding)
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.weight(1f))
        LazyRow {
            items(items = uiState.drawnTempCards) { item ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable(
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.addCardToDeck(item)
                                }
                                navigateUp()
                            }
                        )
                ) {
                    Image(
                        painterResource(item.image),
                        contentDescription = stringResource(item.name),
                        Modifier
                            .padding(5.dp)
                            .clip(RoundedCornerShape(2))
                    )
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))

//        TODO ANIMATION

//        AnimatedVisibility(
//            visible = !uiState.selectCard,
//            exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(durationMillis = 1000))
//        ) {
//            DrawTypeSelector(viewModel, uiState)
//        }
//
//        AnimatedVisibility(
//            visible = uiState.selectCard,
//            enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(durationMillis = 1000))
//        ) {
//            SelectCardText()
//        }


        if (uiState.cardDeck.size >= 6) {
            TooManyCardsText()
        } else if (uiState.selectCard)
            SelectCardText()
        else DrawTypeSelector(viewModel, uiState)
    }

}

@Composable
fun DrawTypeSelector(
    viewModel: HideAndSeekViewModel,
    uiState: HideAndSeekUiState
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)) {
            RadioButtonWithText(
                text = R.string.draw_1,
                drawType = DrawType.Pick1,
                viewModel = viewModel,
                uiState = uiState
            )
            RadioButtonWithText(
                text = R.string.draw_2_pick_1,
                drawType = DrawType.Draw2Pick1,
                viewModel = viewModel,
                uiState = uiState
            )
            RadioButtonWithText(
                text = R.string.draw_3_pick_1,
                drawType = DrawType.Draw3Pick1,
                viewModel = viewModel,
                uiState = uiState
            )

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
                    viewModel.drawTempCards()
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
    viewModel: HideAndSeekViewModel,
    uiState: HideAndSeekUiState
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .selectable(
                selected = uiState.selectedDrawType == drawType,
                onClick = { viewModel.updateDrawType(drawType) }
            )
            .fillMaxWidth()
    ) {
        RadioButton(
            selected = uiState.selectedDrawType == drawType,
            onClick = { viewModel.updateDrawType(drawType) }
        )
        Text(stringResource(text))
    }
}

@Composable
fun SelectCardText() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.select_card),
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Composable
fun TooManyCardsText() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.too_many_cards),
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Preview
@Composable
fun DrawCardsScreenPreview() {
    DrawCardsScreen(viewModel(factory = AppViewModelProvider.Factory), HideAndSeekUiState(), {})
}