package github.daisukiKaffuChino.MomoQR.ui.fragment;

import android.annotation.SuppressLint;
import android.app.StatusBarManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import github.daisukiKaffuChino.MomoQR.R;
import github.daisukiKaffuChino.MomoQR.databinding.FragmentResultBinding;
import github.daisukiKaffuChino.MomoQR.logic.utils.ActionUtil;
import github.daisukiKaffuChino.MomoQR.logic.utils.QRCodeUtil;
import github.daisukiKaffuChino.MomoQR.ui.model.ResultViewModel;
import github.daisukiKaffuChino.MomoQR.ui.view.ShowImageDialog;
import github.daisukiKaffuChino.MomoQR.ui.view.colorpicker.ColorModel;
import github.daisukiKaffuChino.MomoQR.ui.view.colorpicker.ColorPickerDialog;

public class ResultFragment extends BaseBindingFragment<FragmentResultBinding> {
    FragmentResultBinding binding;
    ResultViewModel viewModel;
    ActionUtil actionUtil;
    Bitmap generatedQRBitmap;
    SharedPreferences sp;

    @Override
    protected FragmentResultBinding onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent) {
        return FragmentResultBinding.inflate(inflater, parent, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = getBinding();

        initTips();

        binding.copyBtn.setOnClickListener(v ->
                ActionUtil.copyContent(Objects.requireNonNull(binding.resultText.getText()).toString()));
        binding.addFavBtn.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("mode", EditTextDialogFragment.MODE_INPUT_WITH_CHECKBOX);
            bundle.putString("content", viewModel.contentLiveData.getValue());
            //bundle.putString("imgPath", actionUtil.saveImageViewImage(binding.remakeCodeImg));
            getNavController().navigate(R.id.nav_edt_dialog, bundle);
        });
        binding.openLinkBtn.setOnClickListener(v ->
                ActionUtil.detectIntentAndStart(viewModel.contentLiveData.getValue()));

        binding.addFavBtn.setEnabled(!viewModel.isFromFav);

        binding.resultOriginImageBtn.setEnabled(!viewModel.isFromFav && viewModel.pathLiveData.getValue() != null);

        binding.resultOriginImageBtn.setOnClickListener(v -> new ShowImageDialog(requireContext(), viewModel.pathLiveData.getValue()).show());

        binding.resultShareBtn.setOnClickListener(v -> new ShareCompat.IntentBuilder(requireContext())
                .setType("text/plain")
                .setChooserTitle("From MomoQR")
                .setText(viewModel.contentLiveData.getValue())
                .startChooser());

        binding.resultSaveBtn.setOnClickListener(v -> {
            if (generatedQRBitmap == null)
                ActionUtil.toast(R.string.wait_for_generate);
            else
                saveBitmapLocal(generatedQRBitmap);
        });

        binding.resultQrPaletteForeBtn.setOnClickListener(v -> showPaletteDialog("qrForegroundColor"));
        binding.resultQrPaletteBgBtn.setOnClickListener(v -> showPaletteDialog("qrBackgroundColor"));

        binding.resultQrPalettePresetsBtn.setOnClickListener(v -> {
            actionUtil.decorBlur(requireActivity().getWindow().getDecorView(), true);
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
            builder.setTitle(getString(R.string.presets));
            String[] items = {"White/Black", "Momoi", "Midori", "Yuzu", "Alice"};
            builder.setItems(items, (dialog, which) -> {
                int fore, back;
                switch (items[which]) {
                    case "Momoi":
                        fore = Color.WHITE;
                        back = Color.parseColor("#F59BC3");
                        break;
                    case "Midori":
                        fore = Color.parseColor("#B8DE58");
                        back = Color.parseColor("#2A374D");
                        break;
                    case "Yuzu":
                        fore = Color.parseColor("#F5B877");
                        back = Color.parseColor("#AC4457");
                        break;
                    case "Alice":
                        fore = Color.parseColor("#5DBDFF");
                        back = Color.parseColor("#7B8393");
                        break;
                    default:
                        fore = Color.BLACK;
                        back = Color.WHITE;
                }
                SharedPreferences.Editor edt = sp.edit();
                edt.putInt("qrForegroundColor", fore);
                edt.putInt("qrBackgroundColor", back);
                edt.apply();
                viewModel.qrForegroundColor.setValue(fore);
                viewModel.qrBackgroundColor.setValue(back);
            });
            builder.setOnDismissListener(
                    dialog -> actionUtil.decorBlur(requireActivity().getWindow().getDecorView(), false)
            );
            builder.show();
        });

        viewModel.contentLiveData.observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                binding.resultText.setText(result);
                viewModel.qrForegroundColor.setValue(sp.getInt("qrForegroundColor", Color.BLACK));
                viewModel.qrBackgroundColor.setValue(sp.getInt("qrBackgroundColor", Color.WHITE));
            }
        });

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = PreferenceManager.getDefaultSharedPreferences(requireContext());
        actionUtil = new ActionUtil(requireContext());
        viewModel = new ViewModelProvider(this).get(ResultViewModel.class);
        if (getArguments() != null) {
            String content = getArguments().getString("content");
            String imgPath = getArguments().getString("imgPath");
            boolean isFromFav = getArguments().getBoolean("isFromFav", false);
            viewModel.contentLiveData.setValue(content);
            viewModel.pathLiveData.setValue(imgPath);
            viewModel.isFromFav = isFromFav;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.qrForegroundColor.observe(getViewLifecycleOwner(), result ->
                displayQrImage(viewModel.contentLiveData.getValue(), result, sp.getInt("qrBackgroundColor", Color.WHITE)));
        viewModel.qrBackgroundColor.observe(getViewLifecycleOwner(), result ->
                displayQrImage(viewModel.contentLiveData.getValue(), sp.getInt("qrForegroundColor", Color.BLACK), result));
    }

    private void displayQrImage(String content, int fore, int back) {
        if (content != null) {
            generatedQRBitmap = QRCodeUtil.INSTANCE.createQRCodeBitmap(content, 180, 180, fore, back);
            Glide.with(requireContext()).load(generatedQRBitmap).into(binding.remakeCodeImg);
        }
    }

    private void saveBitmapLocal(Bitmap bitmap) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(requireContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q & sp.getBoolean("notAskForSavePath", false)) {
            //悄无声息地保存
            QRCodeUtil.INSTANCE.saveBitmapAboveQ(requireActivity(), bitmap);
        } else {
            //申请临时uri保存
            String fileName = "QR" + System.currentTimeMillis() + ".png";
            saveRequest.launch(fileName);
        }
    }

    private final ActivityResultLauncher<String> saveRequest = registerForActivityResult(
            new ActivityResultContracts.CreateDocument("image/png"),
            uri -> saveBitmapViaUri(requireContext(), uri));

    private void saveBitmapViaUri(Context context, Uri uri) {
        ParcelFileDescriptor pfd = null;
        try {
            if (uri != null) {
                pfd = context.getContentResolver().openFileDescriptor(uri, "rw");
                assert pfd != null;
                BufferedOutputStream bos =
                        new BufferedOutputStream(new FileOutputStream(pfd.getFileDescriptor()));
                generatedQRBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                bos.flush();
                bos.close();

                //尝试获取文件名
                ContentResolver cr = context.getContentResolver();
                Cursor cur = cr.query(
                        uri, new String[]{MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null);
                String name = "";
                if (cur != null) {
                    if (cur.moveToFirst()) {
                        int index = cur.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME);
                        name = cur.getString(index);
                    }
                    cur.close();
                }

                actionUtil.showMessageDialog(
                        requireActivity(),
                        getString(R.string.save_ok),
                        name);
            }
        } catch (IOException e) {
            e.fillInStackTrace();
        } finally {
            try {
                if (pfd != null)
                    pfd.close();
            } catch (IOException e1) {
                e1.fillInStackTrace();
            }
        }
    }

    private void showPaletteDialog(String dataName) {
        int initialColor;
        if (dataName.equals("qrForegroundColor"))
            initialColor = sp.getInt("qrForegroundColor", Color.BLACK);
        else
            initialColor = sp.getInt("qrBackgroundColor", Color.WHITE);
        ColorPickerDialog colorPickerDialog = new ColorPickerDialog.Builder()
                .setInitialColor(initialColor)
                .setColorModel(ColorModel.RGB)
                .onColorSelected(color -> {
                    SharedPreferences.Editor edt = sp.edit();
                    edt.putInt(dataName, color);
                    edt.apply();
                    if (dataName.equals("qrForegroundColor"))
                        viewModel.qrForegroundColor.setValue(color);
                    else
                        viewModel.qrBackgroundColor.setValue(color);
                })
                .create();
        colorPickerDialog.show(getChildFragmentManager(), "qr_palette");
    }

    private void initTips() {
        if (sp.getBoolean("hideTips", false)) {
            binding.resultTipCard.setVisibility(View.GONE);
        } else {
            binding.resultTipHideBtn.setOnClickListener(view -> {
                binding.resultTipCard.setVisibility(View.GONE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("hideTips", true);
                editor.apply();
            });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                binding.resultTipTileBtn.setVisibility(View.VISIBLE);
                binding.resultTipTileBtn.setOnClickListener(view -> {
                    @SuppressLint("WrongConstant") StatusBarManager manager = (StatusBarManager) requireActivity().getSystemService(Context.STATUS_BAR_SERVICE);
                    ComponentName componentName = new ComponentName("github.daisukiKaffuChino.MomoQR",
                            "github.daisukiKaffuChino.MomoQR.service.ScanTileService");
                    Icon icon = Icon.createWithResource(requireActivity(), R.drawable.outline_qr_code_scanner_24);
                    manager.requestAddTileService(componentName, "Scan Tile", icon, runnable -> {
                    }, integer -> {
                    });
                });
            }
        }
    }

}
