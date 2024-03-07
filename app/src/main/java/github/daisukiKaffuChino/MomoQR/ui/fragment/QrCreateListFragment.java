package github.daisukiKaffuChino.MomoQR.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import github.daisukiKaffuChino.MomoQR.databinding.FragmentQrCreateListBinding;

public class QrCreateListFragment extends BaseBindingFragment<FragmentQrCreateListBinding> {
    @Override
    protected FragmentQrCreateListBinding onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent) {
        return FragmentQrCreateListBinding.inflate(inflater, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentQrCreateListBinding binding=getBinding();

    }
}
