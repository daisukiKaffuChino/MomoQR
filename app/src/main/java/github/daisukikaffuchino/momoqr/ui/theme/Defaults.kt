package github.daisukikaffuchino.momoqr.ui.theme

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
object Defaults {
    /**
     * 屏幕左右两边预留边距
     */
    val screenHorizontalPadding = 16.dp

    /**
     * 屏幕上下预留边距
     */
    val screenVerticalPadding = 8.dp

    /**
     * 设置项水平边距
     */
    val settingsItemHorizontalPadding = 24.dp

    /**
     * 设置项垂直边距
     */
    val settingsItemVerticalPadding = 16.dp

    val settingsItemPadding = 4.dp
    val settingsSegmentedItemPadding = 2.dp

    val homeScanCardHeight = 110.dp

    val ScreenContainerShape: Shape
        @Composable
        get() = MaterialTheme.shapes.largeIncreased

    object Colors {
        val Container: Color
            @Composable
            get() = MaterialTheme.colorScheme.surfaceBright

        val Background: Color
            @Composable
            get() = MaterialTheme.colorScheme.surfaceContainer

        val PrimaryMix: Color
            @Composable
            get() = lerp(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.surfaceDim,
                0.25f
            )

        val SecondaryMix: Color
            @Composable
            get() = lerp(
                MaterialTheme.colorScheme.secondary,
                MaterialTheme.colorScheme.surfaceDim,
                0.25f
            )

    }

    val fadedEdgeWidth = 8.dp

    val defaultShape: CornerBasedShape
        @Composable
        get() = MaterialTheme.shapes.extraSmall

    val largeCornerShape: CornerBasedShape
        @Composable
        get() = MaterialTheme.shapes.largeIncreased

    val pressedShape: CornerBasedShape
        @Composable
        get() = MaterialTheme.shapes.small

    @Composable
    fun shapes() = ButtonDefaults.shapes(
        shape = defaultShape,
        pressedShape = pressedShape
    )

    @Composable
    fun largerShapes() = ButtonDefaults.shapes(
        shape = largeCornerShape,
        pressedShape = pressedShape
    )

    val shapesDefaultAnimationSpec: FiniteAnimationSpec<Float>
        @Composable
        get() = MaterialTheme.motionScheme.defaultEffectsSpec()
}