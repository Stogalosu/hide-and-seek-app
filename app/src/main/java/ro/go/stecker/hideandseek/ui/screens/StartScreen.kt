package ro.go.stecker.hideandseek.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ro.go.stecker.hideandseek.AppViewModelProvider
import ro.go.stecker.hideandseek.R
import ro.go.stecker.hideandseek.data.HideAndSeekUiState
import ro.go.stecker.hideandseek.data.HideAndSeekViewModel
import ro.go.stecker.hideandseek.getActivity
import ro.go.stecker.hideandseek.ui.HideAndSeekTopAppBar
import ro.go.stecker.hideandseek.ui.infraFontFamily
import ro.go.stecker.hideandseek.ui.navigation.HideAndSeekScreen

@Composable
fun StartScreen(
    onButtonClick: () -> Unit,
    viewModel: HideAndSeekViewModel,
    uiState: HideAndSeekUiState,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    BackHandler() {
        context.getActivity()?.finish()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            HideAndSeekTopAppBar(
                title = stringResource(R.string.app_name),
                canNavigateBack = false,
                currentScreen = HideAndSeekScreen.StartScreen,
                viewModel = viewModel
            )
        }
    ) { innerPadding ->
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
                    coroutineScope.launch {
                        viewModel.initAtGameStart()
                        delay(200)
                        onButtonClick()
                    }
                },

            ) {
                Text(stringResource(R.string.start_game), fontFamily = infraFontFamily)
            }
            Spacer(modifier = Modifier.height(64.dp))
        }

    }
}

@Composable
@Preview
fun StartScreenPreview() {
    StartScreen({}, viewModel(factory = AppViewModelProvider.Factory), HideAndSeekUiState())
}