package github.daisukikaffuchino.momoqr.ui.pages.settings

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.ui.components.TopAppBarScaffold

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsLab(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBarScaffold(
        title = stringResource(R.string.pref_lab),
        onBack = onNavigateUp,
        modifier = modifier,
    ) {
        Text(
            text = "No Item",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}