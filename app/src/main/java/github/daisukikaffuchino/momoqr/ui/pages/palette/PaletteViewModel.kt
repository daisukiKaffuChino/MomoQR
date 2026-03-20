package github.daisukikaffuchino.momoqr.ui.pages.palette

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.scale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import github.daisukikaffuchino.momoqr.MomoApplication
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.logic.datastore.DataStoreManager
import github.daisukikaffuchino.momoqr.logic.model.PaletteColorTarget
import github.daisukikaffuchino.momoqr.logic.model.PaletteDotShape
import github.daisukikaffuchino.momoqr.logic.model.PalettePreset
import github.daisukikaffuchino.momoqr.utils.QrAppearanceOptions
import github.daisukikaffuchino.momoqr.utils.QrGenerateUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class PaletteViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(PaletteUiState())
    val state = _state.asStateFlow()

    companion object {
        const val MAX_PRESET_COUNT = 10
    }

    private var renderSequence = 0L

    init {
        observePreviewInputs()
        observePresets()
    }

    fun updatePreviewContent(content: String) {
        _state.update {
            it.copy(
                previewContent = content,
                previewErrorMessage = null,
            )
        }
    }

    fun updateDotShape(shape: PaletteDotShape) {
        _state.update { it.copy(dotShape = shape) }
    }

    fun updateDotScale(scale: Float) {
        _state.update { it.copy(dotScale = scale) }
    }

    fun updateBackgroundAlpha(alpha: Float) {
        _state.update { it.copy(backgroundAlpha = alpha) }
    }

    fun updateBorderWidth(width: Int) {
        _state.update { it.copy(borderWidth = width) }
    }

    fun updatePickColorFromBackground(enabled: Boolean) {
        _state.update { it.copy(pickColorFromBackground = enabled) }
    }

    fun selectColorTarget(target: PaletteColorTarget) {
        _state.update { it.copy(selectedColorTarget = target) }
    }

    fun updateSelectedPaneIndex(index: Int) {
        _state.update { it.copy(selectedPaneIndex = index) }
    }

    fun resetEditorState() {
        val previousLogo = _state.value.logoBitmap
        val previousBackground = _state.value.backgroundBitmap
        val previousPreview = _state.value.previewBitmap

        _state.update { current ->
            PaletteUiState(
                presets = current.presets,
                editorGridState = current.editorGridState,
            )
        }

        recycleBitmap(previousLogo)
        recycleBitmap(previousBackground)
        recycleBitmap(previousPreview)

        viewModelScope.launch {
            renderPreview(_state.value.toPreviewRenderInput())
        }
    }

    fun updateSelectedColor(color: Color) {
        _state.update { current ->
            when (current.selectedColorTarget) {
                PaletteColorTarget.Dark -> current.copy(darkColorArgb = color.toArgb())
                PaletteColorTarget.Light -> current.copy(lightColorArgb = color.toArgb())
                PaletteColorTarget.Background -> current.copy(backgroundColorArgb = color.toArgb())
            }
        }
    }

    fun setLogoBitmap(bitmap: Bitmap?) {
        val previousBitmap = _state.value.logoBitmap
        if (previousBitmap != null && previousBitmap !== bitmap && !previousBitmap.isRecycled) {
            previousBitmap.recycle()
        }
        _state.update { it.copy(logoBitmap = bitmap) }
    }

    fun clearLogoBitmap() {
        setLogoBitmap(null)
    }

    fun setBackgroundBitmap(bitmap: Bitmap?) {
        val previousBitmap = _state.value.backgroundBitmap
        if (previousBitmap != null && previousBitmap !== bitmap && !previousBitmap.isRecycled) {
            previousBitmap.recycle()
        }
        _state.update { it.copy(backgroundBitmap = bitmap) }
    }

    fun clearBackgroundBitmap() {
        setBackgroundBitmap(null)
    }

    fun savePreset(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentState = state.value
            val existingPreset = currentState.presets.firstOrNull { it.name == name }
            if (existingPreset == null && currentState.presets.size >= MAX_PRESET_COUNT) {
                return@launch
            }
            val now = System.currentTimeMillis()
            val presetId = existingPreset?.id ?: UUID.randomUUID().toString()

            existingPreset?.let { deletePresetFiles(it) }

            val logoFileName = currentState.logoBitmap?.let {
                savePresetBitmap(it, presetId, "logo")
            }
            val backgroundFileName = currentState.backgroundBitmap?.let {
                savePresetBitmap(it, presetId, "background")
            }

            val preset = PalettePreset(
                id = presetId,
                name = name,
                previewContent = currentState.previewContent,
                darkColorArgb = currentState.darkColorArgb,
                lightColorArgb = currentState.lightColorArgb,
                backgroundColorArgb = currentState.backgroundColorArgb,
                pickColorFromBackground = currentState.pickColorFromBackground,
                selectedColorTarget = currentState.selectedColorTarget,
                dotShape = currentState.dotShape,
                dotScale = currentState.dotScale,
                backgroundAlpha = currentState.backgroundAlpha,
                borderWidth = currentState.borderWidth,
                logoFileName = logoFileName,
                backgroundFileName = backgroundFileName,
                createdAt = existingPreset?.createdAt ?: now,
                updatedAt = now,
            )

            val updatedPresets = (
                    currentState.presets.filterNot { it.id == presetId } + preset
                    ).sortedByDescending { it.updatedAt }

            DataStoreManager.setPalettePresets(updatedPresets)
        }
    }

    fun applyPreset(preset: PalettePreset) {
        viewModelScope.launch(Dispatchers.IO) {
            val logoBitmap = preset.logoFileName?.let(::loadPresetBitmap)
            val backgroundBitmap = preset.backgroundFileName?.let(::loadPresetBitmap)

            withContext(Dispatchers.Main) {
                val previousLogo = state.value.logoBitmap
                val previousBackground = state.value.backgroundBitmap

                _state.update {
                    it.copy(
                        previewContent = preset.previewContent,
                        darkColorArgb = preset.darkColorArgb,
                        lightColorArgb = preset.lightColorArgb,
                        backgroundColorArgb = preset.backgroundColorArgb,
                        pickColorFromBackground = preset.pickColorFromBackground,
                        selectedColorTarget = preset.selectedColorTarget,
                        dotShape = preset.dotShape,
                        dotScale = preset.dotScale,
                        backgroundAlpha = preset.backgroundAlpha,
                        borderWidth = preset.borderWidth,
                        logoBitmap = logoBitmap,
                        backgroundBitmap = backgroundBitmap,
                    )
                }

                recycleBitmap(previousLogo)
                recycleBitmap(previousBackground)
            }
        }
    }

    fun deletePreset(preset: PalettePreset) {
        viewModelScope.launch(Dispatchers.IO) {
            deletePresetFiles(preset)
            DataStoreManager.setPalettePresets(
                state.value.presets.filterNot { it.id == preset.id }
            )
        }
    }

    private fun observePresets() {
        viewModelScope.launch {
            DataStoreManager.palettePresetsFlow.collect { presets ->
                _state.update { it.copy(presets = presets) }
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observePreviewInputs() {
        viewModelScope.launch {
            state
                .map {
                    PreviewRenderInput(
                        previewContent = it.previewContent,
                        darkColorArgb = it.darkColorArgb,
                        lightColorArgb = it.lightColorArgb,
                        backgroundColorArgb = it.backgroundColorArgb,
                        pickColorFromBackground = it.pickColorFromBackground,
                        dotShape = it.dotShape,
                        dotScale = it.dotScale,
                        backgroundAlpha = it.backgroundAlpha,
                        borderWidth = it.borderWidth,
                        logoBitmap = it.logoBitmap,
                        backgroundBitmap = it.backgroundBitmap,
                    )
                }
                .distinctUntilChanged()
                .debounce(150)
                .collect { renderPreview(it) }
        }
    }

    private suspend fun renderPreview(input: PreviewRenderInput) {
        if (input.previewContent.isBlank()) {
            val previousPreview = _state.value.previewBitmap
            _state.update {
                it.copy(
                    previewBitmap = null,
                    isGeneratingPreview = false,
                    previewErrorMessage = MomoApplication.context.getString(
                        R.string.error_palette_preview_content_required
                    ),
                )
            }
            recycleBitmap(previousPreview)
            return
        }

        val requestId = ++renderSequence
        _state.update {
            it.copy(
                isGeneratingPreview = true,
                previewErrorMessage = null,
            )
        }

        withContext(Dispatchers.Default) {
            QrGenerateUtil.generateQrBitmap(
                content = input.previewContent,
                eclFloat = 15f,
                qrSize = 960,
                appearance = QrAppearanceOptions(
                    darkArgb = input.darkColorArgb,
                    lightArgb = input.lightColorArgb,
                    backgroundArgb = input.backgroundColorArgb,
                    autoColor = input.pickColorFromBackground,
                    roundedPatterns = input.dotShape == PaletteDotShape.Circle,
                    patternScale = input.dotScale,
                    logoBitmap = input.logoBitmap,
                    backgroundBitmap = input.backgroundBitmap,
                    backgroundAlpha = input.backgroundAlpha,
                    borderWidth = input.borderWidth,
                ),
                onSuccess = { bitmap ->
                    if (requestId != renderSequence) {
                        recycleBitmap(bitmap)
                        return@generateQrBitmap
                    }
                    val previousPreview = _state.value.previewBitmap
                    _state.update {
                        it.copy(
                            previewBitmap = bitmap,
                            isGeneratingPreview = false,
                            previewErrorMessage = null,
                        )
                    }
                    recycleBitmap(previousPreview)
                },
                onError = { throwable ->
                    if (requestId != renderSequence) {
                        return@generateQrBitmap
                    }
                    val previousPreview = _state.value.previewBitmap
                    _state.update {
                        it.copy(
                            previewBitmap = null,
                            isGeneratingPreview = false,
                            previewErrorMessage = throwable.message
                                ?: MomoApplication.context.getString(
                                    R.string.error_no_content_entered_or_too_long
                                ),
                        )
                    }
                    recycleBitmap(previousPreview)
                },
            )
        }
    }

    private fun presetDirectory(): File {
        return File(MomoApplication.context.filesDir, "palette_presets").apply {
            if (!exists()) mkdirs()
        }
    }

    private fun savePresetBitmap(
        bitmap: Bitmap,
        presetId: String,
        suffix: String,
    ): String? {
        val fileName = "${presetId}_$suffix.png"
        val file = File(presetDirectory(), fileName)
        val sampledBitmap = downsampleBitmap(bitmap)
        return runCatching {
            FileOutputStream(file).use { output ->
                sampledBitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
            }
            fileName
        }.getOrNull().also {
            if (sampledBitmap !== bitmap) {
                recycleBitmap(sampledBitmap)
            }
        }
    }

    private fun loadPresetBitmap(fileName: String): Bitmap? {
        val file = File(presetDirectory(), fileName)
        if (!file.exists()) return null
        return BitmapFactory.decodeFile(file.absolutePath)
    }

    private fun deletePresetFiles(preset: PalettePreset) {
        listOfNotNull(preset.logoFileName, preset.backgroundFileName).forEach { fileName ->
            runCatching {
                File(presetDirectory(), fileName).delete()
            }
        }
    }

    private fun downsampleBitmap(
        bitmap: Bitmap,
        maxSide: Int = 768,
    ): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val longestSide = maxOf(width, height)
        if (longestSide <= maxSide) return bitmap

        val scale = maxSide.toFloat() / longestSide.toFloat()
        val targetWidth = (width * scale).roundToInt().coerceAtLeast(1)
        val targetHeight = (height * scale).roundToInt().coerceAtLeast(1)
        return bitmap.scale(targetWidth, targetHeight)
    }

    private fun recycleBitmap(bitmap: Bitmap?) {
        if (bitmap != null && !bitmap.isRecycled) {
            bitmap.recycle()
        }
    }

    override fun onCleared() {
        recycleBitmap(_state.value.logoBitmap)
        recycleBitmap(_state.value.backgroundBitmap)
        recycleBitmap(_state.value.previewBitmap)
        super.onCleared()
    }
}

private fun PaletteUiState.toPreviewRenderInput(): PreviewRenderInput {
    return PreviewRenderInput(
        previewContent = previewContent,
        darkColorArgb = darkColorArgb,
        lightColorArgb = lightColorArgb,
        backgroundColorArgb = backgroundColorArgb,
        pickColorFromBackground = pickColorFromBackground,
        dotShape = dotShape,
        dotScale = dotScale,
        backgroundAlpha = backgroundAlpha,
        borderWidth = borderWidth,
        logoBitmap = logoBitmap,
        backgroundBitmap = backgroundBitmap,
    )
}

private data class PreviewRenderInput(
    val previewContent: String,
    val darkColorArgb: Int,
    val lightColorArgb: Int,
    val backgroundColorArgb: Int,
    val pickColorFromBackground: Boolean,
    val dotShape: PaletteDotShape,
    val dotScale: Float,
    val backgroundAlpha: Float,
    val borderWidth: Int,
    val logoBitmap: Bitmap?,
    val backgroundBitmap: Bitmap?,
)

fun launchImagePicker(
    target: PaletteImageTarget,
    onTargetSet: (PaletteImageTarget) -> Unit,
    launcher: (PickVisualMediaRequest) -> Unit,
) {
    onTargetSet(target)
    launcher(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
}

fun presetSummary(preset: PalettePreset): String {
    val context = MomoApplication.context
    val logoState = context.getString(
        if (preset.logoFileName != null) R.string.tip_palette_has_logo else R.string.tip_palette_no_logo
    )
    val backgroundState = context.getString(
        if (preset.backgroundFileName != null) {
            R.string.tip_palette_has_background
        } else {
            R.string.tip_palette_no_background
        }
    )
    return context.getString(
        R.string.tip_palette_preset_summary,
        context.getString(preset.dotShape.stringRes),
        preset.dotScale,
        logoState,
        backgroundState,
    )
}

fun snapToStep(value: Float, step: Float, min: Float = 0.1f): Float {
    val steps = ((value - min) / step).roundToInt()
    return (min + steps * step).coerceAtLeast(min)
}

fun decodeSampledBitmapFromUri(
    context: Context,
    uri: Uri
): Bitmap? {
    val maxSide = 1200
    val boundsOptions = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }

    resolveUri(context, uri, boundsOptions)

    val srcWidth = boundsOptions.outWidth
    val srcHeight = boundsOptions.outHeight
    if (srcWidth <= 0 || srcHeight <= 0) return null

    var sampleSize = 1
    var currentWidth = srcWidth
    var currentHeight = srcHeight
    while (currentWidth > maxSide || currentHeight > maxSide) {
        currentWidth /= 2
        currentHeight /= 2
        sampleSize *= 2
    }

    val decodeOptions = BitmapFactory.Options().apply {
        inSampleSize = sampleSize.coerceAtLeast(1)
        inPreferredConfig = Bitmap.Config.ARGB_8888
    }

    return resolveBitmap(context, uri, decodeOptions)
}

private fun resolveUri(
    context: Context,
    uri: Uri,
    options: BitmapFactory.Options,
) {
    when (uri.scheme) {
        ContentResolver.SCHEME_CONTENT,
        ContentResolver.SCHEME_FILE -> {
            runCatching {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream, null, options)
                }
            }
        }
    }
}

private fun resolveBitmap(
    context: Context,
    uri: Uri,
    options: BitmapFactory.Options,
): Bitmap? {
    return when (uri.scheme) {
        ContentResolver.SCHEME_CONTENT,
        ContentResolver.SCHEME_FILE -> {
            runCatching {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream, null, options)
                }
            }.getOrNull()
        }

        else -> null
    }
}
