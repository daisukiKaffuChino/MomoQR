package github.daisukiKaffuChino.MomoQR.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import github.daisukiKaffuChino.MomoQR.R;
import github.daisukiKaffuChino.MomoQR.logic.utils.ActionUtil;
import github.daisukiKaffuChino.MomoQR.logic.utils.QRCodeUtil;

public class EditTextDialogFragment extends DialogFragment {
    public final static String MODE_INPUT_ONLY = "MODE_INPUT_ONLY";
    public final static String MODE_INPUT_WITH_CHECKBOX = "MODE_INPUT_WITH_CHECKBOX";
    ActionUtil actionUtil;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        actionUtil = new ActionUtil(requireContext());
        actionUtil.decorBlur(requireActivity().getWindow().getDecorView(), true);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());

        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.dialog_edittext, null);
        EditText editText = view.findViewById(R.id.dialog_edt);
        TextInputLayout textInputLayout = view.findViewById(R.id.dialog_edt_layout);
        CheckBox checkBox = view.findViewById(R.id.dialog_checkbox);

        assert getArguments() != null;
        String MODE = getArguments().getString("mode", MODE_INPUT_ONLY);
        String favContent = getArguments().getString("content", "");
        //String favImagePath = getArguments().getString("imgPath", "");
        String ext = getArguments().getString("ext", "");

        editText.setText(ext);
        if (Objects.equals(MODE, MODE_INPUT_ONLY)) {
            builder.setTitle(R.string.content_to_generate);
            textInputLayout.setHint(R.string.input_content);
            checkBox.setVisibility(View.GONE);
        } else if (Objects.equals(MODE, MODE_INPUT_WITH_CHECKBOX)) {
            builder.setTitle(R.string.add_fav);
            textInputLayout.setHint(R.string.input_title);
            builder.setNeutralButton(R.string.use_current_date, null);
        }

        builder.setView(view);
        builder.setPositiveButton(R.string.ok, null);
        builder.setNegativeButton(R.string.cancel, null);

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);

        alertDialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);

            positiveButton.setOnClickListener(v -> {
                String contentText = editText.getText().toString();
                if (contentText.isEmpty()) {
                    ActionUtil.toast(R.string.empty_data);
                    return;
                }
                if (Objects.equals(MODE, MODE_INPUT_ONLY)) {
                    Bundle args = new Bundle();
                    args.putString("content", contentText);
                    navController.navigateUp();
                    navController.navigate(R.id.nav_result, args);
                } else if (Objects.equals(MODE, MODE_INPUT_WITH_CHECKBOX)) {
                    saveToFav(contentText, favContent, checkBox.isChecked());
                    navController.navigateUp();
                }

            });
            if (Objects.equals(MODE, MODE_INPUT_WITH_CHECKBOX)) {
                Button neutralButton = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                neutralButton.setOnClickListener(v -> {
                    saveToFav(ActionUtil.currentTime(), favContent, checkBox.isChecked());
                    navController.navigateUp();
                });
            }
        });
        return alertDialog;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        actionUtil.decorBlur(requireActivity().getWindow().getDecorView(), false);
    }

    private void saveToFav(String title, String content, boolean isStar) {
        ActionUtil actionUtil = new ActionUtil(requireContext());
        Bitmap bitmap = QRCodeUtil.INSTANCE.createQRCodeBitmap(content, 180, 180, Color.BLACK, Color.WHITE);
        String favImagePath = actionUtil.saveBitmapPrivate(bitmap);
        if (favImagePath != null) actionUtil.addFav(title, content, favImagePath, isStar);
    }

}
