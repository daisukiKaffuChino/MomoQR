package github.daisukiKaffuChino.MomoQR.ui.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CaptureViewModel extends ViewModel {
    public MutableLiveData<Boolean> flashLiveData;

    public CaptureViewModel() {
        flashLiveData = new MutableLiveData<>();
    }
}
