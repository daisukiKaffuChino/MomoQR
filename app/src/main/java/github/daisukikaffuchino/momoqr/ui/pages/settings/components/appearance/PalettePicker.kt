package github.daisukikaffuchino.momoqr.ui.pages.settings.components.appearance

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.logic.model.ContrastLevel
import github.daisukikaffuchino.momoqr.logic.model.DarkMode
import github.daisukikaffuchino.momoqr.logic.model.PaletteStyle
import github.daisukikaffuchino.momoqr.ui.pages.settings.components.LazyRowSettingsItem
import github.daisukikaffuchino.momoqr.ui.theme.Defaults

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PalettePicker(
    modifier: Modifier = Modifier,
    currentPalette: () -> PaletteStyle,
    onPaletteChange: (paletteStyle: PaletteStyle) -> Unit,
    isDynamicColor: Boolean,
    isDarkMode: DarkMode,
    contrastLevel: ContrastLevel,
) {
    val paletteOptions = remember { PaletteStyle.entries.toList() }

    LazyRowSettingsItem(
        title = stringResource(R.string.pref_palette_style),
        description = stringResource(R.string.pref_palette_style_desc),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        fadedEdgeWidth = Defaults.fadedEdgeWidth,
        modifier = modifier
    ) {
        items(items = paletteOptions, key = { it.id }) {
            val isSelected by remember { derivedStateOf { currentPalette() == it } }

            PaletteItem(
                isDynamicColor = isDynamicColor,
                isDark = when (isDarkMode) {
                    DarkMode.FollowSystem -> isSystemInDarkTheme()
                    DarkMode.Light -> false
                    DarkMode.Dark -> true
                },
                paletteStyle = it,
                selected = isSelected,
                contrastLevel = contrastLevel,
                onSelect = { onPaletteChange(it) }
            )
        }
    }
}