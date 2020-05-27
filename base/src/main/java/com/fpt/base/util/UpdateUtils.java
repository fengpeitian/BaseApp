package com.fpt.base.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.UriUtils;
import com.fpt.base.CommonCallback;
import com.fpt.base.app.ContextProvider;

import java.io.File;
import java.util.List;

/**
 * <pre>
 *   @author  : tocci.feng
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2020/05/27 09:33
 *   desc    : 更新工具
 * </pre>
 */
public class UpdateUtils {

    /**
     * 检查本地是否有下载好的最新apk更新安装包
     * @param callback
     */
    public static void checkUpdateFromLocal(CommonCallback<File> callback) {
        List<File> files = FileUtils.listFilesInDirWithFilter(FolderUtils.getApkFolder()
                , file -> file.getName().endsWith(".apk")
                , (f1, f2) -> {
                    int v1 = getApkVersionCode(f1);
                    int v2 = getApkVersionCode(f2);
                    if (v1 > v2){
                        return -1;
                    }else if (v1 < v2){
                        return 1;
                    }else {
                        return 0;
                    }
                });
        if (files.size() > 0 && compareVersionCode(files.get(0))){
            callback.onSuccess(files.get(0));
        } else {
            callback.onFailure("there is no latest APK installation package.");
        }
    }

    /**
     * 安装apk
     * @param apk   apk文件
     */
    public static void installApk(File apk) {
        Context context = ContextProvider.get().getContext();
        //兼容8.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean installAllowed = context.getPackageManager().canRequestPackageInstalls();
            if (!installAllowed) {
                // 跳转到允许安装应用的页面
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + context.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }

        installApk(context, apk);
    }

    /**
     * 安装apk
     * @param context
     * @param apk
     */
    private static void installApk(Context context, File apk) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        Uri uri = UriUtils.file2Uri(apk);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 获取apk包的versionCode
     * @param apk
     * @return
     */
    private static int getApkVersionCode(File apk) {
        AppUtils.AppInfo info = AppUtils.getApkInfo(apk);
        return info.getVersionCode();
    }

    /**
     * 比较安装的apk与当前的版本
     * @param apk
     * @return
     */
    private static boolean compareVersionCode(File apk) {
        int version_apk = getApkVersionCode(apk);
        int version = AppUtils.getAppVersionCode();
        return version_apk > version;
    }

}
