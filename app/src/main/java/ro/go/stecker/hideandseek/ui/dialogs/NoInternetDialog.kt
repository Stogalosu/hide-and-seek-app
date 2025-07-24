package ro.go.stecker.hideandseek.ui.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import ro.go.stecker.hideandseek.R

@Composable
fun NoInternetDialog(
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = { Icon(Icons.Rounded.Warning, tint = Color.Red, contentDescription = stringResource(R.string.no_internet)) },
        title = { Text(stringResource(R.string.no_internet)) },
        text = { Text(stringResource(R.string.no_internet_dialog)) },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.got_it))
            }
        }
    )
}