-keep class **.R$* { *; }
-keep class com.jph.android.entity.** { *; } #实体类不参与混淆
-keep class com.jph.android.view.** { *; } #自定义控件不参与混淆
-keep class github.daisukiKaffuChino.MomoQR.logic.bean.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

-keep class com.google.zxing.** {*;}
-dontwarn com.google.zxing.**
