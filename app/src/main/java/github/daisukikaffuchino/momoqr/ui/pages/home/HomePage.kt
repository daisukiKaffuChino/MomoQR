package github.daisukikaffuchino.momoqr.ui.pages.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.zxing.BarcodeFormat
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.constants.AppConstants
import github.daisukikaffuchino.momoqr.logic.database.StarEntity
import github.daisukikaffuchino.momoqr.logic.datastore.DataStoreManager
import github.daisukikaffuchino.momoqr.ui.components.ItemTitleText
import github.daisukikaffuchino.momoqr.ui.components.TopAppBarScaffold
import github.daisukikaffuchino.momoqr.ui.pages.home.components.ExpressiveActionCard
import github.daisukikaffuchino.momoqr.ui.pages.home.components.GenerateActionCard
import github.daisukikaffuchino.momoqr.ui.pages.home.components.PaletteCard
import github.daisukikaffuchino.momoqr.ui.pages.home.components.ScanFromCameraCard
import github.daisukikaffuchino.momoqr.ui.pages.home.components.ScanFromGalleryCard
import github.daisukikaffuchino.momoqr.ui.theme.Defaults
import github.daisukikaffuchino.momoqr.ui.viewmodels.SharedViewModel
import github.daisukikaffuchino.momoqr.utils.QrReaderUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("LocalContextGetResourceValueCall")
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomePage(
    toScanPage: () -> Unit,
    toPalettePage: () -> Unit,
    toResultAddPage: (StarEntity) -> Unit,
    toFactoryPage: () -> Unit,
    modifier: Modifier = Modifier
) {

    val sharedViewModel = hiltViewModel<SharedViewModel>()
    val scanResult by sharedViewModel.scanResult.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val homeClassicCard by DataStoreManager.homeClassicCardFlow.collectAsState(initial = AppConstants.PREF_HOME_CLASSIC_CARD_DEFAULT)
    val codeFormats by DataStoreManager.barcodeFormatsFlow.collectAsState(
        initial = setOf(
            BarcodeFormat.QR_CODE
        )
    )

    LaunchedEffect(scanResult) {
        scanResult?.let {
            sharedViewModel.clearScanResult()
            val correctionLevel = DataStoreManager.correctionLevelFlow.first()
            toResultAddPage(
                StarEntity(
                    content = it,
                    errorCorrectionLevel = correctionLevel
                )
            )
        }
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted)
                toScanPage()
            else {
                val permanentlyDenied =
                    !ActivityCompat.shouldShowRequestPermissionRationale(
                        context as Activity,
                        Manifest.permission.CAMERA
                    )
                scope.launch {
                    if (permanentlyDenied) {
                        val result = snackbarHostState.showSnackbar(
                            message = context.getString(R.string.toast_camera_permission_required),
                            actionLabel = context.getString(R.string.toast_go_to_settings),
                            duration = SnackbarDuration.Long
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            val intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", context.packageName, null)
                            )
                            context.startActivity(intent)
                        }
                    } else snackbarHostState.showSnackbar(
                        message = context.getString(R.string.toast_camera_permission_required)
                    )

                }
            }
        }

    val openGalleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        scope.launch(Dispatchers.IO) {
            val result = QrReaderUtil.scanImageFromGallery(context, uri, codeFormats.toList())
            withContext(Dispatchers.Main) {
                if (result != null) {
                    val correctionLevel = DataStoreManager.correctionLevelFlow.first()
                    toResultAddPage(
                        StarEntity(
                            content = result.text,
                            errorCorrectionLevel = correctionLevel
                        )
                    )
                } else {
                    Toast.makeText(context, R.string.toast_no_data_detected, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    TopAppBarScaffold(
        title = stringResource(R.string.page_home),
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        modifier = modifier,
    ) {
        val configuration = LocalConfiguration.current

        LazyVerticalStaggeredGrid(
            modifier = Modifier
                .fillMaxSize(),
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ItemTitleText(R.string.label_scan)
                    if (homeClassicCard) {
                        ScanFromCameraCard(
                            onClick = {
                                requestCameraPermissionIfNeeded(
                                    context = context,
                                    permissionLauncher = permissionLauncher,
                                    onGranted = toScanPage
                                )
                            }
                        )
                        ScanFromGalleryCard(
                            onClick = {
                                openGalleryLauncher.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            }
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            ExpressiveActionCard(
                                onCookieClick = {
                                    requestCameraPermissionIfNeeded(
                                        context = context,
                                        permissionLauncher = permissionLauncher,
                                        onGranted = toScanPage
                                    )
                                },
                                onPillClick = {
                                    openGalleryLauncher.launch(
                                        PickVisualMediaRequest(
                                            ActivityResultContracts.PickVisualMedia.ImageOnly
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ItemTitleText(R.string.label_generate)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Defaults.settingsSegmentedItemPadding),
                        modifier = modifier.clip(MaterialTheme.shapes.largeIncreased)
                    ) {
                        GenerateActionCard(
                            icon = painterResource(R.drawable.ic_edit_square),
                            title = stringResource(R.string.label_generate_text),
                            //onClick = { uriHandler.openUri(AppConstants.GITHUB_REPO) },
                        )
                        GenerateActionCard(
                            icon = painterResource(R.drawable.ic_content_paste),
                            title = stringResource(R.string.label_generate_from_clip_board),
                            //onClick = toLicencePage
                        )
                        GenerateActionCard(
                            icon = painterResource(R.drawable.ic_more),
                            title = stringResource(R.string.label_generate_more_type),
                            onClick = toFactoryPage
                        )
                    }
                    PaletteCard(
                        modifier = Modifier.padding(top = 8.dp),
                        onClick = toPalettePage
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

private fun requestCameraPermissionIfNeeded(
    context: Context,
    permissionLauncher: ActivityResultLauncher<String>,
    onGranted: () -> Unit
) {
    val granted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    if (granted) {
        onGranted()
    } else {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }
}
