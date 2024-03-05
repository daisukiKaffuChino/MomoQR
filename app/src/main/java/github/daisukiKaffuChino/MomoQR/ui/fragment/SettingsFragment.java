package github.daisukiKaffuChino.MomoQR.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Arrays;
import java.util.Objects;

import github.daisukiKaffuChino.MomoQR.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference_settings_root, rootKey);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            Preference dynamicColor=findPreference("dynamicColor");
            assert dynamicColor != null;
            dynamicColor.setSummary(R.string.require_android_s);
            dynamicColor.setEnabled(false);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Preference notAskForSavePath=findPreference("notAskForSavePath");
            assert notAskForSavePath != null;
            notAskForSavePath.setSummary(R.string.require_android_q);
            notAskForSavePath.setEnabled(false);
        }

        findPreference("opensource").setOnPreferenceClickListener(preference -> {
            Uri uriSetting = Uri.parse("https://github.com/daisukiKaffuChino/MomoQR");
            Intent settingsIntent = new Intent(Intent.ACTION_VIEW, uriSetting);
            startActivity(settingsIntent);
            return true;
        });

        findPreference("appInfo").setTitle(getVerInfo());
    }

    private String getVerInfo() {
        try {
            PackageInfo packageInfo = requireContext().getPackageManager().getPackageInfo("github.daisukiKaffuChino.MomoQR", 0);
            String versionName = packageInfo.versionName;
            String versionCode = String.valueOf(packageInfo.versionCode);
            return versionName + " (" + versionCode + ")";
        } catch (PackageManager.NameNotFoundException e) {
            return "ERROR!";
        }
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
        } else if (key.equals("enableMaterial3") | key.equals("dynamicColor")) {
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

}
