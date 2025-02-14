package ro.go.stecker.hideandseek.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import ro.go.stecker.hideandseek.R
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import ro.go.stecker.hideandseek.HideAndSeekTopAppBar
import  androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import ro.go.stecker.hideandseek.data.CardsRepository
import ro.go.stecker.hideandseek.data.HideAndSeekUiState
import ro.go.stecker.hideandseek.data.HideAndSeekViewModel

@Composable
fun HiderDeckScreen(
    onDrawCards: () -> Unit,
    viewModel: HideAndSeekViewModel,
    uiState: HideAndSeekUiState,
    modifier: Modifier = Modifier
) {
    uiState.drawnTempCards.clear()
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    Scaffold(
        modifier = modifier,
        topBar = {
            HideAndSeekTopAppBar(
                title = stringResource(R.string.hider_deck),
                canNavigateBack = false
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
            contentPadding = if(isPortrait) PaddingValues() else innerPadding
        )

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiderDeck(
    viewModel: HideAndSeekViewModel,
    uiState: HideAndSeekUiState,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    modifier: Modifier = Modifier
) {
    if(uiState.isDeleteMenuActive) {
        AlertDialog(
            onDismissRequest = { viewModel.updateDeleteMenu() },
            icon = { Icon(Icons.Rounded.Delete, contentDescription = stringResource(R.string.delete_card)) },
            title = { Text(stringResource(R.string.delete_card)) },
            text = { Text(stringResource(R.string.delete_card_question, stringResource(uiState.cardDeck[uiState.cardToDelete].name))) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateDeleteMenu()
                        if(uiState.cardDeck[uiState.cardToDelete].name == R.string.curse_overflowing_chalice) viewModel.updateOverflowingChalice()
                        viewModel.deleteCardAtIndex(uiState.cardToDelete)
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
    if(!uiState.cardDeck.isEmpty()) {
        LazyRow(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            items(items = uiState.cardDeck) { item ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable(
                            onClick = {
                                viewModel.updateDeleteMenu()
                                viewModel.setIndexToDelete(uiState.cardDeck.indexOf(item))
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
    HiderDeckScreen({ CardsRepository[0] }, HideAndSeekViewModel(), HideAndSeekUiState(), )
}