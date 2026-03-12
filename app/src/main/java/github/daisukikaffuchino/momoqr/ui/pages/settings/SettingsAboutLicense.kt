package github.daisukikaffuchino.momoqr.ui.pages.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.util.author
import com.mikepenz.aboutlibraries.ui.compose.util.htmlReadyLicenseContent
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.ui.components.BasicDialog
import github.daisukikaffuchino.momoqr.ui.components.TopAppBarScaffold
import github.daisukikaffuchino.momoqr.ui.components.ListItemContainer
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.SettingsItem
import github.daisukikaffuchino.momoqr.ui.theme.Defaults
import github.daisukikaffuchino.momoqr.utils.VibrationUtil

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsAboutLicence(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val libraries by produceLibraries(R.raw.aboutlibraries)
    val view = LocalView.current
    val uriHandler = LocalUriHandler.current

    TopAppBarScaffold(
        title = stringResource(R.string.pref_licence),
        onBack = onNavigateUp,
        modifier = modifier
    ) {
        ListItemContainer(Modifier.fillMaxSize()) {
            items(
                items = libraries?.libraries ?: listOf(),
                key = { it.artifactId }
            ) { library ->
                var openDialog by remember { mutableStateOf(false) }
                SettingsItem(
                    shapes = Defaults.largerShapes(),
                    headlineContent = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = library.name,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        val author = library.author
                        if (author.isNotBlank()) {
                            Text(
                                text = author,
                                style = MaterialTheme.typography.bodyMediumEmphasized
                            )
                        }
                    },
                    supportingContent = {
                        if (library.licenses.isNotEmpty()) {
                            FlowRow {
                                library.licenses.forEach {

                                        Text(
                                            maxLines = 1,
                                            text = it.name,
                                            overflow = TextOverflow.Ellipsis,
                                            style = MaterialTheme.typography.bodySmallEmphasized,
                                            color = MaterialTheme.colorScheme.primary,
                                        )

                                }
                            }
                        }
                    },
                    onClick = {
                        val license = library.licenses.firstOrNull()
                        if (!license?.htmlReadyLicenseContent.isNullOrBlank()) {
                            openDialog = true
                        } else if (!license?.url.isNullOrBlank()) {
                            license.url?.also {
                                try {
                                    uriHandler.openUri(it)
                                } catch (t: Throwable) {
                                    throw Exception("Failed to open licence URL: $it", t)
                                }
                            }
                        }
                    }
                )

                BasicDialog(
                    visible = openDialog,
                    title = { Text(library.name) },
                    text = {
                        library.licenses.firstOrNull()?.licenseContent?.let {
                            Column(Modifier.verticalScroll(rememberScrollState())) {
                                SelectionContainer { Text(text = it) }
                            }
                        }
                    },
                    confirmButton = {
                        FilledTonalButton(
                            onClick = {
                                openDialog = false
                                VibrationUtil.performHapticFeedback(view)
                            },
                            shapes = ButtonDefaults.shapes()
                        ) { Text(stringResource(R.string.action_confirm)) }
                    },
                    onDismissRequest = { openDialog = false }
                )
            }
        }
    }
}