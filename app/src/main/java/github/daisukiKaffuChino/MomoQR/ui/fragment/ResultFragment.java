package github.daisukiKaffuChino.MomoQR.ui.fragment;

import android.annotation.SuppressLint;
import android.app.StatusBarManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;

import github.daisukiKaffuChino.MomoQR.R;
import github.daisukiKaffuChino.MomoQR.databinding.FragmentResultBinding;
import github.daisukiKaffuChino.MomoQR.logic.utils.FavSqliteHelper;
import github.daisukiKaffuChino.MomoQR.logic.utils.MyUtil;
import github.daisukiKaffuChino.MomoQR.logic.utils.QRCodeUtil;
import github.daisukiKaffuChino.MomoQR.ui.model.ResultViewModel;

public class ResultFragment extends BaseBindingFragment<FragmentResultBinding> {
    FragmentResultBinding binding;
    ResultViewModel viewModel;
    FavSqliteHelper helper;

    @Override
    protected FragmentResultBinding onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent) {
        return FragmentResultBinding.inflate(inflater, parent, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = getBinding();

        initTips();

        binding.copyBtn.setOnClickListener(v ->
                MyUtil.copyContent(Objects.requireNonNull(binding.resultText.getText()).toString()));
        binding.addFavBtn.setOnClickListener(v -> showEditTextDialog());
        binding.openLinkBtn.setOnClickListener(v ->
                MyUtil.detectIntentAndStart(viewModel.contentLiveData.getValue()));
        binding.remakeCodeImg.setOnLongClickListener(v -> {
            v.setDrawingCacheEnabled(true);
            QRCodeUtil.INSTANCE.saveBitmap(requireContext(), v.getDrawingCache());
            v.setDrawingCacheEnabled(false);
            return true;
        });

        viewModel.contentLiveData.observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                showScanResults(result);
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new FavSqliteHelper(requireContext());
        viewModel = new ViewModelProvider(this).get(ResultViewModel.class);
        if (getArguments() != null) {
            String content = getArguments().getString("content");
            viewModel.contentLiveData.setValue(content);
        }
    }

    private void showScanResults(String content) {
        if (content != null) {
            binding.resultText.setText(content);
            Bitmap bitmap = QRCodeUtil.INSTANCE.createQRCodeBitmap(content, 180, 180, Color.BLACK, Color.WHITE);
            Glide.with(requireContext()).load(bitmap).into(binding.remakeCodeImg);
        }
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

    private void showEditTextDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        builder.setTitle(R.string.add_fav);
        builder.setView(R.layout.dialog_edittext);
        builder.setNegativeButton(R.string.cancel, null);

        builder.setPositiveButton(R.string.ok, (dialogInterface, i) -> {
            EditText edt = ((AlertDialog) dialogInterface).findViewById(R.id.dialog_edt);
            if (edt != null)
                addFav(edt.getText().toString(), viewModel.contentLiveData.getValue());
        });
        builder.setNeutralButton(R.string.use_current_date, (dialogInterface, i) ->
                addFav(MyUtil.currentTime(), viewModel.contentLiveData.getValue()));

        builder.show();
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
