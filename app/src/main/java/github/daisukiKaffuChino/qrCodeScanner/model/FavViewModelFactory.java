package github.daisukiKaffuChino.qrCodeScanner.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import github.daisukiKaffuChino.qrCodeScanner.bean.FavBean;

public class FavViewModelFactory implements ViewModelProvider.Factory {
    ArrayList<FavBean> beans;

    public FavViewModelFactory(ArrayList<FavBean> beans){
        this.beans=beans;
    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new FavViewModel(beans);
    }
}
