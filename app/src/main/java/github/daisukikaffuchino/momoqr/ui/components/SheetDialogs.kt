package github.daisukikaffuchino.momoqr.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties

@Composable
fun BasicSheetDialog(
    modifier: Modifier = Modifier,
    visible: Boolean,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable () -> Unit,
    text: @Composable (() -> Unit)? = null,
    confirmButton: (@Composable () -> Unit),
    dismissButton: (@Composable () -> Unit)? = null,
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties()
) {
    if (visible) {
        AlertDialog(
            icon = icon,
            title = title,
            text = text,
            confirmButton = confirmButton,
            dismissButton = dismissButton,
            onDismissRequest = onDismissRequest,
            properties = properties,
            modifier = modifier
        )
    }
}