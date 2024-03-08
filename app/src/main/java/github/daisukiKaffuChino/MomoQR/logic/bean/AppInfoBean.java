package github.daisukiKaffuChino.MomoQR.logic.bean;

import android.graphics.drawable.Drawable;

public class AppInfoBean {
    Drawable icon;
    String appName;
    String appPackageName;

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    public String getAppPackageName() {
        return appPackageName;
    }
}
