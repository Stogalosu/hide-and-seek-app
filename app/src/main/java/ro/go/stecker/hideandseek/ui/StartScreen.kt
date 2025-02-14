package ro.go.stecker.hideandseek.ui

import androidx.annotation.Dimension
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ro.go.stecker.hideandseek.HideAndSeekTopAppBar
import ro.go.stecker.hideandseek.R
import ro.go.stecker.hideandseek.data.HideAndSeekUiState
import ro.go.stecker.hideandseek.data.HideAndSeekViewModel

@Composable
fun StartScreen(
    navigateToDeck: () -> Unit,
    viewModel: HideAndSeekViewModel,
    uiState: HideAndSeekUiState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            HideAndSeekTopAppBar(
                title = stringResource(R.string.app_name),
                canNavigateBack = false,
            )
        }
    ) {innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.welcome_1),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 32.dp)
            )
            Text(
                text = stringResource(R.string.welcome_2),
                textAlign = TextAlign.Justify,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    navigateToDeck()
                    viewModel.init()
                },

            ) {
                Text(stringResource(R.string.start_round), fontFamily = infraFontFamily)
            }
            Spacer(modifier = Modifier.height(64.dp))
        }

    }
}

@Composable
@Preview
fun StartScreenPreview() {
    StartScreen({}, HideAndSeekViewModel(), HideAndSeekUiState())
}