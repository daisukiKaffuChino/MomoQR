package github.daisukiKaffuChino.MomoQR.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
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
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.zxing.Result;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.Objects;

import github.daisukiKaffuChino.MomoQR.CaptureActivity;
import github.daisukiKaffuChino.MomoQR.R;
import github.daisukiKaffuChino.MomoQR.databinding.FragmentHomeBinding;
import github.daisukiKaffuChino.MomoQR.logic.utils.FavSqliteHelper;
import github.daisukiKaffuChino.MomoQR.logic.utils.MyUtil;
import github.daisukiKaffuChino.MomoQR.logic.utils.QRCodeUtil;
import github.daisukiKaffuChino.MomoQR.ui.model.HomeViewModel;

public class HomeFragment extends BaseBindingFragment<FragmentHomeBinding> {
    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    FavSqliteHelper helper;
    private static final int EDITTEXT_DIALOG_FAV_TITLE = 0;
    private static final int EDITTEXT_DIALOG_QRCODE_CONTENT = 1;

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
        binding.makeQRCodeBtn.setOnClickListener(v ->
                showEditTextDialog(EDITTEXT_DIALOG_QRCODE_CONTENT));
        binding.retryBtn.setOnClickListener(v -> {
            viewModel.isScanned = false;
            btnRootVisible(true);
        });
        binding.copyBtn.setOnClickListener(v ->
                MyUtil.copyContent(Objects.requireNonNull(binding.resultText.getText()).toString()));
        binding.addFavBtn.setOnClickListener(v -> showEditTextDialog(EDITTEXT_DIALOG_FAV_TITLE));
        binding.openLinkBtn.setOnClickListener(v ->
                MyUtil.detectIntentAndStart(viewModel.contentLiveData.getValue()));
        binding.remakeCodeImg.setOnLongClickListener(v -> {
            v.setDrawingCacheEnabled(true);
            QRCodeUtil.INSTANCE.saveBitmap(requireContext(), v.getDrawingCache());
            v.setDrawingCacheEnabled(false);
            return true;
        });

        viewModel.contentLiveData.observe(getViewLifecycleOwner(), result -> {
            if (result != null & viewModel.isScanned) {
                showScanResults(result, false);
            }
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

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    MyUtil.toast(R.string.scan_cancel);
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
                    Result mResult = QRCodeUtil.INSTANCE.scanningImage(requireContext(),
                            Objects.requireNonNull(intent.getData()));
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

    private void showEditTextDialog(int mode) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        builder.setView(R.layout.dialog_edittext);
        builder.setNegativeButton(R.string.cancel, null);
        if (mode == EDITTEXT_DIALOG_FAV_TITLE) {
            builder.setTitle(R.string.add_fav);
            builder.setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                EditText edt = ((AlertDialog) dialogInterface).findViewById(R.id.dialog_edt);
                if (edt != null)
                    addFav(edt.getText().toString(), viewModel.contentLiveData.getValue());
            });
            builder.setNeutralButton(R.string.use_current_date, (dialogInterface, i) ->
                    addFav(MyUtil.currentTime(), viewModel.contentLiveData.getValue()));
        } else if (mode == EDITTEXT_DIALOG_QRCODE_CONTENT) {
            builder.setTitle(R.string.content_to_generate);
            builder.setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                EditText edt = ((AlertDialog) dialogInterface).findViewById(R.id.dialog_edt);
                assert edt != null;
                if (!TextUtils.isEmpty(edt.getText().toString()))
                    showScanResults(edt.getText().toString(), true);
            });
        }
        builder.show();
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
