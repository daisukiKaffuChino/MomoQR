package github.daisukiKaffuChino.MomoQR.ui.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ResultViewModel extends ViewModel {
    public MutableLiveData<String> contentLiveData;
    public MutableLiveData<String> pathLiveData;
    public boolean isFromFav;
    public MutableLiveData<Integer> qrForegroundColor;
    public MutableLiveData<Integer> qrBackgroundColor;

    public ResultViewModel() {
        contentLiveData = new MutableLiveData<>();
        pathLiveData = new MutableLiveData<>();
        qrForegroundColor = new MutableLiveData<>();
        qrBackgroundColor = new MutableLiveData<>();
    }
}
