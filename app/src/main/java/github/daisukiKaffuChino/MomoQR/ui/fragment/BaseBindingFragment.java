package github.daisukiKaffuChino.MomoQR.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewbinding.ViewBinding;

import java.util.Objects;

import github.daisukiKaffuChino.MomoQR.logic.bean.ResultArgs;

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

    //传参用 20240303新增
    protected <X> LiveData<Pair<Integer, X>> startFragmentForResult(@IdRes int destination, int requestCode, Bundle bundle) {
        ResultArgs args = new ResultArgs(Objects.requireNonNull(getNavController().getCurrentDestination()).getId(), requestCode).setBusinessArgs(bundle);
        LiveData<Pair<Integer, X>> liveData = Objects.requireNonNull(getNavController().getCurrentBackStackEntry()).getSavedStateHandle().getLiveData(String.valueOf(requestCode));
        getNavController().navigate(destination, args.toBundle());
        return liveData;
    }

    protected NavController getNavController() {
        return Navigation.findNavController(requireView());
    }
}