package github.daisukikaffuchino.momoqr.ui.pages.crash

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.ui.activities.CrashActivity.Companion.BEGINNING_CRASH
import github.daisukikaffuchino.momoqr.ui.activities.CrashActivity.Companion.BRAND_PREFIX
import github.daisukikaffuchino.momoqr.ui.activities.CrashActivity.Companion.CRASH_TIME_PREFIX
import github.daisukikaffuchino.momoqr.ui.activities.CrashActivity.Companion.DEVICE_SDK_PREFIX
import github.daisukikaffuchino.momoqr.ui.activities.CrashActivity.Companion.MODEL_PREFIX
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.TertiarySettingsItem
import github.daisukikaffuchino.momoqr.ui.theme.Defaults
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CrashPage(
    crashLog: String,
    exitApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val scrollState = rememberScrollState()
    val isExpanded by remember {
        derivedStateOf {
            scrollState.value == 0
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.page_crash),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            SmallExtendedFloatingActionButton(
                text = { Text(stringResource(R.string.action_exit_app)) },
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_exit_to_app),
                        contentDescription = null
                    )
                },
                expanded = isExpanded,
                onClick = exitApp
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)

    ) { innerPadding ->
        val context = LocalContext.current
        val packageName = context.packageName

        val deviceBrand = Build.BRAND
        val deviceModel = Build.MODEL
        val sdkLevel = Build.VERSION.SDK_INT
        val currentDateTime = Calendar.getInstance().time
        @SuppressLint("NonObservableLocale") val formatter =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedDateTime = formatter.format(currentDateTime)

        val deviceInfo = StringBuilder().apply {
            append(BRAND_PREFIX).append(deviceBrand).append("\n")
            append(MODEL_PREFIX).append(deviceModel).append("\n")
            append(DEVICE_SDK_PREFIX).append(sdkLevel).append("\n").append("\n")
            append(CRASH_TIME_PREFIX).append(formattedDateTime).append("\n").append("\n")
            append(BEGINNING_CRASH).append("\n")
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = Defaults.screenHorizontalPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            TertiarySettingsItem(
                leadingIconRes = R.drawable.ic_bug_report,
                title = stringResource(R.string.tip_tips),
                description = stringResource(R.string.tip_copy_logs_to_github),
                onClick = {}
            )
            Spacer(Modifier.height(16.dp))
            SelectionContainer {
                Text(
                    text = buildAnnotatedString {
                        append(deviceInfo)
                        val splitLines = crashLog.lines()
                        splitLines.forEach {
                            if (it.contains(packageName)) {
                                withStyle(
                                    SpanStyle(
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        background = MaterialTheme.colorScheme.primaryContainer,
                                        fontWeight = FontWeight.Bold,
                                    )
                                ) {
                                    append(it)
                                }
                            } else {
                                append(it)
                            }
                            append("\n")
                        }
                    },
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}