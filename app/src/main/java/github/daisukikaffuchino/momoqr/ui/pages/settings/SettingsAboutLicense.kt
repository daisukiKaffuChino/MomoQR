package github.daisukikaffuchino.momoqr.ui.pages.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.util.author
import com.mikepenz.aboutlibraries.ui.compose.util.htmlReadyLicenseContent
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.ui.components.BasicDialog
import github.daisukikaffuchino.momoqr.ui.components.EmptyListTip
import github.daisukikaffuchino.momoqr.ui.components.EmptyTipType
import github.daisukikaffuchino.momoqr.ui.components.ListItemContainer
import github.daisukikaffuchino.momoqr.ui.components.TopAppBarScaffold
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.SettingsItem
import github.daisukikaffuchino.momoqr.ui.components.SearchTextField
import github.daisukikaffuchino.momoqr.ui.components.SearchTopAppBar
import github.daisukikaffuchino.momoqr.ui.theme.Defaults
import github.daisukikaffuchino.momoqr.ui.theme.fadeScale
import github.daisukikaffuchino.momoqr.utils.VibrationUtil

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsAboutLicence(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    data class DisplayLicense(
        val name: String,
        val url: String? = null,
        val content: String? = null
    )

    data class SelectedLicenseDialog(
        val libraryName: String,
        val licenseName: String,
        val content: String
    )

    data class LicenseItem(
        val library: com.mikepenz.aboutlibraries.entity.Library,
        val displayLicenses: List<DisplayLicense>
    )

    fun buildDisplayLicenses(library: com.mikepenz.aboutlibraries.entity.Library): List<DisplayLicense> {
        val parsed = library.licenses.map { license ->
            DisplayLicense(
                name = license.name.ifBlank { "Unknown License" },
                url = license.url,
                content = license.htmlReadyLicenseContent?.takeIf { it.isNotBlank() }
                    ?: license.licenseContent?.takeIf { it.isNotBlank() }
            )
        }

        if (parsed.isNotEmpty()) return parsed

        val name = library.name.lowercase()
        val artifactId = library.artifactId.lowercase()
        val author = library.author.lowercase()

        val isUbuntuFont = name.contains("ubuntu") ||
                artifactId.contains("ubuntu") ||
                author.contains("canonical")

        return if (isUbuntuFont) {
            listOf(
                DisplayLicense(
                    name = "Ubuntu Font Licence 1.0",
                    url = "https://ubuntu.com/legal/font-licence",
                    content = "    <p>This font is licensed under the Ubuntu Font Licence 1.0.</p>\n" +
                            "    <p>Full license text:<br>\n" +
                            "    <a href=\"https://ubuntu.com/legal/font-licence\" target=\"_blank\">\n" +
                            "        https://ubuntu.com/legal/font-licence"
                )
            )
        } else {
            emptyList()
        }
    }

    val libraries by produceLibraries(R.raw.aboutlibraries)
    val view = LocalView.current
    val uriHandler = LocalUriHandler.current
    var searchMode by rememberSaveable { mutableStateOf(false) }
    val searchFieldState = rememberTextFieldState()
    val transitionSpec = fadeScale()
    var selectedLicenseDialog by rememberSaveable(
        stateSaver = listSaver(
            save = {
                it?.let { dialog ->
                    listOf(dialog.libraryName, dialog.licenseName, dialog.content)
                } ?: emptyList()
            },
            restore = {
                it.takeIf { saved -> saved.size == 3 }?.let { saved ->
                    SelectedLicenseDialog(
                        libraryName = saved[0],
                        licenseName = saved[1],
                        content = saved[2]
                    )
                }
            }
        )
    ) { mutableStateOf<SelectedLicenseDialog?>(null) }

    val licenseItems = remember(libraries) {
        libraries?.libraries.orEmpty().map { library ->
            LicenseItem(
                library = library,
                displayLicenses = buildDisplayLicenses(library)
            )
        }
    }

    val filteredLicenseItems = remember(licenseItems, searchMode, searchFieldState.text) {
        if (!searchMode) {
            licenseItems
        } else {
            val keyword = searchFieldState.text.toString()
            licenseItems.filter { item ->
                item.library.name.contains(keyword, ignoreCase = true) ||
                        item.library.author.contains(keyword, ignoreCase = true) ||
                        item.library.artifactId.contains(keyword, ignoreCase = true) ||
                        item.displayLicenses.any { license ->
                            license.name.contains(keyword, ignoreCase = true) ||
                                    license.url.orEmpty().contains(keyword, ignoreCase = true) ||
                                    license.content.orEmpty().contains(keyword, ignoreCase = true)
                        }
            }
        }
    }

    TopAppBarScaffold(
        topBar = {
            SearchTopAppBar(
                title = stringResource(R.string.pref_licence),
                searchMode = searchMode,
                selectedMode = false,
                selectedAll = false,
                selectedIds = emptyList(),
                onSearchModeChange = { searchMode = it },
                onNavigateUp = onNavigateUp,
                onCancelSelect = {},
                onSelectAll = {},
                onDeleteSelected = {}
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier
    ) {
        Column {
            AnimatedVisibility(
                visible = searchMode,
                enter = fadeIn(MaterialTheme.motionScheme.fastEffectsSpec()) + expandVertically(
                    MaterialTheme.motionScheme.fastSpatialSpec()
                ),
                exit = fadeOut(MaterialTheme.motionScheme.fastEffectsSpec()) + shrinkVertically(
                    MaterialTheme.motionScheme.fastSpatialSpec()
                ),
            ) {
                SearchTextField(
                    searchMode = searchMode,
                    onSearchModeChange = { searchMode = it },
                    textFieldState = searchFieldState
                )
            }

            AnimatedContent(
                targetState = filteredLicenseItems.isEmpty(),
                transitionSpec = { transitionSpec }
            ) { isEmpty ->
                if (isEmpty) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        EmptyListTip(
                            type = if (searchMode) EmptyTipType.Search else EmptyTipType.List,
                            size = 96.dp
                        )

                        Text(
                            text = stringResource(
                                if (searchMode) R.string.tip_search_not_found else R.string.tip_no_item
                            ),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                } else {
                    ListItemContainer(Modifier.fillMaxSize()) {
                        items(
                            items = filteredLicenseItems,
                            key = { it.library.uniqueId }
                        ) { item ->
                            val library = item.library
                            val displayLicenses = item.displayLicenses

                            SettingsItem(
                                shapes = Defaults.largerShapes(),
                                headlineContent = {
                                    Column {
                                        Text(
                                            text = library.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        val author = library.author
                                        if (author.isNotBlank()) {
                                            Text(
                                                text = author,
                                                style = MaterialTheme.typography.bodyMediumEmphasized
                                            )
                                        }
                                    }
                                },
                                supportingContent = {
                                    if (displayLicenses.isNotEmpty()) {
                                        FlowRow {
                                            displayLicenses.forEach { license ->
                                                Text(
                                                    text = license.name,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    style = MaterialTheme.typography.bodySmallEmphasized,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                },
                                onClick = {
                                    val license =
                                        displayLicenses.firstOrNull() ?: return@SettingsItem

                                    when {
                                        !license.content.isNullOrBlank() ->
                                            selectedLicenseDialog = SelectedLicenseDialog(
                                                libraryName = library.name,
                                                licenseName = license.name,
                                                content = license.content
                                            )

                                        !license.url.isNullOrBlank() ->
                                            try {
                                                uriHandler.openUri(license.url)
                                            } catch (_: Throwable) {
                                            }
                                    }
                                }
                            )

                        }
                    }
                }
            }
        }

        BasicDialog(
            visible = selectedLicenseDialog != null,
            title = {
                Text(
                    selectedLicenseDialog?.licenseName
                        ?: selectedLicenseDialog?.libraryName.orEmpty()
                )
            },
            text = {
                val content = selectedLicenseDialog?.content
                if (!content.isNullOrBlank()) {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        SelectionContainer {
                            val htmlText = AnnotatedString.fromHtml(content)
                            Text(text = htmlText)
                        }
                    }
                }
            },
            confirmButton = {
                FilledTonalButton(
                    onClick = {
                        selectedLicenseDialog = null
                        VibrationUtil.performHapticFeedback(view)
                    },
                    shapes = ButtonDefaults.shapes()
                ) {
                    Text(stringResource(R.string.action_confirm))
                }
            },
            onDismissRequest = { selectedLicenseDialog = null }
        )
    }
}
