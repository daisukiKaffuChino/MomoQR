package github.daisukikaffuchino.momoqr.ui.pages.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.ui.theme.Defaults
import github.daisukikaffuchino.momoqr.ui.theme.animatedShape
import github.daisukikaffuchino.momoqr.utils.VibrationUtil

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ScanFromCameraCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val animatedShape = animatedShape(Defaults.largerShapes(), interactionSource)
    val cardColors = CardDefaults.cardColors(containerColor = Defaults.Colors.PrimaryMix)
    Card(
        modifier = modifier.height(Defaults.homeScanCardHeight),
        colors = cardColors,
        shape = animatedShape
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    enabled = true,
                    onClick = onClick,
                    interactionSource = interactionSource
                )
        ) {
            Image(
                painter = painterResource(R.drawable.ic_kid_star),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.08f)),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-40).dp, y = 30.dp)
                    .rotate(-15f)
                    .size(180.dp)
            )
            Image(
                painter = painterResource(R.drawable.ic_ac_unit),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.04f)),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 140.dp, y = (-50).dp)
                    .rotate(10f)
                    .size(140.dp)
            )
            Image(
                painter = painterResource(R.drawable.ic_momoi),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(
                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.65f)
                ),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp)
                    .height(90.dp)
            )

            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 16.dp, start = 24.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_photo_camera),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = stringResource(R.string.label_from_camera),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(top = Defaults.screenVerticalPadding)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ScanFromGalleryCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val animatedShape = animatedShape(Defaults.largerShapes(), interactionSource)
    val cardColors = CardDefaults.cardColors(containerColor = Defaults.Colors.SecondaryMix)
    Card(
        modifier = modifier.height(Defaults.homeScanCardHeight),
        colors = cardColors,
        shape = animatedShape
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    enabled = true,
                    onClick = onClick,
                    interactionSource = interactionSource
                )
        ) {
            Image(
                painter = painterResource(R.drawable.ic_favorite),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.08f)),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 30.dp, y = (-20).dp)
                    .rotate(15f)
                    .size(140.dp)
            )
            Image(
                painter = painterResource(R.drawable.ic_explosion),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.04f)),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-120).dp, y = 55.dp)
                    .rotate(-10f)
                    .size(140.dp)
            )
            Image(
                painter = painterResource(R.drawable.ic_midori),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(
                    MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.65f)
                ),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 24.dp)
                    .height(90.dp)
            )

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.label_from_gallery),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSecondary
                )
                Icon(
                    painter = painterResource(R.drawable.ic_photo_library),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .size(24.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GenerateActionCard(
    modifier: Modifier = Modifier,
    icon: Painter,
    title: String,
    onClick: () -> Unit = {},
) {
    val view = LocalView.current
    val userInteractionSource = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(animatedShape(Defaults.shapes(), userInteractionSource))
            .clickable(
                interactionSource = userInteractionSource,
                onClick = {
                    VibrationUtil.performHapticFeedback(view)
                    onClick()
                }
            )
            .background(Defaults.Colors.Container)
            .padding(
                horizontal = Defaults.settingsItemHorizontalPadding,
                vertical = Defaults.settingsItemVerticalPadding
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = Defaults.settingsItemHorizontalPadding),
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }

    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PaletteCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val animatedShape = animatedShape(Defaults.largerShapes(), interactionSource)

    Card(
        modifier = modifier.height(Defaults.homeScanCardHeight),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = animatedShape
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    onClick = onClick,
                    interactionSource = interactionSource
                )
                .padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 8.dp, y = 4.dp)
                    .size(56.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = CircleShape
                    )
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 42.dp, y = 20.dp)
                    .size(32.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                        shape = CircleShape
                    )
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 20.dp, y = 50.dp)
                    .size(20.dp)
                    .background(
                        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.08f),
                        shape = CircleShape
                    )
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(72.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_palette),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(34.dp)
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(end = 88.dp)
            ) {
                Text(
                    text = stringResource(R.string.label_generate_color_palette),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    text = stringResource(R.string.label_generate_color_palette_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.76f),
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 4.dp, end = 4.dp)
            ) {
                PaletteMiniChip(MaterialTheme.colorScheme.primary)
                PaletteMiniChip(MaterialTheme.colorScheme.secondary)
                PaletteMiniChip(MaterialTheme.colorScheme.tertiary)
            }
        }
    }
}

@Composable
private fun PaletteMiniChip(color: Color) {
    Box(
        modifier = Modifier
            .size(width = 18.dp, height = 10.dp)
            .clip(RoundedCornerShape(80.dp))
            .background(color)
    )
}

@Preview
@Composable
private fun Preview() {
    Column {
        ScanFromCameraCard {}
        ScanFromGalleryCard {}
        PaletteCard {}
    }
}