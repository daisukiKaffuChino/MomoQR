package github.daisukikaffuchino.momoqr.ui.pages.home

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.ui.components.TopAppBarScaffold
import github.daisukikaffuchino.momoqr.ui.components.segmentedGroup
import github.daisukikaffuchino.momoqr.ui.components.segmentedSection
import github.daisukikaffuchino.momoqr.ui.pages.home.components.GenerateActionCard
import github.daisukikaffuchino.momoqr.ui.pages.home.components.ScanFromCameraCard
import github.daisukikaffuchino.momoqr.ui.pages.home.components.ScanFromGalleryCard
import github.daisukikaffuchino.momoqr.ui.theme.Defaults
import github.daisukikaffuchino.momoqr.ui.viewmodels.SharedViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomePage(
    toScanPage: () -> Unit,
    modifier: Modifier = Modifier
) {

    val sharedViewModel = hiltViewModel<SharedViewModel>()
    val scanResult by sharedViewModel.scanResult.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(scanResult) {
        scanResult?.let {
            Toast.makeText(
                context,
                "扫描结果: $it",
                Toast.LENGTH_SHORT
            ).show()

            sharedViewModel.clearScanResult()
        }
    }

    TopAppBarScaffold(
        title = stringResource(R.string.page_home),
        modifier = modifier,
    ) {
        val configuration = LocalConfiguration.current

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Defaults.settingsItemPadding)
        ) {

            item{
                Text(
                    text = stringResource(R.string.label_scan),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(
                        horizontal = Defaults.screenVerticalPadding,
                        vertical = Defaults.settingsItemPadding
                    )
                )
            }

            item{
                LazyVerticalStaggeredGrid(
                    modifier = when (configuration.orientation) {
                        Configuration.ORIENTATION_LANDSCAPE ->
                            Modifier.fillMaxWidth().height(112.dp)

                        else ->
                            Modifier.fillMaxWidth().height(220.dp)
                    },
                    columns = when (configuration.orientation) {
                        Configuration.ORIENTATION_LANDSCAPE ->
                            StaggeredGridCells.Fixed(2)

                        else ->
                            StaggeredGridCells.Fixed(1)
                    },
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalItemSpacing = 8.dp
                ) {
                    item {
                        ScanFromCameraCard(
                            onClick = toScanPage
                        )
                    }
                    item {
                        ScanFromGalleryCard()
                    }
                }
            }

            segmentedSection(R.string.label_generate) {
                segmentedGroup {
                    GenerateActionCard(
                        icon = painterResource(R.drawable.ic_edit_square),
                        title = stringResource(R.string.label_generate_text),
                        //onClick = { uriHandler.openUri(Constants.GITHUB_REPO) },
                    )
                    GenerateActionCard(
                        icon = painterResource(R.drawable.ic_content_paste),
                        title = stringResource(R.string.label_generate_from_clip_board),
                        //onClick = toLicencePage
                    )
                    GenerateActionCard(
                        icon = painterResource(R.drawable.ic_more),
                        title = stringResource(R.string.label_generate_more_type),
                        //onClick = toLicencePage
                    )
                }
            }

        }

    }

}
