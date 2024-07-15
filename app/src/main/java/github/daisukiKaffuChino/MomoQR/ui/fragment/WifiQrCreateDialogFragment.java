package github.daisukiKaffuChino.MomoQR.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;

import github.daisukiKaffuChino.MomoQR.R;
import github.daisukiKaffuChino.MomoQR.databinding.DialogCreateWifiQrBinding;
import github.daisukiKaffuChino.MomoQR.logic.utils.ActionUtil;

public class WifiQrCreateDialogFragment extends DialogFragment {
    DialogCreateWifiQrBinding binding;
    String[] spinnerStrings = {"WEP", "WPA/WPA2 PSK", "-"};
    ActionUtil actionUtil;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DialogCreateWifiQrBinding.inflate(getLayoutInflater());
        actionUtil = new ActionUtil(requireContext());
        actionUtil.decorBlur(requireActivity().getWindow().getDecorView(), true);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        builder.setTitle(R.string.wifi);
        builder.setView(binding.getRoot());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, spinnerStrings);
        binding.dialogWifiSpinner.setAdapter(adapter);

        binding.dialogWifiSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 2)
                    binding.dialogWifiPasswordEdtParent.setVisibility(View.GONE);
                else
                    binding.dialogWifiPasswordEdtParent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            String wifiName = Objects.requireNonNull(binding.dialogWifiNameEdt.getText()).toString();
            String wifiPassword = Objects.requireNonNull(binding.dialogWifiPasswordEdt.getText()).toString();
            if (!TextUtils.isEmpty(wifiName)) {
                String content = makeWifiShareContent(wifiName, wifiPassword);
                if (Objects.equals(content, "")) {
                    ActionUtil.toast(R.string.empty_data);
                    return;
                }
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
                Bundle args = new Bundle();
                args.putString("content", content);
                navController.navigateUp();
                navController.navigate(R.id.nav_result, args);
            } else {
                ActionUtil.toast(R.string.empty_data);
            }
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

    private String makeWifiShareContent(String name, String password) {
        String securityType = binding.dialogWifiSpinner.getSelectedItem().toString();
        String result = "";
        if (securityType.equals(spinnerStrings[0]) && !TextUtils.isEmpty(password)) {
            result = "WIFI:S:" + name + ";T:WEP;P:" + password + ";;";
        } else if (securityType.equals(spinnerStrings[1]) && !TextUtils.isEmpty(password)) {
            result = "WIFI:S:" + name + ";T:WPA:P:" + password + ";;";
        } else if (securityType.equals(spinnerStrings[2])) {
            result = "WIFI:S:" + name + ";T:nopass;P:;;";
        }
        return result;
    }
}
