package github.daisukiKaffuChino.MomoQR.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;

import github.daisukiKaffuChino.MomoQR.R;
import github.daisukiKaffuChino.MomoQR.databinding.DialogCreateEmailQrBinding;
import github.daisukiKaffuChino.MomoQR.logic.utils.ActionUtil;

public class MailQrCreateDialogFragment extends DialogFragment {
    DialogCreateEmailQrBinding binding;
    ActionUtil actionUtil;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        actionUtil = new ActionUtil(requireContext());
        actionUtil.decorBlur(requireActivity().getWindow().getDecorView(), true);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());

        binding = DialogCreateEmailQrBinding.inflate(getLayoutInflater());
        builder.setView(binding.getRoot());
        builder.setTitle(R.string.email);

        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            String mailAddress = Objects.requireNonNull(binding.dialogMailAddressEdt.getText()).toString();
            String mailTitle = Objects.requireNonNull(binding.dialogMailTitleEdt.getText()).toString();
            String mailContent = Objects.requireNonNull(binding.dialogMailContentEdt.getText()).toString();
            if (TextUtils.isEmpty(mailAddress) || TextUtils.isEmpty(mailTitle) || TextUtils.isEmpty(mailContent)) {
                ActionUtil.toast(R.string.empty_data);
                return;
            }
            String result = "MATMSG:TO:" + mailAddress +
                    ";SUB:" + mailTitle + ";BODY:" +
                    mailContent + ";;";
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            Bundle args = new Bundle();
            args.putString("content", result);
            navController.navigateUp();
            navController.navigate(R.id.nav_result, args);
        });
        builder.setNegativeButton(R.string.cancel, null);
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
}
