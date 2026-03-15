package github.daisukikaffuchino.momoqr.ui.pages.settings

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.ui.components.TopAppBarScaffold
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.SettingsPlainBox
import github.daisukikaffuchino.momoqr.ui.theme.Defaults
import github.daisukikaffuchino.momoqr.utils.VibrationUtil
import github.daisukikaffuchino.momoqr.utils.launchWeChat

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsAboutDonate(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    TopAppBarScaffold(
        title = stringResource(R.string.pref_donate),
        onBack = onNavigateUp,
        modifier = modifier,
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(if (isLandscape) 2 else 1),
            verticalItemSpacing = 16.dp,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Column {
                    Text(
                        text = stringResource(R.string.pref_label_via_wechat),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(horizontal = Defaults.screenVerticalPadding)
                            .padding(top = Defaults.settingsItemPadding)
                    )
                    Spacer(modifier = Modifier.size(12.dp))
                    OutlinedCard(
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        AsyncImage(
                            model = R.drawable.img_donate_wechat,
                            contentDescription = stringResource(R.string.pref_label_via_wechat),
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    SettingsPlainBox(
                        text = stringResource(R.string.tip_donate_via_wechat),
                        modifier = Modifier.weight(1f)
                    )

                    FilledIconButton(
                        shapes = IconButtonDefaults.shapes(),
                        onClick = {
                            VibrationUtil.performHapticFeedback(view)
                            context.launchWeChat()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_wechat),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}