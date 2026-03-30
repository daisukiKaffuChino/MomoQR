package github.daisukikaffuchino.momoqr.ui.activities

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.ui.pages.crash.CrashPage

class CrashActivity : BaseActivity() {

    companion object {
        const val BRAND_PREFIX = "Brand:      "
        const val MODEL_PREFIX = "Model:      "
        const val DEVICE_SDK_PREFIX = "Device SDK: "
        const val CRASH_TIME_PREFIX = "Crash time: "
        const val BEGINNING_CRASH = "======beginning of crash======"
    }

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    override fun ActivityContent() {
        val crashLogs = intent.getStringExtra("logs")

        CrashPage(
            crashLog = crashLogs ?: stringResource(R.string.tip_no_crash_logs),
            exitApp = ::finishAffinity,
            modifier = Modifier.fillMaxSize()
        )
    }
}