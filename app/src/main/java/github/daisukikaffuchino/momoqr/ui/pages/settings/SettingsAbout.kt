package github.daisukikaffuchino.momoqr.ui.pages.settings

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import github.daisukikaffuchino.momoqr.BuildConfig
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.constants.AppConstants
import github.daisukikaffuchino.momoqr.logic.datastore.DataStoreManager
import github.daisukikaffuchino.momoqr.logic.model.Languages
import github.daisukikaffuchino.momoqr.ui.components.BasicDialog
import github.daisukikaffuchino.momoqr.ui.components.ListItemContainer
import github.daisukikaffuchino.momoqr.ui.components.TopAppBarScaffold
import github.daisukikaffuchino.momoqr.ui.components.segmentedGroup
import github.daisukikaffuchino.momoqr.ui.components.segmentedSection
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.SettingsItem
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.TertiarySettingsItem
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.appearance.ExpressiveLogoCard
import github.daisukikaffuchino.momoqr.utils.LinkOpener
import github.daisukikaffuchino.momoqr.utils.VibrationUtil
import github.daisukikaffuchino.momoqr.utils.appVersion
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("LocalContextConfigurationRead")
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsAbout(
    toLicencePage: () -> Unit,
    toDonatePage: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val languageCode by DataStoreManager.languageFlow.collectAsState(initial = null)
    val openInAppBrowser by DataStoreManager.openInAppBrowserFlow.collectAsState(initial = AppConstants.PREF_OPEN_IN_APP_BROWSER_DEFAULT)
    val context = LocalContext.current
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    var openPolicyDialog by rememberSaveable { mutableStateOf(false) }
    val systemLanguage = if (!context.resources.configuration.locales.isEmpty) {
        context.resources.configuration.locales[0].language
    } else ""

    val isChineseSimplified =
        languageCode == Languages.Language.CHINESE_SIMPLIFIED.code ||
                (languageCode == Languages.Language.SYSTEM.code && systemLanguage == "zh")

    TopAppBarScaffold(
        title = stringResource(R.string.pref_about),
        onBack = onNavigateUp,
        modifier = modifier,
    ) {
        val uriHandler = LocalUriHandler.current
        var clickCount by remember { mutableIntStateOf(0) }
        var lastClickTime by remember { mutableLongStateOf(0L) }

        LaunchedEffect(clickCount) {
            if (clickCount > 0) {
                lastClickTime = System.currentTimeMillis()
                val currentClickTime = lastClickTime
                delay(300L)

                if (currentClickTime == lastClickTime) {
                    clickCount = 0
                }
            }
        }

        ListItemContainer(Modifier.fillMaxWidth()) {
            item {
                ExpressiveLogoCard(
                    onClick = {
                        clickCount++
                        if (clickCount == 6) {
                            scope.launch { DataStoreManager.setShowLab(true) }
                            Toast.makeText(context, "\uD83C\uDF51", Toast.LENGTH_SHORT).show()
                            clickCount = 0
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.size(8.dp))
            }

            item {
                TertiarySettingsItem(
                    leadingIconRes = R.drawable.ic_redeem,
                    title = stringResource(R.string.pref_donate),
                    description = stringResource(R.string.pref_donate_desc),
                    onClick = { toDonatePage() }
                )
            }

            item {
                Spacer(modifier = Modifier.size(8.dp))
            }

            segmentedSection(R.string.pref_label_info) {
                segmentedGroup {
                    SettingsItem(
                        leadingIconRes = R.drawable.ic_code,
                        title = context.appVersion(),
                        description = BuildConfig.BUILD_TYPE,
                        onClick = {}
                    )
                    SettingsItem(
                        leadingIconRes = R.drawable.ic_person,
                        title = stringResource(R.string.pref_developer),
                        description = "GitHub@daisukiKaffuChino",
                        onClick = {
                            LinkOpener.open(
                                context = context,
                                uriHandler = uriHandler,
                                url = AppConstants.DEVELOPER_GITHUB,
                                useCustomTabs = openInAppBrowser
                            )
                        },
                    )
                    SettingsItem(
                        leadingIconRes = R.drawable.ic_policy,
                        title = stringResource(R.string.pref_policy),
                        description = stringResource(R.string.pref_policy_desc),
                        onClick = { openPolicyDialog = true },
                    )

                    // 简中限定
                    if (isChineseSimplified) {
                        SettingsItem(
                            leadingIconRes = R.drawable.ic_license,
                            title = "ICP 备案号",
                            description = "沪ICP备xxxxxxxx号",
                            onClick = {
                                LinkOpener.open(
                                    context = context,
                                    uriHandler = uriHandler,
                                    url = AppConstants.ICP_BEIAN,
                                    useCustomTabs = openInAppBrowser
                                )
                            },
                        )
                    }
                }
            }

            segmentedSection(R.string.pref_label_others) {
                segmentedGroup {
                    SettingsItem(
                        leadingIconRes = R.drawable.ic_github,
                        title = stringResource(R.string.pref_view_on_github),
                        description = stringResource(R.string.pref_view_on_github_desc),
                        onClick = {
                            LinkOpener.open(
                                context = context,
                                uriHandler = uriHandler,
                                url = AppConstants.GITHUB_REPO,
                                useCustomTabs = openInAppBrowser
                            )
                        },
                    )
                    SettingsItem(
                        leadingIconRes = R.drawable.ic_gavel,
                        title = stringResource(R.string.pref_licence),
                        description = stringResource(R.string.pref_licence_desc),
                        onClick = toLicencePage
                    )
                }
            }
        }
        BasicDialog(
            visible = openPolicyDialog,
            title = { Text(stringResource(R.string.pref_policy)) },
            text = {
                val policyText = privacyPolicyText(context, isChineseSimplified)
                //val htmlText = HtmlCompat.fromHtml(policyText, HtmlCompat.FROM_HTML_MODE_LEGACY)
                val htmlText= AnnotatedString.fromHtml(policyText)
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    SelectionContainer {
                        Text(text = htmlText)
                    }
                }
            },
            confirmButton = {
                FilledTonalButton(
                    onClick = {
                        openPolicyDialog = false
                        VibrationUtil.performHapticFeedback(view)
                    },
                    shapes = ButtonDefaults.shapes()
                ) { Text(stringResource(R.string.action_confirm)) }
            },
            onDismissRequest = { openPolicyDialog = false }
        )
    }
}

@Composable
private fun privacyPolicyText(context: Context, isChineseSimplified: Boolean): String {
    val fileName = if (isChineseSimplified)
        "privacy_policy_cn.txt"
    else
        "privacy_policy.txt"
    val text = remember {
        context.assets.open(fileName)
            .bufferedReader()
            .use { it.readText() }
    }
    return text
}