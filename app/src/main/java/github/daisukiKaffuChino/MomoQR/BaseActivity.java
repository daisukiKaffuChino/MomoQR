package github.daisukiKaffuChino.MomoQR;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.color.DynamicColors;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initTheme();
        initWindow();
        super.onCreate(savedInstanceState);
    }

    private void initTheme(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean enableMaterial3=sp.getBoolean("enableMaterial3", false);
        if (enableMaterial3)
            setTheme(R.style.Theme_MomoQR_Material3);
        else
            setTheme(R.style.Theme_MomoQR_Material2);

        if (enableMaterial3 && sp.getBoolean("dynamicColor", false))
            DynamicColors.applyToActivityIfAvailable(this);
    }

    private void initWindow() {
        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
    }

}
