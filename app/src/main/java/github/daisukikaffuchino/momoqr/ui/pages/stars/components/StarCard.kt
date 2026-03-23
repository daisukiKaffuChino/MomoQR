package github.daisukikaffuchino.momoqr.ui.pages.stars.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.ui.theme.Defaults
import github.daisukikaffuchino.momoqr.ui.theme.shapeByInteraction
import github.daisukikaffuchino.momoqr.utils.VibrationUtil
import github.daisukikaffuchino.momoqr.utils.toLocalDateString
import github.daisukikaffuchino.momoqr.utils.toRelativeTimeString

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StarCard(
    modifier: Modifier = Modifier,
    content: String,
    category: String,
    marked: Boolean,
    modDate: Long?,
    selected: Boolean,
    onCardClick: () -> Unit = {},
    onCardLongClick: () -> Unit = {},
    shapes: ButtonShapes = Defaults.largerShapes(),
) {
    val view = LocalView.current
    val cardColors = CardDefaults.cardColors(containerColor = Defaults.Colors.Container)
    val animatedContainerColor by animateColorAsState(targetValue = if (selected) MaterialTheme.colorScheme.secondaryContainer else cardColors.containerColor)

    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val animatedShape = shapeByInteraction(
        shapes = shapes,
        pressed = if (selected) true else pressed,
        animationSpec = Defaults.shapesDefaultAnimationSpec
    )

    val enterTransition = fadeIn(MaterialTheme.motionScheme.fastSpatialSpec()) + expandHorizontally(
        MaterialTheme.motionScheme.fastSpatialSpec()
    )
    val exitTransition = fadeOut(MaterialTheme.motionScheme.fastSpatialSpec()) + shrinkHorizontally(
        MaterialTheme.motionScheme.fastSpatialSpec()
    )

    val trailingPadding = when {
        !selected && marked -> 52.dp
        !selected -> 16.dp
        else -> 0.dp
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(animatedShape)
            .combinedClickable(
                interactionSource = interactionSource,
                onClick = {
                    VibrationUtil.performHapticFeedback(view)
                    onCardClick()
                },
                onLongClick = onCardLongClick
            )
            .drawBehind {
                drawRect(animatedContainerColor)
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = Defaults.screenHorizontalPadding,
                    end = trailingPadding
                )
        ) {
            AnimatedVisibility(
                visible = selected,
                enter = enterTransition,
                exit = exitTransition
            ) {
                Box(
                    Modifier
                        .padding(end = 12.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(6.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_check),
                        tint = contentColorFor(MaterialTheme.colorScheme.secondary),
                        contentDescription = stringResource(R.string.tip_selected)
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides cardColors.contentColor,
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (category == "") stringResource(R.string.label_unclassified) else category,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.primary
                        ),
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = content,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = modDate.toLocalDateString(false),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = modDate.toRelativeTimeString(LocalContext.current),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }


                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        AnimatedVisibility(
            visible = !selected && marked,
            enter = enterTransition,
            exit = exitTransition,
            modifier = Modifier.matchParentSize()
        ) {
            Box(
                contentAlignment = Alignment.CenterEnd,
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.secondaryContainer
                                )
                            )
                        )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_loyalty),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 12.dp, end = 16.dp)
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true)
@Composable
private fun Preview() {
    StarCard(
        content = "哇哇哇哇哇哇哇哇",
        category = "分类",
        marked = true,
        modDate = 0,
        selected = false,
        onCardClick = {},
        onCardLongClick = {}
    )
}
