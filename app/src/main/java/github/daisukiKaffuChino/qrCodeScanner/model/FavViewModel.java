package github.daisukiKaffuChino.qrCodeScanner.model;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import github.daisukiKaffuChino.qrCodeScanner.bean.FavBean;

public class FavViewModel extends ViewModel {

    public ArrayList<FavBean> ls;
    //private FavBean bean = new FavBean();

    public FavViewModel(ArrayList<FavBean> favBeans) {
        ls = favBeans;
    }

    public void deleteAll() {
        ls.clear();
    }
}
