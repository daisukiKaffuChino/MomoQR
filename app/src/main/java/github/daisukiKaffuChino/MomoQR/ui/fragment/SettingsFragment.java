package github.daisukiKaffuChino.MomoQR.ui.fragment;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

import github.daisukiKaffuChino.MomoQR.R;

import java.text.DecimalFormat;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference_settings_root, rootKey);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            Preference dynamicColor = findPreference("dynamicColor");
            assert dynamicColor != null;
            dynamicColor.setSummary(R.string.require_android_s);
            dynamicColor.setEnabled(false);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Preference notAskForSavePath = findPreference("notAskForSavePath");
            assert notAskForSavePath != null;
            notAskForSavePath.setSummary(R.string.require_android_q);
            notAskForSavePath.setEnabled(false);
        }

        Preference clearCache = findPreference("clearCache");
        assert clearCache != null;
        clearCache.setSummary(getReadableFileSize(getFolderSize(requireContext().getCacheDir())));
        clearCache.setOnPreferenceClickListener(preference -> {
            File cacheDir=requireContext().getCacheDir();
            if (clearFolder(cacheDir))
                preference.setSummary(getReadableFileSize(getFolderSize(cacheDir)));
            return false;
        });
    }

    @Override
    public void onDisplayPreferenceDialog(@NonNull Preference preference) {
        if (preference instanceof ListPreference) {
            showListPreference((ListPreference) preference);
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    private void showListPreference(@NonNull ListPreference preference) {
        int selectionIndex = Arrays.asList(preference.getEntryValues()).indexOf(preference.getValue());
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle(preference.getTitle());
        builder.setNegativeButton(R.string.cancel, null);
        builder.setSingleChoiceItems(preference.getEntries(), selectionIndex, (dialog, index) -> {
            String newValue = preference.getEntryValues()[index].toString();
            if (preference.callChangeListener(newValue)) {
                preference.setValue(newValue);
            }
            dialog.dismiss();
        });
        builder.show();
    }

    private final SharedPreferences.OnSharedPreferenceChangeListener mListener = (sharedPreferences, key) -> {
        assert key != null;
        if (key.equals("dayNightTheme")) {
            switch (sharedPreferences.getString(key, "0")) {
                case "0":
                    ((AppCompatActivity) requireActivity()).getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    break;
                case "1":
                    ((AppCompatActivity) requireActivity()).getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    break;
                case "2":
                    ((AppCompatActivity) requireActivity()).getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        } else if (key.equals("enableMaterial2") | key.equals("dynamicColor")) {
            requireActivity().recreate();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(getPreferenceManager().getSharedPreferences()).registerOnSharedPreferenceChangeListener(mListener);
    }

    @Override
    public void onPause() {
        Objects.requireNonNull(getPreferenceManager().getSharedPreferences()).unregisterOnSharedPreferenceChangeListener(mListener);
        super.onPause();
    }

    private long getFolderSize(File folder) {
        long size = 0;
        if (folder != null && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        size += file.length();
                    } else if (file.isDirectory()) {
                        size += getFolderSize(file);
                    }
                }
            }
        }
        return size;
    }

    private String getReadableFileSize(long size) {
        if (size <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private boolean clearFolder(File folder) {
        boolean success = true;
        if (folder != null && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        success &= file.delete();
                    } else if (file.isDirectory()) {
                        success &= clearFolder(file);
                        success &= file.delete();
                    }
                }
            }
        }
        return success;
    }
}
