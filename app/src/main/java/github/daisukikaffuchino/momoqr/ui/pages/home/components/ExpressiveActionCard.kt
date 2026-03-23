package github.daisukikaffuchino.momoqr.ui.pages.home.components

import android.graphics.Matrix
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import androidx.graphics.shapes.transformed
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.ui.theme.Defaults

@Preview
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveActionCard(
    modifier: Modifier = Modifier,
    onCookieClick: () -> Unit = {},
    onPillClick: () -> Unit = {}
) {
    val cookieShape = remember { MaterialShapes.Cookie4Sided.toComposeShape() }
    val pillShape = remember { MaterialShapes.Pill.toComposeShape() }

    Box(
        modifier = modifier.size(280.dp)
    ) {
        PressableShapeCard(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(160.dp),
            shape = pillShape,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            onClick = onPillClick
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
                    .padding(horizontal = 24.dp)
                    .offset(y = (-8).dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_photo_library),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.label_from_gallery),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 15.sp
                    ),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    textAlign = TextAlign.Center
                )
            }
        }

        PressableShapeCard(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(190.dp),
            shape = cookieShape,
            containerColor = MaterialTheme.colorScheme.secondary,
            onClick = onCookieClick
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
                    .padding(horizontal = 24.dp)
                    .offset(y = (-8).dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_photo_camera),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.label_from_camera),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 15.sp
                    ),
                    color = MaterialTheme.colorScheme.onSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun PressableShapeCard(
    modifier: Modifier = Modifier,
    shape: Shape,
    containerColor: Color,
    borderColor: Color = Defaults.Colors.Background,
    onClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    var pressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (pressed) 1.08f else 1f,
        animationSpec = tween(
            durationMillis = if (pressed) 90 else 160
        ),
        label = "card_scale"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                transformOrigin = TransformOrigin.Center
            }
            .pointerInput(onClick) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        val released = tryAwaitRelease()
                        pressed = false
                        if (released) onClick()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // 外层：描边
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
                .background(borderColor)
        ) {
            // 内层：内容卡片
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp) // 描边宽度
                    .clip(shape)
                    .background(containerColor),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    content = content
                )
            }
        }
    }
}

private fun RoundedPolygon.toComposeShape(): Shape {
    return GenericShape { size, _ ->
        val matrix = Matrix().apply {
            setScale(size.width, size.height)
        }

        val path = this@toComposeShape
            .normalized()
            .transformed(matrix)
            .toPath()
            .asComposePath()

        addPath(path)
    }
}