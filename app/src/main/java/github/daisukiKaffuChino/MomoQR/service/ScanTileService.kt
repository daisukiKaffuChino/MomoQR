package github.daisukiKaffuChino.MomoQR.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.TileService
import github.daisukiKaffuChino.MomoQR.MomoApplication
import github.daisukiKaffuChino.MomoQR.StartActivity

class ScanTileService : TileService() {
    @SuppressLint("StartActivityAndCollapseDeprecated")
    override fun onClick() {
        super.onClick()
        val context = MomoApplication.context
        val intent = Intent()
        intent.setClass(context, StartActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("startScan", true)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
            startActivityAndCollapse(pendingIntent)
        else startActivityAndCollapse(intent)
    }
}
