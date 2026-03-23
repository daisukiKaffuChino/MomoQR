package github.daisukikaffuchino.momoqr.logic.datastore

import android.content.Context
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.zxing.BarcodeFormat
import github.daisukikaffuchino.momoqr.MomoApplication
import github.daisukikaffuchino.momoqr.constants.AppConstants
import github.daisukikaffuchino.momoqr.logic.model.PalettePreset
import github.daisukikaffuchino.momoqr.logic.model.QrRenderQuality
import github.daisukikaffuchino.momoqr.logic.model.SearchEngine
import github.daisukikaffuchino.momoqr.logic.model.ThemeAccentColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

object DataStoreManager {
    const val MAX_CATEGORY_COUNT = 20

    private val Context.dataStore by preferencesDataStore(
        name = AppConstants.SP_NAME,
        produceMigrations = { context ->
            listOf(
                SharedPreferencesMigration(
                    context = context,
                    sharedPreferencesName = AppConstants.SP_NAME,
                )
            )
        }
    )

    val dataStore = MomoApplication.context.dataStore

    // Keys
    // 外观与个性化
    private val DYNAMIC_COLOR = booleanPreferencesKey(AppConstants.PREF_DYNAMIC_COLOR)
    private val ACCENT_COLOR = intPreferencesKey(AppConstants.PREF_ACCENT_COLOR)
    private val PALETTE_STYLE = intPreferencesKey(AppConstants.PREF_PALETTE_STYLE)
    private val DARK_MODE = intPreferencesKey(AppConstants.PREF_DARK_MODE)
    private val CONTRAST_LEVEL = floatPreferencesKey(AppConstants.PREF_CONTRAST_LEVEL)
    private val LANGUAGE = stringPreferencesKey(AppConstants.PREF_LANGUAGE)
    private val OPEN_IN_APP_BROWSER = booleanPreferencesKey(AppConstants.PREF_OPEN_IN_APP_BROWSER)
    private val HAPTIC_FEEDBACK = booleanPreferencesKey(AppConstants.PREF_HAPTIC_FEEDBACK)
    private val HOME_CLASSIC_CARD = booleanPreferencesKey(AppConstants.PREF_HOME_CLASSIC_CARD)

    private val SORTING_METHOD = intPreferencesKey(AppConstants.PREF_SORTING_METHOD)
    private val CATEGORIES = stringPreferencesKey(AppConstants.PREF_CATEGORIES)
    private val BARCODE_FORMATS = stringSetPreferencesKey(AppConstants.PREF_BARCODE_FORMATS)
    private val SWITCH_CAMERA = booleanPreferencesKey(AppConstants.PREF_SWITCH_CAMERA)
    private val BEEP_SOUND = booleanPreferencesKey(AppConstants.PREF_BEEP_SOUND)
    private val ENHANCED_PREPROCESSING =
        booleanPreferencesKey(AppConstants.PREF_ENHANCED_PREPROCESSING)
    private val AUTO_COPY = booleanPreferencesKey(AppConstants.PREF_AUTO_COPY)
    private val CORRECTION_LEVEL = floatPreferencesKey(AppConstants.PREF_CORRECTION_LEVEL)
    private val SHOW_LAB = booleanPreferencesKey(AppConstants.PREF_SHOW_LAB)
    private val SEARCH_ENGINE = stringPreferencesKey(AppConstants.PREF_SEARCH_ENGINE)
    private val SAVE_DIRECTLY = booleanPreferencesKey(AppConstants.PREF_NOT_ASK_SAVE_PATH)
    private val QR_RENDER_QUALITY = stringPreferencesKey(AppConstants.PREF_IMAGE_QUALITY)
    private val PALETTE_PRESETS = stringPreferencesKey(AppConstants.PREF_PALETTE_PRESETS)
    private val HIDDEN_OPTION_CONTRAST_LEVEL =
        booleanPreferencesKey(AppConstants.PREF_HIDDEN_OPTION_CONTRAST_LEVEL)
    private val EXIT_CONFIRMATION = booleanPreferencesKey(AppConstants.PREF_EXIT_CONFIRMATION)
    private val RESULT_PAGE_TIP_DISMISSED =
        booleanPreferencesKey(AppConstants.PREF_RESULT_PAGE_TIP_DISMISSED)

    // Getters
    val dynamicColorFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[DYNAMIC_COLOR] ?: AppConstants.PREF_DYNAMIC_COLOR_DEFAULT
    }

    val accentColorFlow: Flow<ThemeAccentColor> = dataStore.data.map { preferences ->
        ThemeAccentColor.fromId(
            preferences[ACCENT_COLOR] ?: AppConstants.PREF_ACCENT_COLOR_DEFAULT
        )
    }

    val paletteStyleFlow = dataStore.data.map { preferences ->
        preferences[PALETTE_STYLE] ?: AppConstants.PREF_PALETTE_STYLE_DEFAULT
    }

    val darkModeFlow = dataStore.data.map { preferences ->
        preferences[DARK_MODE] ?: AppConstants.PREF_DARK_MODE_DEFAULT
    }

    val contrastLevelFlow = dataStore.data.map { preferences ->
        preferences[CONTRAST_LEVEL] ?: AppConstants.PREF_CONTRAST_LEVEL_DEFAULT
    }

    val languageFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[LANGUAGE]
    }

    val openInAppBrowserFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[OPEN_IN_APP_BROWSER] ?: AppConstants.PREF_OPEN_IN_APP_BROWSER_DEFAULT
    }

    val hapticFeedbackFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[HAPTIC_FEEDBACK] ?: AppConstants.PREF_HAPTIC_FEEDBACK_DEFAULT
    }

    val homeClassicCardFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[HOME_CLASSIC_CARD] ?: AppConstants.PREF_HOME_CLASSIC_CARD_DEFAULT
    }

    val sortingMethodFlow: Flow<Int> = dataStore.data.map { preferences ->
        preferences[SORTING_METHOD] ?: AppConstants.PREF_SORTING_METHOD_DEFAULT
    }

    val categoriesFlow: Flow<List<String>> = dataStore.data.map { preferences ->
        Json.decodeFromString(preferences[CATEGORIES] ?: AppConstants.PREF_CATEGORIES_DEFAULT)
    }

    val barcodeFormatsFlow: Flow<Set<BarcodeFormat>> = dataStore.data.map { prefs ->
        prefs[BARCODE_FORMATS]
            ?.mapNotNull {
                runCatching { BarcodeFormat.valueOf(it) }.getOrNull()
            }
            ?.toSet()
            ?: setOf(BarcodeFormat.QR_CODE) //默认值
    }

    val switchCameraFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[SWITCH_CAMERA] ?: AppConstants.PREF_SWITCH_CAMERA_DEFAULT
    }

    val beepSoundFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[BEEP_SOUND] ?: AppConstants.PREF_BEEP_SOUND_DEFAULT
    }

    val enhancedPreprocessFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[ENHANCED_PREPROCESSING] ?: AppConstants.PREF_ENHANCED_PREPROCESSING_DEFAULT
    }

    val autoCopyFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[AUTO_COPY] ?: AppConstants.PREF_AUTO_COPY_DEFAULT
    }

    val correctionLevelFlow = dataStore.data.map { preferences ->
        preferences[CORRECTION_LEVEL] ?: AppConstants.PREF_CORRECTION_LEVEL_DEFAULT
    }

    val showLabFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[SHOW_LAB] ?: false
    }

    val searchEngineFlow = dataStore.data.map { preferences ->
        SearchEngine.fromValue(
            preferences[SEARCH_ENGINE] ?: SearchEngine.GOOGLE.value
        )
    }

    val saveDirectlyFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[SAVE_DIRECTLY] ?: AppConstants.PREF_NOT_ASK_SAVE_PATH_DEFAULT
    }

    val qrRenderQualityFlow: Flow<QrRenderQuality> = dataStore.data.map { preferences ->
        QrRenderQuality.fromValue(
            preferences[QR_RENDER_QUALITY] ?: QrRenderQuality.BALANCED.value
        )
    }

    val palettePresetsFlow: Flow<List<PalettePreset>> = dataStore.data.map { preferences ->
        runCatching {
            Json.decodeFromString<List<PalettePreset>>(
                preferences[PALETTE_PRESETS] ?: AppConstants.PREF_PALETTE_PRESETS_DEFAULT
            )
        }.getOrDefault(emptyList())
    }

    val hiddenOptionContrastLevelFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[HIDDEN_OPTION_CONTRAST_LEVEL] ?: false
    }

    val exitConfirmationFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[EXIT_CONFIRMATION] ?: AppConstants.PREF_EXIT_CONFIRMATION_DEFAULT
    }

    val resultPageTipDismissedFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[RESULT_PAGE_TIP_DISMISSED]
            ?: AppConstants.PREF_RESULT_PAGE_TIP_DISMISSED_DEFAULT
    }


    // Setters
    suspend fun setDynamicColor(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[DYNAMIC_COLOR] = value
        }
    }

    suspend fun setAccentColor(color: ThemeAccentColor) {
        dataStore.edit { preferences ->
            preferences[ACCENT_COLOR] = color.id
        }
    }

    suspend fun setPaletteStyle(value: Int) {
        dataStore.edit { preferences ->
            preferences[PALETTE_STYLE] = value
        }
    }

    suspend fun setDarkMode(value: Int) {
        dataStore.edit { preferences ->
            preferences[DARK_MODE] = value
        }
    }

    suspend fun setContrastLevel(value: Float) {
        dataStore.edit { preferences ->
            preferences[CONTRAST_LEVEL] = value
        }
    }

    suspend fun setLanguage(code: String?) {
        dataStore.edit {
            if (code == null)
                it.remove(LANGUAGE)
            else
                it[LANGUAGE] = code
        }
    }

    suspend fun setOpenInAppBrowser(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[OPEN_IN_APP_BROWSER] = value
        }
    }

    suspend fun setHapticFeedback(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[HAPTIC_FEEDBACK] = value
        }
    }

    suspend fun setHomeClassicCard(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[HOME_CLASSIC_CARD] = value
        }
    }

    suspend fun setSortingMethod(value: Int) {
        dataStore.edit { preferences ->
            preferences[SORTING_METHOD] = value
        }
    }

    suspend fun setCategories(value: List<String>) {
        if (value.distinct().size > MAX_CATEGORY_COUNT) return
        dataStore.edit { preferences ->
            preferences[CATEGORIES] = Json.encodeToString(value)
        }
    }

    suspend fun setCodeFormats(formats: Set<BarcodeFormat>) {
        dataStore.edit { preferences ->
            preferences[BARCODE_FORMATS] = formats.map { f -> f.name }.toSet()
        }
    }

    suspend fun setSwitchCamera(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[SWITCH_CAMERA] = value
        }
    }

    suspend fun setBeepSound(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[BEEP_SOUND] = value
        }
    }

    suspend fun setEnhancedPreprocess(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[ENHANCED_PREPROCESSING] = value
        }
    }

    suspend fun setAutoCopy(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[AUTO_COPY] = value
        }
    }

    suspend fun setCorrectionLevel(value: Float) {
        dataStore.edit { preferences ->
            preferences[CORRECTION_LEVEL] = value
        }
    }

    suspend fun setShowLab(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[SHOW_LAB] = value
        }
    }

    suspend fun setSearchEngine(engine: SearchEngine) {
        dataStore.edit { preferences ->
            preferences[SEARCH_ENGINE] = engine.value
        }
    }

    suspend fun setSaveDirectly(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[SAVE_DIRECTLY] = value
        }
    }

    suspend fun setQrRenderQuality(quality: QrRenderQuality) {
        dataStore.edit { preferences ->
            preferences[QR_RENDER_QUALITY] = quality.value
        }
    }

    suspend fun setPalettePresets(value: List<PalettePreset>) {
        dataStore.edit { preferences ->
            preferences[PALETTE_PRESETS] = Json.encodeToString(value)
        }
    }

    suspend fun setHiddenOptionContrastLevel(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[HIDDEN_OPTION_CONTRAST_LEVEL] = value
        }
    }

    suspend fun setExitConfirmation(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[EXIT_CONFIRMATION] = value
        }
    }

    suspend fun setResultPageTipDismissed(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[RESULT_PAGE_TIP_DISMISSED] = value
        }
    }

}
