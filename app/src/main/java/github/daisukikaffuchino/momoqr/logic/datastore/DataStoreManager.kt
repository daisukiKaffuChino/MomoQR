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
import github.daisukikaffuchino.momoqr.constants.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlin.math.E

object DataStoreManager {
    private val Context.dataStore by preferencesDataStore(
        name = Constants.SP_NAME,
        produceMigrations = { context ->
            listOf(
                SharedPreferencesMigration(
                    context = context,
                    sharedPreferencesName = Constants.SP_NAME,
                )
            )
        }
    )

    val dataStore = MomoApplication.context.dataStore

    // Keys
    // 外观与个性化
    private val DYNAMIC_COLOR = booleanPreferencesKey(Constants.PREF_DYNAMIC_COLOR)
    private val PALETTE_STYLE = intPreferencesKey(Constants.PREF_PALETTE_STYLE)
    private val DARK_MODE = intPreferencesKey(Constants.PREF_DARK_MODE)
    private val CONTRAST_LEVEL = floatPreferencesKey(Constants.PREF_CONTRAST_LEVEL)
    private val LANGUAGE = stringPreferencesKey(Constants.PREF_LANGUAGE)
    private val SECURE_MODE = booleanPreferencesKey(Constants.PREF_SECURE_MODE)
    private val HAPTIC_FEEDBACK = booleanPreferencesKey(Constants.PREF_HAPTIC_FEEDBACK)
    private val SORTING_METHOD = intPreferencesKey(Constants.PREF_SORTING_METHOD)
    private val CATEGORIES = stringPreferencesKey(Constants.PREF_CATEGORIES)
    private val BARCODE_FORMATS = stringSetPreferencesKey(Constants.PREF_BARCODE_FORMATS)
    private val SWITCH_CAMERA = booleanPreferencesKey(Constants.PREF_SWITCH_CAMERA)
    private val BEEP_SOUND = booleanPreferencesKey(Constants.PREF_BEEP_SOUND)
    private val ENHANCED_PREPROCESSING = booleanPreferencesKey(Constants.PREF_ENHANCED_PREPROCESSING)

    // Getters
    val dynamicColorFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[DYNAMIC_COLOR] ?: Constants.PREF_DYNAMIC_COLOR_DEFAULT
    }

    val paletteStyleFlow = dataStore.data.map { preferences ->
        preferences[PALETTE_STYLE] ?: Constants.PREF_PALETTE_STYLE_DEFAULT
    }

    val darkModeFlow = dataStore.data.map { preferences ->
        preferences[DARK_MODE] ?: Constants.PREF_DARK_MODE_DEFAULT
    }

    val contrastLevelFlow = dataStore.data.map { preferences ->
        preferences[CONTRAST_LEVEL] ?: Constants.PREF_CONTRAST_LEVEL_DEFAULT
    }

    val languageFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[LANGUAGE]
    }

    val secureModeFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[SECURE_MODE] ?: Constants.PREF_SECURE_MODE_DEFAULT
    }

    val hapticFeedbackFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[HAPTIC_FEEDBACK] ?: Constants.PREF_HAPTIC_FEEDBACK_DEFAULT
    }

    val sortingMethodFlow: Flow<Int> = dataStore.data.map { preferences ->
        preferences[SORTING_METHOD] ?: Constants.PREF_SORTING_METHOD_DEFAULT
    }

    val categoriesFlow: Flow<List<String>> = dataStore.data.map { preferences ->
        Json.decodeFromString(preferences[CATEGORIES] ?: Constants.PREF_CATEGORIES_DEFAULT)
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
        preferences[SWITCH_CAMERA] ?: Constants.PREF_SWITCH_CAMERA_DEFAULT
    }

    val beepSoundFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[BEEP_SOUND] ?: Constants.PREF_BEEP_SOUND_DEFAULT
    }

    val enhancedPreprocessFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[ENHANCED_PREPROCESSING] ?: Constants.PREF_ENHANCED_PREPROCESSING_DEFAULT
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


}