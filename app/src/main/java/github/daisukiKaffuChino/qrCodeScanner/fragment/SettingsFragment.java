package github.daisukiKaffuChino.qrCodeScanner.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
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

import github.daisukiKaffuChino.qrCodeScanner.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference_settings_root, rootKey);

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
            PackageInfo packageInfo = requireContext().getPackageManager().getPackageInfo("github.daisukiKaffuChino.qrCodeScanner", 0);
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
        if (key.equals("forceDark")) {
            if (sharedPreferences.getBoolean(key, false)) {
                ((AppCompatActivity) requireActivity()).getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                ((AppCompatActivity) requireActivity()).getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
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
