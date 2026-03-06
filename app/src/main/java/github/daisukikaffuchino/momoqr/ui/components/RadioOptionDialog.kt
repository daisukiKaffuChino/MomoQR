package github.daisukikaffuchino.momoqr.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import github.daisukikaffuchino.momoqr.ui.theme.Defaults
import github.daisukikaffuchino.momoqr.ui.theme.shapeByInteraction
import github.daisukikaffuchino.momoqr.utils.VibrationUtils

data class RadioOptions(
    val id: Int,
    val text: String,
)

@Composable
fun RadioOptionDialog(
    visible: Boolean,
    title: String,
    currentOptions: RadioOptions,
    options: List<RadioOptions>,
    onSelect: (id: Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SimpleDialog(
        visible = visible,
        title = title,
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .selectableGroup()
                    .verticalScroll(rememberScrollState())
            ) {
                options.forEach { option ->
                    RadioItem(
                        selected = option == currentOptions,
                        text = option.text,
                        onClick = {
                            onSelect(option.id)
                            onDismiss()
                        }
                    )
                }
            }
        },
        onDismissRequest = onDismiss,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RadioItem(
    selected: Boolean,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    Row(
        modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(
                shapeByInteraction(
                    shapes = Defaults.largerShapes(),
                    pressed = pressed,
                    animationSpec = Defaults.shapesDefaultAnimationSpec
                )
            )
            .selectable(
                interactionSource = interactionSource,
                selected = selected,
                onClick = {
                    VibrationUtils.performHapticFeedback(view)
                    onClick()
                },
                role = Role.RadioButton
            )
            .padding(horizontal = Defaults.screenHorizontalPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
private fun SimpleDialog(
    modifier: Modifier = Modifier,
    visible: Boolean,
    title: String,
    text: @Composable (() -> Unit)? = null,
    onDismissRequest: () -> Unit = {}
) {
    BasicDialog(
        visible = visible,
        title = { Text(title) },
        text = text,
        confirmButton = {},
        dismissButton = {},
        onDismissRequest = onDismissRequest,
        modifier = modifier
    )
}



