package github.daisukiKaffuChino.MomoQR.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

public abstract class BaseBindingFragment<T extends ViewBinding> extends Fragment {

    private T binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 调用onCreateViewBinding方法获取binding
        binding = onCreateViewBinding(inflater, container);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 引用置空处理
        binding = null;
    }

    // 子类使用该方法来使用binding
    public T getBinding() {
        return binding;
    }

    // 由子类去重写
    protected abstract T onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent);
}