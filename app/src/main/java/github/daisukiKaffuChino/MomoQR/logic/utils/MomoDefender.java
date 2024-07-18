package github.daisukiKaffuChino.MomoQR.logic.utils;
/*
 * LuaAppDefender.java
 * reOpenLua-Open-Source
 * https://github.com/daisukiKaffuChino/reOpenLua-Open-Source
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.util.DisplayMetrics;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Objects;

public class MomoDefender {
    @SuppressLint("StaticFieldLeak")
    protected Context context;

    public MomoDefender(Context c) {
        context = c;
    }

    private String prepare(Context c) throws PackageManager.NameNotFoundException {
        String PATH_PackageParser = "android.content.pm.PackageParser";
        String apkPath = c.getPackageManager().getApplicationInfo(Objects.requireNonNull(getName()), 0).sourceDir;
        try {
            @SuppressLint("PrivateApi") Class<?> pkgParserCls = Class.forName(PATH_PackageParser);
            Class[] typeArgs = new Class[1];
            typeArgs[0] = String.class;
            DisplayMetrics metrics = new DisplayMetrics();
            metrics.setToDefaults();
            Constructor pkgParserCt;
            Object pkgParser;
            {
                pkgParserCt = pkgParserCls.getConstructor();
                pkgParser = pkgParserCt.newInstance();
                @SuppressLint("DiscouragedPrivateApi") Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod("parsePackage", File.class, int.class);
                Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser, new File(apkPath), PackageManager.GET_SIGNATURES);
                if (Build.VERSION.SDK_INT >= 28) {
                    assert pkgParserPkg != null;
                    Method pkgParser_collectCertificatesMtd = pkgParserCls.getDeclaredMethod("collectCertificates", pkgParserPkg.getClass(), Boolean.TYPE);
                    pkgParser_collectCertificatesMtd.invoke(pkgParser, pkgParserPkg, false);
                    Field mSigningDetailsField = pkgParserPkg.getClass().getDeclaredField("mSigningDetails"); // SigningDetails
                    mSigningDetailsField.setAccessible(true);
                    Object mSigningDetails = mSigningDetailsField.get(pkgParserPkg);
                    assert mSigningDetails != null;
                    Field infoField = mSigningDetails.getClass().getDeclaredField("signatures");
                    infoField.setAccessible(true);
                    Signature[] info = (Signature[]) infoField.get(mSigningDetails);
                    assert info != null;
                    return info[0].toCharsString();
                } else {
                    assert pkgParserPkg != null;
                    Method pkgParser_collectCertificatesMtd = pkgParserCls.getDeclaredMethod("collectCertificates", pkgParserPkg.getClass(), Integer.TYPE);
                    pkgParser_collectCertificatesMtd.invoke(pkgParser, pkgParserPkg, PackageManager.GET_SIGNATURES);
                    Field packageInfoFld = pkgParserPkg.getClass().getDeclaredField("mSignatures");
                    Signature[] info = (Signature[]) packageInfoFld.get(pkgParserPkg);
                    assert info != null;
                    return info[0].toCharsString();
                }
            }
        } catch (Exception ignore) {
        }
        return "";
    }

    private String getName() {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private static String apply(String text) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(text.getBytes(StandardCharsets.UTF_8));

            StringBuilder builder = new StringBuilder();

            for (byte aByte : bytes) {
                builder.append(Integer.toHexString((0x000000FF & aByte) | 0xFFFFFF00).substring(6));
            }
            return builder.toString();
        } catch (Exception e) {
            return "";
        }

    }

    public boolean get() {
        try {
            String[] hiddenStringParts = {
                    "2ab", "ab9", "98c", "44c",
                    "2a6", "aae", "668", "4a5",
                    "f2d", "c40", "bc"
            };
            StringBuilder originalStringBuilder = new StringBuilder();
            for (String part : hiddenStringParts) {
                originalStringBuilder.append(part);
            }
            return !apply(prepare(context)).equals(originalStringBuilder.toString());
        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }
    }

}
