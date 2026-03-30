@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package github.daisukikaffuchino.momoqr.ui.pages.settings.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import github.daisukikaffuchino.momoqr.ui.theme.Defaults
import github.daisukikaffuchino.momoqr.ui.theme.animatedShape
import github.daisukikaffuchino.momoqr.utils.VibrationUtil

@Composable
fun SettingsItem(
    modifier: Modifier = Modifier,
    @DrawableRes leadingIconRes: Int,
    title: String,
    description: String? = null,
    onClick: () -> Unit = {}
) = SettingsItem(
    leadingIcon = painterResource(leadingIconRes),
    title = title,
    description = description,
    trailingContent = null,
    onClick = onClick,
    modifier = modifier
)

@Composable
fun SettingsItem(
    modifier: Modifier = Modifier,
    @DrawableRes leadingIconRes: Int,
    title: String,
    shapes: ButtonShapes = Defaults.shapes(),
    description: String? = null,
    onClick: () -> Unit = {}
) = SettingsItem(
    leadingIcon = painterResource(leadingIconRes),
    title = title,
    shapes = shapes,
    description = description,
    trailingContent = null,
    onClick = onClick,
    modifier = modifier
)

@Composable
fun SettingsItem(
    modifier: Modifier = Modifier,
    leadingIcon: Painter? = null,
    title: String,
    description: String? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    background: Color = Defaults.Colors.Container,
    shapes: ButtonShapes = Defaults.shapes(),
    onClick: () -> Unit = {}
) = SettingsItem(
    leadingIcon = {
        leadingIcon?.let {
            Icon(
                painter = leadingIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(end = Defaults.settingsItemHorizontalPadding)
            )
        }
    },
    title = title,
    description = description,
    trailingContent = trailingContent,
    background = background,
    shapes = shapes,
    onClick = onClick,
    modifier = modifier
)

// Leading icon as ImageVector
@Composable
fun SettingsItem(
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    title: String,
    description: String? = null,
    onClick: () -> Unit = {}
) = SettingsItem(
    leadingIcon = leadingIcon,
    title = title,
    description = description,
    trailingContent = null,
    onClick = onClick,
    modifier = modifier
)

@Composable
fun SettingsItem(
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    title: String,
    description: String? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    background: Color = Defaults.Colors.Container,
    shapes: ButtonShapes = Defaults.shapes(),
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
    shapes = shapes,
    onClick = onClick,
    modifier = modifier
)

@Composable
fun SettingsItem(
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    title: String,
    description: String? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    background: Color = Defaults.Colors.Container,
    shapes: ButtonShapes = Defaults.shapes(),
    onClick: () -> Unit = {},
) = SettingsItem(
    modifier = modifier,
    leadingIcon = leadingIcon,
    headlineContent = {
        Text(
            text = title,
            maxLines = 2,
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
    onClick = onClick
)

@Composable
fun TertiarySettingsItem(
    modifier: Modifier = Modifier,
    leadingIconRes: Int,
    title: String? = null,
    description: String? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    background: Color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.65f),
    shapes: ButtonShapes = Defaults.largerShapes(),
    onClick: () -> Unit = {},
) = SettingsItem(
    modifier = modifier,
    leadingIcon = {
        Icon(
            painter = painterResource(leadingIconRes),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier.padding(end = Defaults.settingsItemHorizontalPadding),
        )
    },
    headlineContent = if (!title.isNullOrBlank()) {
        {
            Text(
                text = title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    fontSize = 18.sp
                )
            )
        }
    } else null,
    supportingContent = if (!description.isNullOrBlank()) {
        {
            Text(
                text = description,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.85f)
                )
            )
        }
    } else null,
    trailingContent = trailingContent,
    background = background,
    shapes = shapes,
    onClick = onClick
)

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
    onClick: () -> Unit = {},
) {
    val view = LocalView.current
    val userInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    Surface(
        onClick = {
            VibrationUtil.performHapticFeedback(view)
            onClick()
        },
        modifier = modifier.fillMaxWidth(),
        shape = animatedShape(shapes, userInteractionSource),
        color = background,
        interactionSource = userInteractionSource,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
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
}