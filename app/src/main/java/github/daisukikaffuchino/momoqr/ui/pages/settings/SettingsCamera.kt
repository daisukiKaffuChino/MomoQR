package github.daisukikaffuchino.momoqr.ui.pages.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.zxing.BarcodeFormat
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.constants.Constants
import github.daisukikaffuchino.momoqr.logic.datastore.DataStoreManager
import github.daisukikaffuchino.momoqr.ui.components.CardListItemContainer
import github.daisukikaffuchino.momoqr.ui.components.TopAppBarScaffold
import github.daisukikaffuchino.momoqr.ui.components.segmentedGroup
import github.daisukikaffuchino.momoqr.ui.components.segmentedSection
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.appearance.CodeFormatsSheet
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.SettingsItem
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.SwitchSettingsItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsCamera(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val codeFormats by DataStoreManager.barcodeFormatsFlow.collectAsState(
        initial = setOf(
            BarcodeFormat.QR_CODE
        )
    )
    val scope = rememberCoroutineScope()
    var showFormatsSheet by remember { mutableStateOf(false) }

    if (showFormatsSheet) {
        CodeFormatsSheet(
            selectedFormats = codeFormats,
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

    val switchCamera by DataStoreManager.switchCameraFlow.collectAsState(initial = Constants.PREF_SWITCH_CAMERA_DEFAULT)
    val beepSound by DataStoreManager.beepSoundFlow.collectAsState(initial = Constants.PREF_BEEP_SOUND_DEFAULT)
    val enhancedPreprocess by DataStoreManager.enhancedPreprocessFlow.collectAsState(initial = Constants.PREF_ENHANCED_PREPROCESSING_DEFAULT)

    TopAppBarScaffold(
        title = stringResource(R.string.pref_camera),
        onBack = onNavigateUp,
        modifier = modifier,
    ) {

        CardListItemContainer(Modifier.fillMaxWidth()) {
            segmentedSection(R.string.pref_label_camera) {
                segmentedGroup {
                    SwitchSettingsItem(
                        checked = switchCamera,
                        leadingIconRes = R.drawable.ic_cameraswitch,
                        title = stringResource(R.string.pref_switch_camera),
                        description = stringResource(R.string.pref_switch_camera_desc),
                        onCheckedChange = { scope.launch { DataStoreManager.setSwitchCamera(it) } }
                    )
                }
            }

            segmentedSection(R.string.pref_label_scanner) {
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
        }

    }
}