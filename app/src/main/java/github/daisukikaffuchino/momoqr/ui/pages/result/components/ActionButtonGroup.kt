package github.daisukikaffuchino.momoqr.ui.pages.result.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.ui.theme.Defaults
import github.daisukikaffuchino.momoqr.ui.theme.animatedShape

@Composable
fun ActionButtonGroup(
    onSearch: () -> Unit,
    onShareText: () -> Unit,
    onSaveImage: () -> Unit,
    onCopyContent: () -> Unit,
    onOpenLink: () -> Unit,
    isUrl: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isUrl) {
            ActionButton(
                icon = painterResource(R.drawable.ic_link),
                label = stringResource(R.string.action_open_link),
                onClick = onOpenLink,
                modifier = Modifier.weight(1f)
            )
        } else {
            ActionButton(
                icon = painterResource(R.drawable.ic_search),
                label = stringResource(R.string.action_search),
                onClick = onSearch,
                modifier = Modifier.weight(1f)
            )
        }

        ActionButton(
            icon = painterResource(R.drawable.ic_content_copy),
            label = stringResource(R.string.action_copy),
            onClick = onCopyContent,
            modifier = Modifier.weight(1f)
        )

        ActionButton(
            icon = painterResource(R.drawable.ic_ios_share),
            label = stringResource(R.string.action_share),
            onClick = onShareText,
            modifier = Modifier.weight(1f)
        )

        ActionButton(
            icon = painterResource(R.drawable.ic_archive),
            label = stringResource(R.string.action_export),
            onClick = onSaveImage,
            modifier = Modifier.weight(1f)
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ActionButton(
    modifier: Modifier = Modifier,
    icon: Painter,
    label: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    FilledTonalButton(
        modifier = modifier.height(64.dp),
        onClick = onClick,
        interactionSource = interactionSource,
        shape = animatedShape(Defaults.largerShapes(), interactionSource),
        contentPadding = PaddingValues(8.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = Defaults.Colors.Container
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Icon(
                painter = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp)
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ActionButtonGroup(
        onSearch = {},
        onShareText = {},
        onCopyContent = {},
        onSaveImage = {},
        onOpenLink = {},
        isUrl = false
    )
}