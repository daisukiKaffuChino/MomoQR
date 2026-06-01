package github.daisukikaffuchino.momoqr.ui.pages.home.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.logic.datastore.DataStoreManager
import github.daisukikaffuchino.momoqr.logic.model.QrCodeECL
import github.daisukikaffuchino.momoqr.ui.theme.Defaults
import github.daisukikaffuchino.momoqr.utils.VibrationUtil
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TextInputSheet(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var stringText by rememberSaveable { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {

        Column(
            Modifier.padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.label_enter_content),
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        VibrationUtil.performHapticFeedback(view)
                        scope.launch {
                            if (isTextInvalid(stringText))
                                Toast.makeText(
                                    context,
                                    R.string.error_no_content_entered_or_too_long,
                                    Toast.LENGTH_SHORT
                                ).show()
                            else
                                onConfirm(stringText)
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_send),
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.label_generate))
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clip(MaterialTheme.shapes.largeIncreased)
                    .background(Defaults.Colors.Container)
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp, bottom = 12.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { stringText = it },
                    value = stringText,
                    minLines = 4,
                    maxLines = 8,
                    shape = RoundedCornerShape(12.dp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    ElevatedAssistChip(
                        modifier = Modifier
                            .height(36.dp),
                        border = null,
                        elevation = null,
                        shape = RoundedCornerShape(14.dp),
                        onClick = { stringText = "" },
                        leadingIcon = {
                            Icon(
                                modifier = Modifier.size(FilterChipDefaults.IconSize),
                                painter = painterResource(R.drawable.ic_cancel),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(R.string.action_clear),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                maxLines = 1
                            )
                        }
                    )
                }
            }

        }
    }
}

private suspend fun isTextInvalid(qrContent: String): Boolean {
    val qr = qrContent.trim()
    val ecl = DataStoreManager.correctionLevelFlow.first()
    val qrMaxBytes = QrCodeECL.fromFloat(ecl).getQrMaxBytes()
    val qrBytesLength = qr.toByteArray(Charsets.UTF_8).size

    val isErrorContent = when {
        qr.isEmpty() -> true
        qrBytesLength > qrMaxBytes -> true
        else -> false
    }

    return isErrorContent
}