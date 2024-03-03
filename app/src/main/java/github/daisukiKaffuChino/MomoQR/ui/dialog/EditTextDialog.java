package github.daisukiKaffuChino.MomoQR.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import github.daisukiKaffuChino.MomoQR.R;
import github.daisukiKaffuChino.MomoQR.logic.bean.ResultArgs;

public class EditTextDialog extends DialogFragment {
    public final static String MODE_INPUT_ONLY = "MODE_INPUT_ONLY";
    public final static String MODE_INPUT_WITH_CHECKBOX = "MODE_INPUT_WITH_CHECKBOX";
    protected ResultArgs mArgs;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mArgs = new ResultArgs(getArguments());
        Bundle businessBundle = mArgs.getBusinessArgs();
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());

        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.dialog_edittext, null);
        EditText editText = view.findViewById(R.id.dialog_edt);
        TextInputLayout textInputLayout = view.findViewById(R.id.dialog_edt_layout);
        CheckBox checkBox = view.findViewById(R.id.dialog_checkbox);

        String MODE = businessBundle.getString("mode", MODE_INPUT_ONLY);
        if (Objects.equals(MODE, MODE_INPUT_ONLY)) {
            builder.setTitle(R.string.content_to_generate);
            textInputLayout.setHint(R.string.input_content);
            checkBox.setVisibility(View.GONE);
        } else {
            builder.setTitle(R.string.add_fav);
            textInputLayout.setHint(R.string.input_title);
        }

        builder.setView(view);
        builder.setPositiveButton(R.string.ok, null);
        builder.setNegativeButton(R.string.cancel, null);

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);

        alertDialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
                int recipientId = mArgs.getRecipientId();
                int reqCode = mArgs.getRequestCode();

                String contentText = editText.getText().toString();
                Log.i("Home",contentText);
                SavedStateHandle stateHandle = navController.getBackStackEntry(recipientId).getSavedStateHandle();
                stateHandle.getLiveData(String.valueOf(reqCode)).postValue(new Pair<>(reqCode, contentText));
                navController.navigateUp();
            });
        });
        return alertDialog;
    }

}
