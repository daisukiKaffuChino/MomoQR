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
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import github.daisukiKaffuChino.MomoQR.R;
import github.daisukiKaffuChino.MomoQR.databinding.FragmentResultBinding;
import github.daisukiKaffuChino.MomoQR.logic.utils.ActionUtil;
import github.daisukiKaffuChino.MomoQR.logic.utils.QRCodeUtil;
import github.daisukiKaffuChino.MomoQR.ui.model.ResultViewModel;

public class ResultFragment extends BaseBindingFragment<FragmentResultBinding> {
    FragmentResultBinding binding;
    ResultViewModel viewModel;
    ActionUtil actionUtil;

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
            bundle.putString("imgPath", actionUtil.saveImageViewImage(binding.remakeCodeImg));
            getNavController().navigate(R.id.nav_edt_dialog, bundle);
        });
        binding.openLinkBtn.setOnClickListener(v ->
                ActionUtil.detectIntentAndStart(viewModel.contentLiveData.getValue()));
        binding.remakeCodeImg.setOnLongClickListener(v -> {
            //TODO 更换为新方法
            v.setDrawingCacheEnabled(true);
            saveBitmapLocal(v.getDrawingCache());
            v.setDrawingCacheEnabled(false);
            return true;
        });

        binding.addFavBtn.setEnabled(!viewModel.isFromFav);

        viewModel.contentLiveData.observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                showScanResults(result);
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionUtil = new ActionUtil(requireContext());
        viewModel = new ViewModelProvider(this).get(ResultViewModel.class);
        if (getArguments() != null) {
            String content = getArguments().getString("content");
            boolean isFromFav = getArguments().getBoolean("isFromFav", false);
            viewModel.contentLiveData.setValue(content);
            viewModel.isFromFav = isFromFav;
        }
    }

    private void showScanResults(String content) {
        if (content != null) {
            binding.resultText.setText(content);
            Bitmap bitmap = QRCodeUtil.INSTANCE.createQRCodeBitmap(content, 180, 180, Color.BLACK, Color.WHITE);
            Glide.with(requireContext()).load(bitmap).into(binding.remakeCodeImg);
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
                binding.remakeCodeImg.setDrawingCacheEnabled(true);
                Bitmap bitmap = binding.remakeCodeImg.getDrawingCache();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                binding.remakeCodeImg.setDrawingCacheEnabled(false);
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
                        getString(R.string.save_ok),
                        name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pfd != null)
                    pfd.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void initTips() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(requireContext());
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
