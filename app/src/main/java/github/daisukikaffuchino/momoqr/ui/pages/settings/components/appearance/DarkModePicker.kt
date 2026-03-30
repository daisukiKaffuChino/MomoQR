package github.daisukikaffuchino.momoqr.ui.pages.settings.components.appearance

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.logic.model.DarkMode
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.LazyRowSettingsItem
import github.daisukikaffuchino.momoqr.ui.theme.Defaults
import github.daisukikaffuchino.momoqr.ui.theme.shapeByInteraction
import github.daisukikaffuchino.momoqr.utils.VibrationUtil

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DarkModePicker(
    modifier: Modifier = Modifier,
    currentDarkMode: () -> DarkMode,
    onDarkModeChange: (darkMode: DarkMode) -> Unit,
) {
    val isInDarkTheme = isSystemInDarkTheme()

    val darkModeList = remember { DarkMode.entries.toList() }

    LazyRowSettingsItem(
        title = stringResource(R.string.pref_dark_mode),
        description = stringResource(R.string.pref_dark_mode_desc),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        fadedEdgeWidth = Defaults.fadedEdgeWidth,
        modifier = modifier
    ) {
        items(items = darkModeList, key = { it.id }) {
            val (contentColor, containerColor) = when (it) {
                DarkMode.FollowSystem -> if (isInDarkTheme) Color.White to Color.Black else Color.Black to Color.White
                DarkMode.Light -> Color.Black to Color.White
                DarkMode.Dark -> Color.White to Color.Black
            }

            val isSelected by remember { derivedStateOf { currentDarkMode() == it } }

            DarkModeItem(
                iconRes = it.iconRes,
                name = stringResource(it.nameRes),
                contentColor = contentColor,
                containerColor = containerColor,
                selected = isSelected,
                onSelect = { onDarkModeChange(it) })
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DarkModeItem(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    name: String,
    contentColor: Color,
    containerColor: Color,
    selected: Boolean,
    onSelect: () -> Unit,
    shapes: ButtonShapes = Defaults.shapes()
) {
    val view = LocalView.current

    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val animatedShape =
        shapeByInteraction(shapes, pressed, Defaults.shapesDefaultAnimationSpec)

    val borderWidth by animateDpAsState(if (selected) 3.dp else (-1).dp)
    Column(
        modifier = modifier
            .clip(animatedShape)
            .clickable(
                interactionSource = interactionSource,
                role = Role.Button,
                onClick = {
                    VibrationUtil.performHapticFeedback(view)
                    onSelect()
                }
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(MaterialTheme.shapes.large)
                .background(containerColor)
                .border(
                    width = borderWidth,
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.large
                ),
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.Center)
            )
        }

        Spacer(Modifier.size(8.dp))

        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}