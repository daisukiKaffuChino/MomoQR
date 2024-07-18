package github.daisukiKaffuChino.MomoQR.ui.fragment;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.google.zxing.Result;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.Objects;

import github.daisukiKaffuChino.MomoQR.CaptureActivity;
import github.daisukiKaffuChino.MomoQR.R;
import github.daisukiKaffuChino.MomoQR.databinding.FragmentHomeBinding;
import github.daisukiKaffuChino.MomoQR.logic.utils.ActionUtil;
import github.daisukiKaffuChino.MomoQR.logic.utils.MomoDefender;
import github.daisukiKaffuChino.MomoQR.logic.utils.QRCodeUtil;

public class HomeFragment extends BaseBindingFragment<FragmentHomeBinding> {
    @Override
    protected FragmentHomeBinding onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent) {
        return FragmentHomeBinding.inflate(inflater, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentHomeBinding binding = getBinding();
        binding.homeScanBtn.setOnClickListener(v -> startScannerIntent());
        binding.homeSelectImageBtn.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
                intent.setType("image/*");
                openGalleryRequest.launch(intent);
            } else {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                openGalleryRequest.launch(Intent.createChooser(intent, getString(R.string.select_gallery_pic)));
            }
        });
        binding.homeCreateFromTextBtn.setOnClickListener(v ->
                navigateEditTextDialog(null));
        binding.homeCreateFromClipboardBtn.setOnClickListener(v -> {
            ClipboardManager clipboardManager = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            if (!clipboardManager.hasPrimaryClip() ||
                    !Objects.requireNonNull(clipboardManager.getPrimaryClipDescription()).hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                ActionUtil.toast(R.string.clipboard_error_toast);
            } else {
                ClipData clipData = clipboardManager.getPrimaryClip();
                if (clipData != null && clipData.getItemCount() > 0) {
                    CharSequence text = clipData.getItemAt(0).getText();
                    navigateEditTextDialog(text.toString());
                }
            }
        });
        binding.homeCreateMoreTypeBtn.setOnClickListener(v ->
                getNavController().navigate(R.id.nav_qr_create_list));
        binding.homeStartCardHelpBtn.setOnClickListener(v ->
                getNavController().navigate(R.id.nav_help));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MomoDefender momoDefender = new MomoDefender(requireContext());
        //if(momoDefender.get()) throw new RuntimeException();
        if (getArguments() != null && getArguments().getBoolean("startScanIntent", false))
            startScannerIntent();
    }

    private void startScannerIntent() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(requireContext());
        ScanOptions options = new ScanOptions();
        switch (Integer.parseInt(sp.getString("identifyType", "0"))) {
            case 0:
                options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
                break;
            case 1:
                options.setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES);
                break;
            case 2:
                options.setDesiredBarcodeFormats(ScanOptions.PRODUCT_CODE_TYPES);
                break;
            case 3:
                options.setDesiredBarcodeFormats(ScanOptions.DATA_MATRIX);
                break;
            case 4:
                options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES);
        }
        options.setCameraId(Integer.parseInt(sp.getString("cameraId", "0")));
        options.setBeepEnabled(sp.getBoolean("beepSound", true));
        options.setBarcodeImageEnabled(true);
        options.setCaptureActivity(CaptureActivity.class);
        options.setOrientationLocked(sp.getBoolean("lockOrientation", true));
        barcodeLauncher.launch(options);
    }

    private void navigateResult(String result, @Nullable String imgPath) {
        Bundle args = new Bundle();
        args.putString("content", result);
        args.putString("imgPath", imgPath);
        getNavController().navigate(R.id.nav_result, args);
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if (result.getContents() == null)
                    ActionUtil.toast(R.string.scan_cancel);
                else
                    navigateResult(result.getContents(), result.getBarcodeImagePath());
            });

    private final ActivityResultLauncher<Intent> openGalleryRequest = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    assert intent != null;
                    Result mResult = QRCodeUtil.INSTANCE.scanningImage(requireContext(),
                            Objects.requireNonNull(intent.getData()));
                    if (mResult != null) {
                        navigateResult(mResult.getText(), null);
                    } else {
                        ActionUtil.toast(R.string.empty_data);
                    }
                }
            });

    private void navigateEditTextDialog(@Nullable String ext) {
        Bundle bundle = new Bundle();
        bundle.putString("mode", EditTextDialogFragment.MODE_INPUT_ONLY);
        if (ext != null) bundle.putString("ext", ext);
        getNavController().navigate(R.id.nav_edt_dialog, bundle);
    }

}
