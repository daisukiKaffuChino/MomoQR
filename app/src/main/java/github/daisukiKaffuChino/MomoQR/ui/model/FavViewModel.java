package github.daisukiKaffuChino.MomoQR.ui.model;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import github.daisukiKaffuChino.MomoQR.logic.bean.FavBean;

public class FavViewModel extends ViewModel {

    public ArrayList<FavBean> ls;
    public FavViewModel(ArrayList<FavBean> favBeans) {
        ls = favBeans;
    }
    public void deleteAll() {
        ls.clear();
    }
}
