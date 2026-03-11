package github.daisukikaffuchino.momoqr.ui.pages.result.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import github.daisukikaffuchino.momoqr.utils.VibrationUtils

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ResultFloatingActionButton(
    text: String,
    @DrawableRes iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    expanded: Boolean = true,
    shape: Shape = FloatingActionButtonDefaults.smallExtendedFabShape,
    containerColor: Color = FloatingActionButtonDefaults.containerColor,
    contentColor: Color = contentColorFor(containerColor),
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    interactionSource: MutableInteractionSource? = null,
) {
    val view = LocalView.current
    SmallExtendedFloatingActionButton(
        text = {
            Text(
                text = text,
                maxLines = 1,
                overflow = TextOverflow.Clip
            )
        },
        icon = {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null
            )
        },
        onClick = {
            VibrationUtils.performHapticFeedback(view)
            onClick()
        },
        elevation = elevation,
        containerColor = containerColor,
        contentColor = contentColor,
        shape = shape,
        interactionSource = interactionSource,
        modifier = modifier
    )
}