package github.daisukikaffuchino.momoqr.ui.pages.home

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.ui.components.TopAppBarScaffold
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.SettingsContainer
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.settingsSection
import github.daisukikaffuchino.momoqr.ui.theme.Defaults
import github.daisukikaffuchino.momoqr.ui.theme.animatedShape
import github.daisukikaffuchino.momoqr.ui.theme.shapeByInteraction

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomePage(
    modifier: Modifier = Modifier
) {
    TopAppBarScaffold(
        title = stringResource(R.string.page_home),
        modifier = modifier,
    ) {
        val configuration = LocalConfiguration.current
        Column(Modifier.fillMaxWidth()) {
            LazyVerticalStaggeredGrid(
                modifier = Modifier.fillMaxSize(),
                columns = when (configuration.orientation) {
                    Configuration.ORIENTATION_LANDSCAPE ->
                        StaggeredGridCells.Fixed(2)

                    else ->
                        StaggeredGridCells.Fixed(1)
                },
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalItemSpacing = 10.dp
            ) {
                item {
                    ScanFromCameraCard { }
                }
                item {
                    ScanFromCameraCard { }
                }
                item {
                    ScanFromCameraCard { }
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ScanFromCameraCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val animatedShape = animatedShape(Defaults.largerShapes(), interactionSource)
    val cardColors = CardDefaults.cardColors(containerColor = Defaults.Colors.Container)
    Card(
        modifier = modifier.height(Defaults.overviewCardHeight),
        colors = cardColors,
        shape = animatedShape
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    enabled = true,
                    onClick = onClick,
                    interactionSource = interactionSource
                )
        ) {
            Button(
                onClick = { /* TODO */ },
                //modifier = Modifier.align(Alignment.BottomEnd) // 右下角对齐
            ) {
                Text("Click")
            }
        }
    }
}