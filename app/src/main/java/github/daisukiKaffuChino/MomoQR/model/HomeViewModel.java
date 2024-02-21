package github.daisukiKaffuChino.MomoQR.model;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {
    public MutableLiveData<String> contentLiveData;
    public boolean isScanned = false;
    public HomeViewModel() {
        contentLiveData = new MutableLiveData<>();
    }
}
