package com.fpt.base.util;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.PermissionUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

/**
 * <pre>
 *   @author  : tocci.feng
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2020/05/15 09:16
 *   desc    : 工具
 * </pre>
 */
public class AndroidUtils {

    /**
     * 检查/申请 权限
     * @param callback
     * @param permissions
     */
    public static void checkPermission(PermissionUtils.FullCallback callback, @PermissionConstants.Permission String... permissions) {
        boolean isGranted = PermissionUtils.isGranted(permissions);
        if (!isGranted) {
            PermissionUtils.permission(permissions).callback(callback).request();
        } else {
            callback.onGranted(Arrays.asList(permissions));
        }
    }

    /**
     * 压缩图片（质量压缩）
     * @param bitmap
     * @param dstPath   .jpg结尾
     */
    public static File compressImage(Bitmap bitmap, String dstPath) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        //循环判断如果压缩后图片是否大于500kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > 500) {
            //重置baos即清空baos
            baos.reset();
            //每次都减少10
            options -= 10;
            //这里压缩options%，把压缩后的数据存放到baos中
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            long length = baos.toByteArray().length;
        }
        File file = new File(dstPath);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            try {
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();

                return file;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置多语言
     * @param activity
     * @param loc
     */
    public static void setLocale(Activity activity, Locale loc) {
        // 获得res资源对象,android 8.0以上必须用activity获取Resources对象
        Resources resources = activity.getResources();
        // 获得屏幕参数：主要是分辨率，像素等。
        DisplayMetrics metrics = resources.getDisplayMetrics();
        // 获得配置对象
        Configuration config = resources.getConfiguration();
        //区别sdk 17版本,设置字体
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            LocaleList localeList = new LocaleList(loc);
            LocaleList.setDefault(localeList);
            config.setLocales(localeList);
            activity.getApplication().createConfigurationContext(config);
            Locale.setDefault(loc);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(loc);
        } else {
            config.locale = loc;
        }
        resources.updateConfiguration(config, metrics);
    }

    /**
     * 获取系统的语言环境
     * @return
     */
    public static Locale getSystemLocale() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                ? Locale.getDefault(Locale.Category.DISPLAY)
                :Locale.getDefault();
    }


}
