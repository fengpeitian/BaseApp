package com.fpt.base;

/**
 * <pre>
 *   @author  : tocci.feng
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2020/05/22 09:41
 *   desc    : 监听
 * </pre>
 */
public interface CommonCallback<T> {

    /**
     * 成功
     * @param t
     */
    void onSuccess(T t);

    /**
     * 失败
     * @param eMsg
     */
    void onFailure(String eMsg);

}
