package github.daisukikaffuchino.momoqr.ui.pages.settings.components.appearance

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.logic.datastore.DataStoreManager
import github.daisukikaffuchino.momoqr.logic.model.Languages
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.SettingsItem
import github.daisukikaffuchino.momoqr.ui.theme.Defaults

@OptIn( ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LanguageItem(
    dataStoreManager: DataStoreManager,
    onLanguageChange: (Languages.Language) -> Unit,
) {
    val languageCode by dataStoreManager.languageFlow
        .collectAsState(initial = null)

    val currentLanguage =
        Languages.Language.entries.find { it.code == languageCode }
            ?: Languages.Language.SYSTEM

    var showBottomSheet by retain { mutableStateOf(false) }

    SettingsItem(
        leadingIcon = painterResource(R.drawable.ic_language),
        title = stringResource(R.string.pref_language),
        description = currentLanguage.displayName(),
        onClick = { showBottomSheet = true }
    )

    if (showBottomSheet) {
        LanguageSelectionSheet(
            onDismiss = { showBottomSheet = false },
            language = currentLanguage,
            onLanguageChange = onLanguageChange,
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun LanguageSelectionSheet(
    onDismiss: () -> Unit,
    language: Languages.Language,
    onLanguageChange: (Languages.Language) -> Unit,
) {
    ModalBottomSheet(onDismissRequest = { onDismiss() }) {
        Column(Modifier.padding(horizontal = Defaults.settingsItemVerticalPadding)) {
            Languages.Language.entries
                .sortedBy { it.code }
                .forEach {
                    ListItem(
                        onClick = { onLanguageChange(it) },
                        selected = it == language,
                        leadingContent = { RadioButton(selected = it == language, onClick = null) },
                        content = { Text(text = it.displayName()) },
                        colors =
                            ListItemDefaults.colors(
                                containerColor = BottomSheetDefaults.ContainerColor
                            ),
                    )
                }
        }
    }
}

@Composable
private fun Languages.Language.displayName(): String {

    return when (this) {

        Languages.Language.SYSTEM -> stringResource(R.string.pref_language_follow_system)

        Languages.Language.ENGLISH -> "English"
        Languages.Language.CHINESE_SIMPLIFIED -> "简体中文"
        Languages.Language.CHINESE_TRADITIONAL -> "繁體中文"
    }
}