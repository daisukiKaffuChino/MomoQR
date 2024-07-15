package github.daisukiKaffuChino.MomoQR.ui.view;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import github.daisukiKaffuChino.MomoQR.R;

public class MillenniumPreference extends Preference {
    Context context;

    public MillenniumPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setLayoutResource(R.layout.preference_millennium);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        ImageView imageView = (ImageView) holder.findViewById(R.id.preference_gdd_logo);
        int currentNightMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES)
            imageView.setImageResource(R.drawable.gdd_white);
    }
}
