package ro.go.stecker.hideandseek.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ro.go.stecker.hideandseek.ui.navigation.HideAndSeekNavHost
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import ro.go.stecker.hideandseek.AppViewModelProvider
import ro.go.stecker.hideandseek.R
import ro.go.stecker.hideandseek.data.HideAndSeekViewModel
import ro.go.stecker.hideandseek.ui.navigation.HideAndSeekScreen
import kotlin.math.exp

@Composable
fun HideAndSeekApp(navController: NavHostController = rememberNavController()) {
    HideAndSeekNavHost(navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HideAndSeekTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = {},
    currentScreen: HideAndSeekScreen,
    viewModel: HideAndSeekViewModel,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        title = { Text(text = title, fontFamily = infraFontFamily) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onSecondary
        ),
        navigationIcon = {
            if(canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        },
        actions = { TopAppBarDropdownMenu(currentScreen, { viewModel.endGame() }) }
    )
}

@Composable
fun TopAppBarDropdownMenu(
    currentScreen: HideAndSeekScreen,
    onEndGame: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var confirmDialog by remember { mutableStateOf(false) }

    if(currentScreen != HideAndSeekScreen.StartScreen) {
        IconButton(
            onClick = { expanded = !expanded }
        ) {
            Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.more_options))
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.end_game)) },
                leadingIcon = { Icon(Icons.Rounded.Close, contentDescription = null) },
                onClick = {
                    expanded = false
                    confirmDialog = true
                }
            )
        }
    }

    if(confirmDialog) {
        AlertDialog(
            onDismissRequest = { confirmDialog = false },
            icon = { Icon(Icons.Rounded.Close, contentDescription = null) },
            title = { Text(stringResource(R.string.end_game)) },
            text = { Text(stringResource(R.string.end_game_question)) },
            confirmButton = {
                TextButton(
                    onClick = { onEndGame() }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { confirmDialog = false }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Preview
@Composable
fun TopAppBarPreview() {
    HideAndSeekTopAppBar("Hide and Seek", false, {}, HideAndSeekScreen.StartScreen, viewModel(factory = AppViewModelProvider.Factory))
}