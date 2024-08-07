package github.daisukiKaffuChino.MomoQR.logic.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Build;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import github.daisukiKaffuChino.MomoQR.MomoApplication;
import github.daisukiKaffuChino.MomoQR.R;


public class ActionUtil {
    Context context;

    public ActionUtil(Context context) {
        this.context = context;
    }

    public static void copyContent(String text) {
        ClipboardManager clipboard = (ClipboardManager) MomoApplication.context.getSystemService(
                Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText(null, text));
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S) {
            toast(R.string.success);
        }
    }

    public static void toast(int id) {
        Toast.makeText(MomoApplication.context, id, Toast.LENGTH_SHORT).show();
    }

    public static boolean hasSpecialChat(String source) {
        if (!TextUtils.isEmpty(source)) {
            SpannableString ss = new SpannableString(source);
            Object[] spans = ss.getSpans(0, ss.length(), Object.class);
            if (spans != null) {
                if (source.equals("\"") || source.equals("\'")) {
                    return true;
                }
                String speChat = "[ `~!@#$%^&*()+=|{}';',\\[\\].<>?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？﹉✔✘～‖……...＝＄＊％＆·〔〕〖〗《》「」『』｛｝]";
                Pattern pattern = Pattern.compile(speChat);
                Matcher matcher = pattern.matcher(source);
                return matcher.find();
            }
        } else {
            return true;
        }
        return true;
    }

    public static String currentTime() {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedDate = currentDate.format(dateFormatter);
        String formattedTime = currentTime.format(timeFormatter);
        return formattedDate + "-" + formattedTime;
    }

    public String saveBitmapPrivate(Bitmap bitmap) {
        if (bitmap == null) {  //检查是否已生成二维码
            toast(R.string.qr_not_prepare_ok);
            return null;
        }
        File file = context.getDir("img", Context.MODE_PRIVATE);
        if (!file.exists()) {
            file.mkdirs();
        }
        String fileName = "QR" + System.currentTimeMillis() + ".png";
        String filePath = file + File.separator + fileName;
        try {
            FileOutputStream outStream = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
            return filePath;
        } catch (IOException e) {
            e.fillInStackTrace();
            return null;
        }
    }

    public static void detectIntentAndStart(String content) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(content));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PackageManager packageManager = MomoApplication.context.getPackageManager();
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, 0);
        if (!resolveInfoList.isEmpty())
            MomoApplication.context.startActivity(intent);
        else
            ActionUtil.toast(R.string.no_match_intent);
    }

    public void showMessageDialog(Activity activity, String title, String content) {
        decorBlur(activity.getWindow().getDecorView(), true);
        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton(R.string.ok, null)
                .setOnDismissListener(
                        dialog -> decorBlur(activity.getWindow().getDecorView(), false)
                )
                .show();
    }

    public void decorBlur(View decorView, boolean isBlur) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        if (sp.getBoolean("dialogBackgroundBlur", true) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (isBlur)
                decorView.setRenderEffect(RenderEffect.createBlurEffect(24, 24, Shader.TileMode.CLAMP));
            else
                decorView.setRenderEffect(null);
        }
    }

    public void addFav(String title, String content, String imageSavedPath, boolean checked) {
        FavSqliteHelper helper = new FavSqliteHelper(context);
        if (ActionUtil.hasSpecialChat(title)) {
            ActionUtil.toast(R.string.invalid_title);
        } else {
            if (imageSavedPath != null) {
                boolean insertOk = helper.insertData(title, content, imageSavedPath, checked, System.currentTimeMillis());
                if (insertOk)
                    ActionUtil.toast(R.string.add_fav_ok);
                else
                    ActionUtil.toast(R.string.add_fav_fail);
                helper.closeDB();
            } else {
                ActionUtil.toast(R.string.add_fav_fail);
                helper.closeDB();
            }
        }
    }
}
