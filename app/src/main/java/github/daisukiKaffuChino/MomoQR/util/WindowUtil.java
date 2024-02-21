package github.daisukiKaffuChino.MomoQR.util;

import android.app.Activity;
import android.graphics.Color;
import android.view.Window;

import androidx.core.view.WindowCompat;

public class WindowUtil {

    public static void initWindow(Activity activity) {
        Window window = activity.getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
    }

}
