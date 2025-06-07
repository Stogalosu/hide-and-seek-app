package ro.go.stecker.hideandseek.ui.screens

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ro.go.stecker.hideandseek.R
import ro.go.stecker.hideandseek.data.Card
import ro.go.stecker.hideandseek.ui.CardImage

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DetailsScreen(
    card: Card,
    onBackClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    with(sharedTransitionScope) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .sharedElement(
                    sharedTransitionScope.rememberSharedContentState(key = "card-${card.id}"),
                    animatedVisibilityScope = animatedVisibilityScope
                )
                .fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(48.dp))

                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = stringResource(R.string.back_button))
                    }
                    Text(
                        text = stringResource(card.name),
                        fontSize = 28.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                BoxWithConstraints {
                    if(maxHeight < 500.dp) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            with(sharedTransitionScope) {
                                CardImage(
                                    card = card,
                                    clickable = false,
                                    imageModifier = Modifier
                                        .padding(5.dp)
                                        .sharedElement(
                                            sharedTransitionScope.rememberSharedContentState(key = "image-${card.id}"),
                                            animatedVisibilityScope = animatedVisibilityScope
                                        )
                                        .clip(RoundedCornerShape(2)),
                                    cardModifier = Modifier
                                        .sharedElement(
                                            sharedTransitionScope.rememberSharedContentState(key = "border-${card.id}"),
                                            animatedVisibilityScope = animatedVisibilityScope
                                        )
                                )
                            }
                            if(card.descrption != 0) {
                                Text(
                                    text = stringResource(card.descrption),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .width(324.dp)
                                )
                            }
                        }
                    } else {
                        Column {
                            with(sharedTransitionScope) {
                                CardImage(
                                    card = card,
                                    clickable = false,
                                    imageModifier = Modifier
                                        .padding(5.dp)
                                        .sharedElement(
                                            sharedTransitionScope.rememberSharedContentState(key = "image-${card.id}"),
                                            animatedVisibilityScope = animatedVisibilityScope
                                        )
                                        .clip(RoundedCornerShape(2)),
                                    cardModifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .sharedElement(
                                            sharedTransitionScope.rememberSharedContentState(key = "border-${card.id}"),
                                            animatedVisibilityScope = animatedVisibilityScope
                                        )
                                )
                            }
                            if(card.descrption != 0) {
                                Text(
                                    text = stringResource(card.descrption),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}