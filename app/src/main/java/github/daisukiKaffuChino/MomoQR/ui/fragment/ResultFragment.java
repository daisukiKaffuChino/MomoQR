package github.daisukiKaffuChino.MomoQR.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.viewbinding.ViewBinding;

import com.bumptech.glide.Glide;

import java.util.Objects;

import github.daisukiKaffuChino.MomoQR.R;
import github.daisukiKaffuChino.MomoQR.databinding.FragmentHomeBinding;
import github.daisukiKaffuChino.MomoQR.databinding.FragmentResultBinding;
import github.daisukiKaffuChino.MomoQR.logic.utils.FavSqliteHelper;
import github.daisukiKaffuChino.MomoQR.logic.utils.MyUtil;
import github.daisukiKaffuChino.MomoQR.logic.utils.QRCodeUtil;
import github.daisukiKaffuChino.MomoQR.ui.model.HomeViewModel;
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

        binding.copyBtn.setOnClickListener(v ->
                MyUtil.copyContent(Objects.requireNonNull(binding.resultText.getText()).toString()));
        //binding.addFavBtn.setOnClickListener(v -> showEditTextDialog(EDITTEXT_DIALOG_FAV_TITLE));
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
        viewModel= new ViewModelProvider(this).get(ResultViewModel.class);
        if (getArguments() != null){
            String content=getArguments().getString("content");
            viewModel.contentLiveData.setValue(content);
        }
    }

    private void showScanResults(String content) {
/*
        if (isSet) {
            viewModel.contentLiveData.setValue(content);
        }
        viewModel.isScanned = true;
        */
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

}
