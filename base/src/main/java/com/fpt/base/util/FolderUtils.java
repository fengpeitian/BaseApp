package com.fpt.base.util;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.PathUtils;

import java.io.File;

/**
 * <pre>
 *   @author  : tocci.feng
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2020/05/27 09:11
 *   desc    : 应用目录工具
 * </pre>
 */
public class FolderUtils {

    /**
     * 初始化,创建文件夹
     */
    public static void init() {
        // 包名
        FileUtils.createOrExistsDir(getPackageFolder());
        // 包名/apk
        FileUtils.createOrExistsDir(getApkFolder());
        // 包名/crash
        FileUtils.createOrExistsDir(getCrashFolder());
        // 包名/log
        FileUtils.createOrExistsDir(getLogFolder());
        // 包名/cache
        FileUtils.createOrExistsDir(getCacheFolder());
    }

    private static String getPackageFolder() {
        StringBuilder dirBuilder = new StringBuilder();
        dirBuilder.append(PathUtils.getExternalStoragePath());
        dirBuilder.append(File.separator);
        dirBuilder.append(AppUtils.getAppPackageName());
        return dirBuilder.toString();
    }

    /**
     * 应用缓存目录
     * @return
     */
    public static String getCacheFolder() {
        return getPackageFolder()+File.separator+"cache";
    }

    /**
     * 应用apk目录
     * @return
     */
    public static String getApkFolder() {
        return getPackageFolder()+File.separator+"apk";
    }

    /**
     * 应用crash目录
     * @return
     */
    public static String getCrashFolder() {
        return getPackageFolder()+File.separator+"crash";
    }

    /**
     * 应用log目录
     * @return
     */
    public static String getLogFolder() {
        return getPackageFolder()+File.separator+"log";
    }

}
