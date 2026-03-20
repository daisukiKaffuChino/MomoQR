package github.daisukikaffuchino.momoqr.ui.pages.palette.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.ui.pages.palette.PaletteUiState
import github.daisukikaffuchino.momoqr.ui.pages.palette.SectionCard

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PreviewSection(
    state: PaletteUiState,
    onContentChanged: (String) -> Unit,
) {
    SectionCard(title = stringResource(R.string.label_palette_preview)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    state.isGeneratingPreview -> {
                        LoadingIndicator()
                    }

                    state.previewBitmap != null -> {
                        AsyncImage(
                            model = state.previewBitmap,
                            contentDescription = stringResource(R.string.label_palette_preview),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    else -> {
                        Text(
                            text = state.previewErrorMessage ?: stringResource(R.string.tip_palette_preview_empty),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                }
            }

            TextField(
                value = state.previewContent,
                onValueChange = onContentChanged,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                label = { Text(stringResource(R.string.label_palette_preview_content)) },
                minLines = 3,
                maxLines = 10
            )
        }

        state.previewErrorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
