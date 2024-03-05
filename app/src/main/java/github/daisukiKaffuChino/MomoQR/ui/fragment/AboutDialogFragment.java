package github.daisukiKaffuChino.MomoQR.ui.fragment;

import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import github.daisukiKaffuChino.MomoQR.databinding.DialogAboutBinding;

public class AboutDialogFragment extends DialogFragment {
    DialogAboutBinding binding;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding=DialogAboutBinding.inflate(getLayoutInflater());
        MaterialAlertDialogBuilder builder=new MaterialAlertDialogBuilder(requireActivity())
                .setView(binding.getRoot());
        binding.aboutVerSubTitle.setText(getVerInfo());
        binding.aboutOpensourceText.setMovementMethod(LinkMovementMethod.getInstance());
        return builder.create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
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
}
