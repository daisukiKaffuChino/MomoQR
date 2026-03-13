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
import github.daisukikaffuchino.momoqr.logic.model.SearchEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

object DataStoreManager {
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
    private val PALETTE_STYLE = intPreferencesKey(AppConstants.PREF_PALETTE_STYLE)
    private val DARK_MODE = intPreferencesKey(AppConstants.PREF_DARK_MODE)
    private val CONTRAST_LEVEL = floatPreferencesKey(AppConstants.PREF_CONTRAST_LEVEL)
    private val LANGUAGE = stringPreferencesKey(AppConstants.PREF_LANGUAGE)
    private val SECURE_MODE = booleanPreferencesKey(AppConstants.PREF_SECURE_MODE)
    private val HAPTIC_FEEDBACK = booleanPreferencesKey(AppConstants.PREF_HAPTIC_FEEDBACK)
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

    // Getters
    val dynamicColorFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[DYNAMIC_COLOR] ?: AppConstants.PREF_DYNAMIC_COLOR_DEFAULT
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

    val secureModeFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[SECURE_MODE] ?: AppConstants.PREF_SECURE_MODE_DEFAULT
    }

    val hapticFeedbackFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[HAPTIC_FEEDBACK] ?: AppConstants.PREF_HAPTIC_FEEDBACK_DEFAULT
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




    // Setters
    suspend fun setDynamicColor(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[DYNAMIC_COLOR] = value
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

    suspend fun setSecureMode(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[SECURE_MODE] = value
        }
    }

    suspend fun setHapticFeedback(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[HAPTIC_FEEDBACK] = value
        }
    }

    suspend fun setSortingMethod(value: Int) {
        dataStore.edit { preferences ->
            preferences[SORTING_METHOD] = value
        }
    }

    suspend fun setCategories(value: List<String>) {
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


}