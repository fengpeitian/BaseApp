package com.fpt.base.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.LogUtils;
import com.fpt.base.BuildConfig;
import com.fpt.base.net.NetConfig;
import com.fpt.base.net.RetrofitManager;
import com.fpt.base.net.interceptor.LoggingInterceptor;
import com.fpt.base.util.FolderUtils;

import okhttp3.Interceptor;

/**
 * <pre>
 *   @author  : tocci.feng
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2020/05/14 15:11
 *   desc    : 全局application
 * </pre>
 */
public class GlobalApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 防止多进程重复初始化
        if (isMainProcess()) {

            initGlobalConfig();

            initModuleConfig();

        }
    }

    /**
     * 是否为主进程
     * @return
     */
    private boolean isMainProcess() {
        String processName = AppUtils.getAppPackageName();
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                processName = appProcess.processName;
                break;
            }
        }
        String packageName = this.getPackageName();
        if (processName.equals(packageName)) {
            return true;
        }
        return false;
    }

    /**
     * 是否是调试模式
     * @return
     */
    private boolean isDebug() {
        return true;
    }

    /**
     * 是否是debug版
     * @return
     */
    private boolean isBuildDebug() {
        return BuildConfig.DEBUG;
    }

    /**
     * 初始化全局配置
     */
    private void initGlobalConfig() {
        // ARouter
        ARouter.openDebug();
        if (isBuildDebug()) {
            ARouter.openLog();
        }
        ARouter.init(this);
        // retrofit
        RetrofitManager.config(new NetConfiguration());
        // 文件夹
        FolderUtils.init();
        // crash
        CrashUtils.init(FolderUtils.getCrashFolder());
        // log开关
        LogUtils.Config config = LogUtils.getConfig();
        config.setLogSwitch(isBuildDebug());
        config.setDir(FolderUtils.getLogFolder());
    }

    /**
     * 初始化其他组件的application配置
     */
    private void initModuleConfig() {
        for (String module : IApplication.MODULES){
            try {
                Class clz = Class.forName(module);
                Object obj = clz.newInstance();
                if (obj instanceof IApplication){
                    IApplication app = ((IApplication) obj);
                    app.onCreate(this);
                    app.isDebug(isDebug());
                    app.isBuildDebug(isBuildDebug());
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 日志打印
     */
    private Interceptor mLoggingInterceptor = new LoggingInterceptor() {

        @Override
        protected void showResponseMsg(String msg) {
            LogUtils.d(msg);
        }

        @Override
        protected void showErrorMsg(String eMsg) {
            LogUtils.e(eMsg);
        }

    };

    /**
     * 网络配置
     */
    private class NetConfiguration implements NetConfig {

        private String baseUrl;

        public NetConfiguration() {
            this.baseUrl = isDebug() ? "http://patient.coreedoctor.com/":"";
        }

        @Override
        public String baseUrl() {
            return baseUrl;
        }

        @Override
        public long connectTimeoutMills() {
            return 0;
        }

        @Override
        public long readTimeoutMills() {
            return 0;
        }

        @Override
        public Interceptor[] interceptors() {
            return null;
        }

        @Override
        public Interceptor logInterceptor() {
            return mLoggingInterceptor;
        }

    }

}
