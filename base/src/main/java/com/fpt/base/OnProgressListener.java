package com.fpt.base;

/**
 * <pre>
 *   @author  : tocci.feng
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2020/05/15 13:33
 *   desc    : 进度监听
 * </pre>
 */
public interface OnProgressListener {

    /**
     * 进度
     * @param progress 进度区间[0,100]
     */
    void onProgress(int progress);

}
