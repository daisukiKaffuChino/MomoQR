package github.daisukikaffuchino.momoqr.ui.pages.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.zxing.BarcodeFormat
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.constants.AppConstants
import github.daisukikaffuchino.momoqr.logic.datastore.DataStoreManager
import github.daisukikaffuchino.momoqr.logic.model.QrCodeECL
import github.daisukikaffuchino.momoqr.logic.model.QrRenderQuality
import github.daisukikaffuchino.momoqr.ui.components.ListItemContainer
import github.daisukikaffuchino.momoqr.ui.components.SingleChoiceBottomSheet
import github.daisukikaffuchino.momoqr.ui.components.TopAppBarScaffold
import github.daisukikaffuchino.momoqr.ui.components.segmentedGroup
import github.daisukikaffuchino.momoqr.ui.components.segmentedSection
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.SettingsItem
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.SettingsPlainBox
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.SwitchSettingsItem
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.appearance.CodeFormatsSheet
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.appearance.ErrorCorrectionPicker
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SettingsCamera(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val codeFormats by DataStoreManager.barcodeFormatsFlow.collectAsState(
        initial = emptySet()
    )
    val switchCamera by DataStoreManager.switchCameraFlow.collectAsState(initial = AppConstants.PREF_SWITCH_CAMERA_DEFAULT)
    val beepSound by DataStoreManager.beepSoundFlow.collectAsState(initial = AppConstants.PREF_BEEP_SOUND_DEFAULT)
    val enhancedPreprocess by DataStoreManager.enhancedPreprocessFlow.collectAsState(initial = AppConstants.PREF_ENHANCED_PREPROCESSING_DEFAULT)
    val autoCopy by DataStoreManager.autoCopyFlow.collectAsState(initial = AppConstants.PREF_AUTO_COPY_DEFAULT)
    val qrRenderQuality by DataStoreManager.qrRenderQualityFlow.collectAsState(initial = QrRenderQuality.BALANCED)
    val correctionLevel by DataStoreManager.correctionLevelFlow.collectAsState(initial = AppConstants.PREF_CORRECTION_LEVEL_DEFAULT)

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var showFormatsSheet by rememberSaveable { mutableStateOf(false) }
    var showQualityDialog by rememberSaveable { mutableStateOf(false) }

    TopAppBarScaffold(
        title = stringResource(R.string.pref_camera),
        onBack = onNavigateUp,
        modifier = modifier,
    ) {
        ListItemContainer(Modifier.fillMaxWidth()) {
            segmentedSection(R.string.pref_label_camera) {
                segmentedGroup {
                    SwitchSettingsItem(
                        checked = switchCamera,
                        leadingIconRes = R.drawable.ic_cameraswitch,
                        title = stringResource(R.string.pref_front_camera),
                        description = stringResource(R.string.pref_front_camera_desc),
                        onCheckedChange = { scope.launch { DataStoreManager.setSwitchCamera(it) } }
                    )
                }
            }

            segmentedSection(R.string.pref_label_analyzer) {
                segmentedGroup {
                    SettingsItem(
                        leadingIconRes = R.drawable.ic_barcode_scanner,
                        title = stringResource(R.string.pref_identification_type),
                        description = stringResource(R.string.pref_identification_type_desc),
                        onClick = {
                            showFormatsSheet = true
                        }
                    )
                    SwitchSettingsItem(
                        checked = autoCopy,
                        leadingIconRes = R.drawable.ic_content_copy,
                        title = stringResource(R.string.pref_auto_copy),
                        description = stringResource(R.string.pref_auto_copy_desc),
                        onCheckedChange = { scope.launch { DataStoreManager.setAutoCopy(it) } }
                    )
                    SwitchSettingsItem(
                        checked = beepSound,
                        leadingIconRes = R.drawable.ic_notification_sound,
                        title = stringResource(R.string.pref_beep),
                        description = stringResource(R.string.pref_beep_desc),
                        onCheckedChange = { scope.launch { DataStoreManager.setBeepSound(it) } }
                    )
                    SwitchSettingsItem(
                        checked = enhancedPreprocess,
                        leadingIconRes = R.drawable.ic_image_compare,
                        title = stringResource(R.string.pref_enhanced_preprocessing),
                        description = stringResource(R.string.pref_enhanced_preprocessing_desc),
                        onCheckedChange = { scope.launch { DataStoreManager.setEnhancedPreprocess(it) } }
                    )
                }
            }

            segmentedSection(R.string.pref_label_rendering) {
                segmentedGroup {
                    SettingsItem(
                        leadingIconRes = R.drawable.ic_low_density,
                        title = stringResource(R.string.pref_qr_render_quality),
                        description = stringResource(qrRenderQuality.labelRes()),
                        onClick = { showQualityDialog = true }
                    )
                    ErrorCorrectionPicker(
                        currentLevel = QrCodeECL.fromFloat(correctionLevel),
                        onLevelChanged = { scope.launch { DataStoreManager.setCorrectionLevel(it.value) } }
                    )
                }
            }
            item {
                SettingsPlainBox(stringResource(R.string.tip_may_cause_rendering_issues))
            }
        }

        if (showFormatsSheet) {
            CodeFormatsSheet(
                selectedFormats = codeFormats.ifEmpty { setOf(BarcodeFormat.QR_CODE) },
                onFormatsChange = { newFormats ->
                    scope.launch {
                        DataStoreManager.setCodeFormats(newFormats)
                    }
                },
                onDismiss = {
                    showFormatsSheet = false
                }
            )
        }
        SingleChoiceBottomSheet(
            visible = showQualityDialog,
            sheetState = sheetState,
            options = QrRenderQuality.entries,
            selectedOption = qrRenderQuality,
            onDismiss = {
                sheetState.hide()
                showQualityDialog = false
            },
            onOptionClick = { option ->
                DataStoreManager.setQrRenderQuality(option)
                sheetState.hide()
                showQualityDialog = false
            },
            optionText = { option ->
                Text(text = stringResource(option.labelRes()))
            }
        )

    }
}