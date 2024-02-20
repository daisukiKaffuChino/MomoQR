package github.daisukiKaffuChino.qrCodeScanner.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.zxing.Result;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.Objects;

import github.daisukiKaffuChino.qrCodeScanner.CaptureActivity;
import github.daisukiKaffuChino.qrCodeScanner.R;
import github.daisukiKaffuChino.qrCodeScanner.databinding.FragmentHomeBinding;
import github.daisukiKaffuChino.qrCodeScanner.model.HomeViewModel;
import github.daisukiKaffuChino.qrCodeScanner.util.FavSqliteHelper;
import github.daisukiKaffuChino.qrCodeScanner.util.MyUtil;
import github.daisukiKaffuChino.qrCodeScanner.util.QRCodeUtil;

public class HomeFragment extends BaseBindingFragment<FragmentHomeBinding> {
    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    FavSqliteHelper helper;

    @Override
    protected FragmentHomeBinding onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent) {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        helper = new FavSqliteHelper(requireContext());
        return FragmentHomeBinding.inflate(inflater, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = getBinding();
        binding.scanBtn.setOnClickListener(v -> startScannerIntent());
        binding.selectImageBtn.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            openGalleryRequest.launch(Intent.createChooser(intent, getString(R.string.select_gallery_pic)));
        });
        binding.retryBtn.setOnClickListener(v -> {
            viewModel.isScanned = false;
            btnRootVisible(true);
        });
        binding.copyBtn.setOnClickListener(v ->
                MyUtil.copyContent(Objects.requireNonNull(binding.resultText.getText()).toString()));
        binding.addFavBtn.setOnClickListener(v -> {
            showFavDialog();
        });
        viewModel.contentLiveData.observe(getViewLifecycleOwner(), result -> {
            if (result != null & viewModel.isScanned) {
                showScanResults(result, false);
            }
        });
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

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    MyUtil.toast( R.string.scan_cancel);
                } else {
                    showScanResults(result.getContents(), true);
                }
            });

    private final ActivityResultLauncher<Intent> openGalleryRequest = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    assert intent != null;
                    Result mResult = QRCodeUtil.INSTANCE.scanningImage(requireContext(), intent.getData());
                    if (mResult != null) {
                        showScanResults(mResult.getText(), true);
                    } else {
                        MyUtil.toast(R.string.empty_data);
                    }
                }
            });


    private void showScanResults(String content, boolean isSet) {
        if (isSet) {
            viewModel.contentLiveData.setValue(content);
        }
        viewModel.isScanned = true;
        btnRootVisible(false);
        if (content != null) {
            binding.resultText.setText(content);
            Bitmap bitmap = QRCodeUtil.INSTANCE.createQRCodeBitmap(content, 180, 180, Color.BLACK, Color.WHITE);
            Glide.with(requireContext()).load(bitmap).into(binding.remakeCodeImg);
        }
    }

    private void showFavDialog() {
        new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.add_fav)
                .setView(R.layout.dialog_edittext)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    EditText edt = ((AlertDialog) dialogInterface).findViewById(R.id.dialog_edt);
                    if (edt != null) {
                        String inputText = edt.getText().toString();
                        addFav(inputText, viewModel.contentLiveData.getValue());
                    }
                })
                .setNeutralButton(R.string.use_current_date, (dialogInterface, i) ->
                        addFav(MyUtil.currentTime(), viewModel.contentLiveData.getValue()))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void addFav(String title, String content) {
        if (MyUtil.hasSpecialChat(title)) {
            MyUtil.toast(R.string.invalid_title);
        } else {
            String imageSavedPath = new MyUtil().saveImageViewImage(requireContext(), binding.remakeCodeImg);
            if (imageSavedPath != null) {
                boolean insertOk = helper.insertData(title, content, imageSavedPath, System.currentTimeMillis());
                if (insertOk)
                    MyUtil.toast(R.string.add_fav_ok);
                else
                    MyUtil.toast(R.string.add_fav_fail);
            } else {
                MyUtil.toast(R.string.add_fav_fail);
            }
        }
    }

    private void btnRootVisible(boolean visible) {
        if (visible) {
            binding.homeBtnRoot.setVisibility(View.VISIBLE);
            binding.resultRoot.setVisibility(View.GONE);
        } else {
            binding.homeBtnRoot.setVisibility(View.GONE);
            binding.resultRoot.setVisibility(View.VISIBLE);
        }
    }

}
