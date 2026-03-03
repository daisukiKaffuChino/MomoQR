package github.daisukikaffuchino.momoqr.ui.pages.settings.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.daisukikaffuchino.momoqr.ui.theme.Defaults
import github.daisukikaffuchino.momoqr.ui.theme.animatedShape
import github.daisukikaffuchino.momoqr.ui.theme.shapeByInteraction
import github.daisukikaffuchino.momoqr.utils.VibrationUtils

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsItem(
    modifier: Modifier = Modifier,
    @DrawableRes leadingIconRes: Int,
    title: String,
    description: String? = null,
    enableClick: Boolean = true,
    onClick: () -> Unit = {}
) = SettingsItem(
    leadingIcon = painterResource(leadingIconRes),
    title = title,
    description = description,
    trailingContent = null,
    enableClick = enableClick,
    onClick = onClick,
    modifier = modifier
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsItem(
    modifier: Modifier = Modifier,
    @DrawableRes leadingIconRes: Int,
    title: String,
    shapes: ButtonShapes = Defaults.shapes(),
    description: String? = null,
    enableClick: Boolean = true,
    onClick: () -> Unit = {}
) = SettingsItem(
    leadingIcon = painterResource(leadingIconRes),
    title = title,
    shapes = shapes,
    description = description,
    trailingContent = null,
    enableClick = enableClick,
    onClick = onClick,
    modifier = modifier
)


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsItem(
    modifier: Modifier = Modifier,
    leadingIcon: Painter? = null,
    title: String,
    description: String? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    background: Color = Defaults.Colors.Container,
    shapes: ButtonShapes = Defaults.shapes(),
    enableClick: Boolean = true,
    onClick: () -> Unit = {}
) = SettingsItem(
    leadingIcon = {
        leadingIcon?.let {
            /*Box(
                modifier = Modifier
                    .padding(end = TodoDefaults.settingsItemHorizontalPadding)
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialShapes.Cookie6Sided.toShape()
                    )
                    .size(35.dp),
                contentAlignment = Alignment.Center
            ) {*/
            Icon(
                painter = leadingIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(end = Defaults.settingsItemHorizontalPadding)
            )
            // }
        }
    },
    title = title,
    description = description,
    trailingContent = trailingContent,
    background = background,
    shapes = shapes,
    enableClick = enableClick,
    onClick = onClick,
    modifier = modifier
)

// Leading icon as ImageVector
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsItem(
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    title: String,
    description: String? = null,
    enableClick: Boolean = true,
    onClick: () -> Unit = {}
) = SettingsItem(
    leadingIcon = leadingIcon,
    title = title,
    description = description,
    trailingContent = null,
    enableClick = enableClick,
    onClick = onClick,
    modifier = modifier
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsItem(
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    title: String,
    description: String? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    background: Color = Defaults.Colors.Container,
    shapes: ButtonShapes = Defaults.shapes(),
    enableClick: Boolean = true,
    onClick: () -> Unit = {}
) = SettingsItem(
    leadingIcon = {
        leadingIcon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(end = Defaults.settingsItemHorizontalPadding),
            )
        }
    },
    title = title,
    description = description,
    trailingContent = trailingContent,
    background = background,
    enableClick = enableClick,
    shapes = shapes,
    onClick = onClick,
    modifier = modifier
)


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsItem(
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    title: String,
    description: String? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    background: Color = Defaults.Colors.Container,
    shapes: ButtonShapes = Defaults.shapes(),
    enableClick: Boolean = true,
    onClick: () -> Unit = {},
) = SettingsItem(
    modifier = modifier,
    leadingIcon = leadingIcon,
    headlineContent = {
        Text(
            text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp
            )
        )
    },
    supportingContent = {
        description?.let {
            Text(
                text = it,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    },
    trailingContent = trailingContent,
    background = background,
    shapes = shapes,
    enableClick = enableClick,
    onClick = onClick
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsItem(
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    headlineContent: (@Composable () -> Unit)? = null,
    supportingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    background: Color = Defaults.Colors.Container,
    shapes: ButtonShapes = Defaults.shapes(),
    interactionSource: MutableInteractionSource? = null,
    enableClick: Boolean = true,
    onClick: () -> Unit = {},
) {
    val view = LocalView.current
    val userInteractionSource = interactionSource ?: remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(animatedShape(shapes, userInteractionSource))
            .clickable(
                interactionSource = userInteractionSource,
                enabled = enableClick,
                onClick = {
                    VibrationUtils.performHapticFeedback(view)
                    onClick()
                }
            )
            .background(background)
            .padding(
                horizontal = Defaults.settingsItemHorizontalPadding,
                vertical = Defaults.settingsItemVerticalPadding
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingIcon?.let { it() }

        Column(modifier = Modifier.weight(1f)) {
            headlineContent?.let { it() }
            supportingContent?.let { it() }
        }

        trailingContent?.let { it() }
    }
}