package github.daisukikaffuchino.momoqr.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.ui.theme.Defaults
import github.daisukikaffuchino.momoqr.utils.VibrationUtil
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T> SingleChoiceBottomSheet(
    visible: Boolean,
    sheetState: SheetState,
    options: List<T>,
    selectedOption: T,
    onDismiss: suspend () -> Unit,
    onOptionClick: suspend (T) -> Unit,
    modifier: Modifier = Modifier,
    optionText: @Composable (T) -> Unit
) {
    val scope = rememberCoroutineScope()

    if (visible) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                scope.launch {
                    onDismiss()
                }
            }
        ) {
            Column(
                modifier = modifier.padding(
                    horizontal = Defaults.settingsItemVerticalPadding
                )
            ) {
                options.forEach { option ->
                    val selected = option == selectedOption

                    ListItem(
                        onClick = {
                            scope.launch {
                                onOptionClick(option)
                            }
                        },
                        selected = selected,
                        leadingContent = {
                            RadioButton(
                                selected = selected,
                                onClick = null
                            )
                        },
                        content = {
                            optionText(option)
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = BottomSheetDefaults.ContainerColor
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ConfirmDialog(
    modifier: Modifier = Modifier,
    visible: Boolean,
    @DrawableRes iconRes: Int,
    title: String = stringResource(R.string.title_warning),
    text: String,
    confirmButtonText: String = stringResource(R.string.action_confirm),
    showDismissButton: Boolean = true,
    dismissButtonText: String = stringResource(R.string.action_cancel),
    properties: DialogProperties = DialogProperties(),
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    BasicDialog(
        visible = visible,
        painter = painterResource(iconRes),
        title = title,
        text = { Text(text) },
        confirmButton = confirmButtonText,
        dismissButton = if (showDismissButton) dismissButtonText else null,
        onConfirm = {
            onConfirm()
            onDismiss()
        },
        onDismiss = onDismiss,
        properties = properties,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BasicDialog(
    modifier: Modifier = Modifier,
    visible: Boolean,
    painter: Painter,
    title: String,
    text: @Composable (() -> Unit)? = null,
    confirmButton: String,
    dismissButton: String? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    properties: DialogProperties = DialogProperties()
) {
    val view = LocalView.current
    BasicDialog(
        visible = visible,
        icon = {
            Icon(
                painter = painter,
                contentDescription = null // 会跟下面的文本重复，所以设置为 null
            )
        },
        title = { Text(title) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                text?.let { it() }
            }
        },
        confirmButton = {
            FilledTonalButton(
                onClick = {
                    VibrationUtil.performHapticFeedback(view)
                    onConfirm()
                },
                shapes = ButtonDefaults.shapes(
                    /*shape = ContinuousRoundedRectangle(50.dp),
                    pressedShape = ContinuousRoundedRectangle(12.dp)*/
                )
            ) {
                Text(confirmButton)
            }
        },
        dismissButton = {
            dismissButton?.let {
                TextButton(
                    onClick = {
                        VibrationUtil.performHapticFeedback(view)
                        onDismiss()
                    },
                    shapes = ButtonDefaults.shapes()
                ) {
                    Text(it)
                }
            }
        },
        onDismissRequest = onDismiss,
        properties = properties,
        modifier = modifier
    )
}

@Composable
fun BasicDialog(
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