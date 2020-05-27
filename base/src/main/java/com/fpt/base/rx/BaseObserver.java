package com.fpt.base.rx;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * <pre>
 *   @author  : tocci.feng
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2020/05/22 10:17
 *   desc    : BaseObserver
 * </pre>
 */
public abstract class BaseObserver<T> implements Observer<T> {

    private Disposable disposable;

    @Override
    public void onSubscribe(Disposable d) {
        disposable = d;
        showDialog(true);
    }

    @Override
    public void onNext(T t) {
        showDialog(false);

        try {
            onSuccess(t);
        }catch (Exception e){
            onFailure(e.getMessage());
        }
    }

    @Override
    public void onError(Throwable e) {
        showDialog(false);

        onFailure(e.getMessage());

        dispose();
    }

    @Override
    public void onComplete() {
        dispose();
    }

    private void dispose() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    /**
     * 是否显示loading弹框
     * @param isShow
     */
    protected abstract void showDialog(boolean isShow);

    /**
     * 成功
     * @param t
     * @throws Exception
     */
    protected abstract void onSuccess(T t) throws Exception;

    /**
     * 失败
     * @param eMsg
     */
    protected abstract void onFailure(String eMsg);

}