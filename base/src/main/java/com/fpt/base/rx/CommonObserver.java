package com.fpt.base.rx;

import com.fpt.base.CommonCallback;

/**
 * <pre>
 *   @author  : tocci.feng
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2020/05/25 13:29
 *   desc    : 通用
 * </pre>
 */
public class CommonObserver<T> extends BaseObserver<T> {

    private CommonCallback<T> callback;

    public CommonObserver(CommonCallback<T> callback) {
        this.callback = callback;
    }

    @Override
    protected void showDialog(boolean isShow) {}

    @Override
    protected void onSuccess(T t) throws Exception {
        if (callback != null){
            callback.onSuccess(t);
        }
    }

    @Override
    protected void onFailure(String eMsg) {
        if (callback != null){
            callback.onFailure(eMsg);
        }
    }
}
