package github.daisukiKaffuChino.MomoQR.ui.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ResultViewModel extends ViewModel {
    public MutableLiveData<String> contentLiveData;

    public ResultViewModel() {
        contentLiveData = new MutableLiveData<>();
    }
}
