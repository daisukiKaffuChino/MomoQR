package github.daisukiKaffuChino.MomoQR.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import github.daisukiKaffuChino.MomoQR.databinding.DialogAboutBinding;
import github.daisukiKaffuChino.MomoQR.logic.utils.ActionUtil;

public class AboutDialogFragment extends DialogFragment {
    DialogAboutBinding binding;
    ActionUtil actionUtil;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DialogAboutBinding.inflate(getLayoutInflater());
        actionUtil = new ActionUtil(requireContext());
        actionUtil.decorBlur(requireActivity().getWindow().getDecorView(), true);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity())
                .setView(binding.getRoot());
        binding.aboutVerSubTitle.setText(getVerInfo());
        binding.aboutOpensourceText.setMovementMethod(LinkMovementMethod.getInstance());
        return builder.create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        actionUtil.decorBlur(requireActivity().getWindow().getDecorView(), false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
