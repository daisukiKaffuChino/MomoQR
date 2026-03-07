package github.daisukikaffuchino.momoqr.ui.pages.settings.components.appearance

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.ui.theme.Defaults
import kotlin.collections.plus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeFormatsSheet(
    selectedFormats: Set<BarcodeFormat>,
    onFormatsChange: (Set<BarcodeFormat>) -> Unit,
    onDismiss: () -> Unit
) {

    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    var currentSelection by remember {
        mutableStateOf(selectedFormats + BarcodeFormat.QR_CODE)
    }
    val supportedFormats = listOf(
        BarcodeFormat.QR_CODE,
        BarcodeFormat.AZTEC,
        BarcodeFormat.DATA_MATRIX,
        BarcodeFormat.CODE_128,
        BarcodeFormat.CODE_93,
        BarcodeFormat.CODE_39,
        BarcodeFormat.EAN_13,
        BarcodeFormat.EAN_8,
        BarcodeFormat.ITF,
        BarcodeFormat.PDF_417,
        BarcodeFormat.UPC_A,
        BarcodeFormat.UPC_E
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {

        Column(Modifier.Companion.padding(horizontal = Defaults.settingsItemVerticalPadding)) {

            Text(
                text = stringResource(R.string.pref_identification_type),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.Companion.height(Defaults.settingsItemVerticalPadding))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                supportedFormats.forEach { format ->

                    val isQr = format == BarcodeFormat.QR_CODE

                    FilterChip(
                        selected = currentSelection.contains(format),

                        onClick = {

                            if (isQr) {
                                Toast.makeText(
                                    context,
                                    R.string.toast_qr_code_format_can_not_remove,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                return@FilterChip
                            }

                            currentSelection =
                                if (currentSelection.contains(format))
                                    currentSelection - format
                                else
                                    currentSelection + format

                            onFormatsChange(currentSelection)
                        },

                        label = {
                            Text(format.name)
                        }
                    )
                }
            }

        }
    }
}