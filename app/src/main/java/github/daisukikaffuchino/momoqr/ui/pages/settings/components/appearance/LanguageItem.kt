package github.daisukikaffuchino.momoqr.ui.pages.settings.components.appearance

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.logic.datastore.DataStoreManager
import github.daisukikaffuchino.momoqr.logic.model.Languages
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.SettingsItem

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
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
        description = stringResource(R.string.pref_language_desc),
        trailingContent = {
            Text(
                currentLanguage.displayName(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.widthIn(max = 85.dp)
            )
        },
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
        Column(modifier = Modifier.padding(horizontal = 12.dp)) {
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
private fun getSystemDisplayLabel(): String {

    val configuration = LocalConfiguration.current
    val systemLocale = configuration.locales[0]

    val appLocales = AppCompatDelegate.getApplicationLocales()
    val isFollowingSystem = appLocales.isEmpty

    // 如果当前就是跟随系统 → 不显示括号
    if (isFollowingSystem) {
        return stringResource(R.string.pref_language_follow_system)
    }

    // 用当前 App 语言显示系统语言
    val displayLocale = LocalLocale.current.platformLocale

    val languageName = systemLocale
        .getDisplayLanguage(displayLocale)
        .replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(displayLocale) else it.toString()
        }

    val countryName = systemLocale.getDisplayCountry(displayLocale)

    val prettyName =
        if (countryName.isNotBlank())
            "$languageName – $countryName"
        else
            languageName

    return "${stringResource(R.string.pref_language_follow_system)} ($prettyName)"
}

@Composable
private fun Languages.Language.displayName(): String {

    return when (this) {

        Languages.Language.SYSTEM -> getSystemDisplayLabel()

        Languages.Language.ENGLISH -> "English"
        Languages.Language.CHINESE_SIMPLIFIED -> "简体中文"
        Languages.Language.CHINESE_TRADITIONAL -> "繁體中文"
    }
}