package github.daisukiKaffuChino.MomoQR.ui.fragment;

import android.app.Activity;
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
import github.daisukiKaffuChino.MomoQR.logic.utils.MyUtil;
import github.daisukiKaffuChino.MomoQR.logic.utils.QRCodeUtil;

public class HomeFragment extends BaseBindingFragment<FragmentHomeBinding> {
    //private HomeViewModel viewModel;
    @Override
    protected FragmentHomeBinding onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent) {
        //viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
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
        binding.homeCreateFromTextBtn.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("mode", EditTextDialogFragment.MODE_INPUT_ONLY);
            getNavController().navigate(R.id.nav_edt_dialog, bundle);
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    private void navigateResult(String result) {
        Bundle args = new Bundle();
        args.putString("content", result);
        getNavController().navigate(R.id.nav_result, args);
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    MyUtil.toast(R.string.scan_cancel);
                } else {
                    navigateResult(result.getContents());
                }
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
                        navigateResult(mResult.getText());
                    } else {
                        MyUtil.toast(R.string.empty_data);
                    }
                }
            });

}
