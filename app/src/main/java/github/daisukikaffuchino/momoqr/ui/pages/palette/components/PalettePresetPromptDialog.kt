package github.daisukikaffuchino.momoqr.ui.pages.palette.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.ui.components.BasicDialog

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun PalettePresetPromptDialog(
    visible: Boolean,
    initialName: String = "",
    onSave: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val textFieldState = rememberTextFieldState(initialText = initialName)
    var isError by rememberSaveable { mutableStateOf(false) }
    var supportingText by remember { mutableStateOf("") }

    LaunchedEffect(visible, initialName) {
        if (visible) {
            textFieldState.setTextAndPlaceCursorAtEnd(initialName)
            isError = false
            supportingText = ""
        }
    }

    BasicDialog(
        visible = visible,
        painter = painterResource(R.drawable.ic_archive),
        title = stringResource(R.string.title_save_preset),
        text = {
            Text(stringResource(R.string.tip_palette_save_preset))
            Spacer(Modifier.size(4.dp))
            OutlinedTextField(
                state = textFieldState,
                lineLimits = TextFieldLineLimits.SingleLine,
                label = { Text(stringResource(R.string.placeholder_palette_preset_name)) },
                supportingText = {
                    Text(
                        supportingText.ifEmpty {
                            stringResource(R.string.tip_palette_name_supporting)
                        }
                    )
                },
                isError = isError,
            )
        },
        confirmButton = stringResource(R.string.action_save),
        dismissButton = stringResource(R.string.action_cancel),
        onConfirm = {
            val trimmed = textFieldState.text.trim().toString()
            when {
                trimmed.isEmpty() -> {
                    isError = true
                    supportingText = context.getString(R.string.tip_palette_name_empty)
                }

                trimmed.length > 24 -> {
                    isError = true
                    supportingText = context.getString(R.string.tip_palette_name_too_long)
                }

                else -> {
                    onSave(trimmed)
                    textFieldState.clearText()
                    onDismiss()
                }
            }
        },
        onDismiss = onDismiss,
        properties = DialogProperties(),
    )
}
