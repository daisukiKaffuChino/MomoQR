package github.daisukiKaffuChino.MomoQR.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.Preference;

import github.daisukiKaffuChino.MomoQR.R;

public class MillenniumPreference extends Preference {
    public MillenniumPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.preference_millennium);
    }
}
