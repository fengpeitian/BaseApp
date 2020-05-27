package com.fpt.base.ui;

import android.os.Bundle;

/**
 * <pre>
 *   @author  : tocci.feng
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2020/05/14 11:03
 *   desc    : activity/fragment
 * </pre>
 */
public interface IView {

    /**
     * 指定布局id
     * @param savedInstanceState
     * @return
     */
    int attachLayoutRes(Bundle savedInstanceState);

    /**
     * findViewById
     */
    void findViewsById();

    /**
     * 初始化视图控件
     */
    void initViews();

    /**
     * 是否使用eventBus
     * @return
     */
    boolean useEventBus();

}
