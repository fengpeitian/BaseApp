package com.fpt.base.util;

import androidx.lifecycle.LifecycleOwner;

import com.fpt.base.rx.BaseObserver;
import com.fpt.base.CommonCallback;
import com.fpt.base.rx.CommonObserver;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * <pre>
 *   @author  : tocci.feng
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2020/05/22 09:23
 *   desc    : rx工具
 * </pre>
 */
public class RxUtils {

    /**
     * 延时delay毫秒后执行
     * @param delay
     * @param callback
     * @param owner
     */
    public static void delay(long delay, CommonCallback<Long> callback, LifecycleOwner owner) {
        CommonObserver<Long> observer = new CommonObserver<>(callback);
        Observable.timer(delay, TimeUnit.MILLISECONDS)
                .compose(RxUtils.transform())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(owner)))
                .subscribe(observer);
    }

    /**
     * 每隔interval毫秒后执行
     * @param interval
     * @param callback
     * @param owner
     * @return
     */
    public static void interval(long interval, CommonCallback<Long> callback, LifecycleOwner owner) {
        CommonObserver<Long> observer = new CommonObserver<>(callback);
        Observable.interval(interval, TimeUnit.MILLISECONDS)
                .compose(RxUtils.transform())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(owner)))
                .subscribe(observer);
    }

    /**
     * 线程切换
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T, T> transform() {
        return observable -> observable
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    /**
     * rxJava调用
     * @param observable
     * @param observer
     * @param owner
     * @param <T>
     */
    public static <T> void apply(Observable<T> observable, BaseObserver<T> observer, LifecycleOwner owner){
        observable.compose(RxUtils.transform())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(owner)))
                .subscribe(observer);
    }


}
