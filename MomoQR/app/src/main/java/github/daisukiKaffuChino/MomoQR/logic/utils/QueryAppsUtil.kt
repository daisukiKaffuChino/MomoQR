package github.daisukiKaffuChino.MomoQR.logic.utils

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import github.daisukiKaffuChino.MomoQR.logic.bean.AppInfoBean

class QueryAppsUtil {

    fun getAppList(packageManager: PackageManager): ArrayList<AppInfoBean> {
        val appBeanList = ArrayList<AppInfoBean>();
        val queryIntentActivities: MutableList<ResolveInfo>
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)

        queryIntentActivities =
            packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)

        queryIntentActivities.forEach {
            val bean = AppInfoBean()
            bean.appName = it.loadLabel(packageManager).toString()
            bean.appPackageName = it.activityInfo.applicationInfo.packageName
            bean.icon = it.loadIcon(packageManager)
            appBeanList.add(bean)
        }
        return appBeanList;
    }

}