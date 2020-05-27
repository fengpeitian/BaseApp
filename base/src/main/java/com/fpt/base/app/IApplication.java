package com.fpt.base.app;

import android.app.Application;

/**
 * <pre>
 *   @author  : tocci.feng
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2020/05/14 17:16
 *   desc    : Application接口
 * </pre>
 */
public interface IApplication {

    /**
     * 各组件application的包名地址
     */
    String[] MODULES = {
            "com.fpt.base.MyApplication"
    };

    /**
     * 创建
     * @param application
     */
    void onCreate(Application application);

    /**
     * 是否是调试模式
     * @param debug
     */
    void isDebug(boolean debug);

    /**
     * 是否是debug版
     * @param debug
     */
    void isBuildDebug(boolean debug);

}
