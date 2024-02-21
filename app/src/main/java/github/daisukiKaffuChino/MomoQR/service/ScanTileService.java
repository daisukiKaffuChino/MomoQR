package github.daisukiKaffuChino.MomoQR.service;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.TileService;

import github.daisukiKaffuChino.MomoQR.MomoApplication;
import github.daisukiKaffuChino.MomoQR.StartActivity;

public class ScanTileService extends TileService {
    @SuppressLint("StartActivityAndCollapseDeprecated")
    @Override
    public void onClick() {
        super.onClick();
        Context context= MomoApplication.context;
        Intent intent=new Intent();
        intent.setClass(context, StartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        intent.putExtra("startScan",true);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
            startActivityAndCollapse(pendingIntent);
        else
            startActivityAndCollapse(intent);
    }

}
