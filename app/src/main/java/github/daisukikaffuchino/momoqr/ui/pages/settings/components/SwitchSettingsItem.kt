package github.daisukikaffuchino.momoqr.ui.pages.settings.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import github.daisukikaffuchino.momoqr.ui.theme.Defaults

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SwitchSettingsItem(
    modifier: Modifier = Modifier,
    @DrawableRes leadingIconRes: Int,
    title: String,
    description: String? = null,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    SettingsItem(
        leadingIcon = painterResource(leadingIconRes),
        title = title,
        description = description,
        trailingContent = {
            Switch(
                checked = checked,
                enabled = enabled,
                onCheckedChange = null,
                modifier = Modifier.padding(start = Defaults.settingsItemHorizontalPadding / 2)
            )
        },
        onClick = {
            if (enabled) onCheckedChange(!checked)
        },
        modifier = modifier,
    )
}

