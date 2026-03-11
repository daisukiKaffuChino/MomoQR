package github.daisukikaffuchino.momoqr.ui.pages.scan.components

import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.SurfaceRequest
import androidx.camera.viewfinder.core.ImplementationMode
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButtonShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.utils.VibrationUtils

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CodeScanner(
    surfaceRequest: SurfaceRequest?,
    isFlashEnabled: Boolean,
    onCloseClicked: () -> Unit,
    onToggleFlash: () -> Unit,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }

    Box(modifier = modifier) {
        surfaceRequest?.let { request ->
            CameraXViewfinder(
                implementationMode = ImplementationMode.EMBEDDED,
                surfaceRequest = request,
                modifier = Modifier.fillMaxSize()
            )
        }

        ScanOverlay()

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .padding(
                    start = paddingValues.calculateStartPadding(LayoutDirection.Ltr) +
                            24.dp,
                    top = paddingValues.calculateTopPadding() + 24.dp,
                    end = paddingValues.calculateEndPadding(LayoutDirection.Ltr) +
                            24.dp
                )
        ) {
            FilledIconButton(
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
                shapes = IconButtonDefaults.shapes(),
                onClick = {
                    VibrationUtils.performHapticFeedback(view)
                    onCloseClicked()
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = stringResource(R.string.action_back)
                )
            }
            FilledTonalIconToggleButton(
                checked = isFlashEnabled,
                onCheckedChange = {
                    VibrationUtils.performHapticFeedback(view)
                    onToggleFlash()
                },
                interactionSource = interactionSource,
                shapes = IconToggleButtonShapes(
                    shape = RoundedCornerShape(28.dp),
                    pressedShape = RoundedCornerShape(8.dp),
                    checkedShape = RoundedCornerShape(12.dp)
                )
            ) {
                Icon(
                    painter =
                        if (isFlashEnabled) painterResource(R.drawable.ic_flashlight_off)
                        else painterResource(R.drawable.ic_flashlight_on),
                    contentDescription = stringResource(R.string.tip_toggle_flashlight)
                )
            }
        }
        Text(
            text = stringResource(R.string.tip_align_to_scan),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = Color.White,
            modifier = Modifier
                .padding(bottom = paddingValues.calculateBottomPadding() + 4.dp)
                .align(Alignment.BottomCenter)
        )
    }
}