package com.fpt.base.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fpt.base.receiver.NetConnectReceiver;
import com.fpt.base.util.AndroidUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Locale;

/**
 * <pre>
 *   @author : tocci.feng
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2020/05/14 11:08
 *   desc    : BaseActivity
 * </pre>
 */
public abstract class BaseActivity extends AppCompatActivity implements IView, NetConnectReceiver.OnNetworkStateChangeListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layoutResId = attachLayoutRes(savedInstanceState);
        //设置布局
        setContentView(layoutResId);
        findViewsById();
        initViews();
        if (useEventBus()){
            eventBus(true);
        }

        //注册网络状态变化监听,android6.0系统bug,导致权限不足
        if(Build.VERSION.SDK_INT != Build.VERSION_CODES.M) {
            new NetConnectReceiver(this, this).register();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
        }finally {
            eventBus(false);
        }
    }

    @Override
    public void onNetworkStateChange(boolean connect) {}

    /**
     * 注册/解注册eventBus
     * @param flag
     */
    private void eventBus(boolean flag) {
        boolean isRegistered = EventBus.getDefault().isRegistered(this);
        if (flag) {
            if (!isRegistered) {
                EventBus.getDefault().register(this);
            }
        }else {
            if (isRegistered){
                EventBus.getDefault().unregister(this);
            }
        }
    }

    /**
     * 杀掉进程并退出app
     * 注意：不能先杀掉主进程，否则逻辑代码无法继续执行，需先杀掉相关进程最后杀掉主进程
     */
    protected void killProcessAndExit() {
        ActivityManager mActivityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> mList = mActivityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : mList) {
            //先杀掉除主进程外的进程
            if (runningAppProcessInfo.pid != android.os.Process.myPid()) {
                android.os.Process.killProcess(runningAppProcessInfo.pid);
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    /**
     * 设置语言
     * @param locale
     */
    protected void setLanguage(Locale locale) {
        // 设置语言
        AndroidUtils.setLocale(this,locale);
        // 重启activity
        recreate();
    }

}
