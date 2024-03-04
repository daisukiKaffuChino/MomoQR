package github.daisukiKaffuChino.MomoQR.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import github.daisukiKaffuChino.MomoQR.logic.utils.MyUtil;

public class EditTextDialogFragment extends DialogFragment {
    public final static String MODE_INPUT_ONLY = "MODE_INPUT_ONLY";
    public final static String MODE_INPUT_WITH_CHECKBOX = "MODE_INPUT_WITH_CHECKBOX";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        MyUtil myUtil = new MyUtil(requireContext());
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());

        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.dialog_edittext, null);
        EditText editText = view.findViewById(R.id.dialog_edt);
        TextInputLayout textInputLayout = view.findViewById(R.id.dialog_edt_layout);
        CheckBox checkBox = view.findViewById(R.id.dialog_checkbox);

        assert getArguments() != null;
        String MODE = getArguments().getString("mode", MODE_INPUT_ONLY);
        String favContent = getArguments().getString("content", "");
        String favImagePath = getArguments().getString("imgPath", "");

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
            Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);

            positiveButton.setOnClickListener(v -> {
                String contentText = editText.getText().toString();
                if (Objects.equals(MODE, MODE_INPUT_ONLY)) {
                    Bundle args = new Bundle();
                    args.putString("content", contentText);
                    navController.navigateUp();
                    navController.navigate(R.id.nav_result, args);
                } else if (Objects.equals(MODE, MODE_INPUT_WITH_CHECKBOX)) {
                    myUtil.addFav(contentText, favContent, favImagePath, checkBox.isChecked());
                    navController.navigateUp();
                }
            });
            if (Objects.equals(MODE, MODE_INPUT_WITH_CHECKBOX)) {
                Button neutralButton = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                neutralButton.setOnClickListener(v -> {
                    myUtil.addFav(MyUtil.currentTime(), favContent, favImagePath, checkBox.isChecked());
                    navController.navigateUp();
                });
            }
            negativeButton.setOnClickListener(v -> navController.navigateUp());
        });
        return alertDialog;
    }


}
