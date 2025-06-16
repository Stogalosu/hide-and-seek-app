package ro.go.stecker.hideandseek.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ro.go.stecker.hideandseek.R
import ro.go.stecker.hideandseek.data.Card
import ro.go.stecker.hideandseek.data.getImage

@Composable
fun CardImage(
    card: Card,
    clickable: Boolean,
    onClick: () -> Unit = {},
    imageModifier: Modifier,
    cardModifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = cardModifier
            .padding(8.dp)
            .clickable(
                enabled = clickable,
                onClick = onClick
            )
    ) {
        Image(
            painter = painterResource(card.getImage()),
            contentDescription = stringResource(R.string.card),
            modifier = imageModifier
        )
    }
}