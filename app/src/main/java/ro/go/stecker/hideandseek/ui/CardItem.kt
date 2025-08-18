package ro.go.stecker.hideandseek.ui

import androidx.annotation.IntRange
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ro.go.stecker.hideandseek.R
import ro.go.stecker.hideandseek.data.Card
import ro.go.stecker.hideandseek.data.getImage

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CardItem(
    card: Card,
    buttons: @Composable () -> Unit,
    onDetailsClick: (String) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
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

                buttons()

            }
        }
    }
}

@Composable
fun ButtonWithIcon(
    icon: ImageVector,
    @StringRes text: Int,
    color: Color = Color.White,
    @IntRange(0, 26) iconSize: Int =  26,
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
                    .padding((26 - iconSize).dp)
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

@Composable
fun ButtonWithIcon(
    icon: Painter,
    @StringRes text: Int,
    color: Color = Color.White,
    @IntRange(0, 26) iconSize: Int =  26,
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
                painter = icon,
                contentDescription = stringResource(text),
                tint = color,
                modifier = Modifier
                    .size(26.dp)
                    .padding((26 - iconSize).dp)
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