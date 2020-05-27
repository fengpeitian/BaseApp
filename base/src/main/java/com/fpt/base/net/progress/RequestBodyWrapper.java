package com.fpt.base.net.progress;

import android.os.Handler;
import android.os.Looper;

import com.fpt.base.OnProgressListener;

import okhttp3.RequestBody;

/**
 * <pre>
 *   @author  : tocci.feng
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2020/05/25 09:25
 *   desc    : request上传进度包装
 * </pre>
 */
public class RequestBodyWrapper implements IProgressListener {

    /**
     * 进度监听
     */
    private OnProgressListener listener;

    private Handler mHandler;

    public RequestBodyWrapper(OnProgressListener listener) {
        this.listener = listener;
        this.mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onProgress(long bytes, long contentLength, boolean done) {
        int progress = (int)(bytes*100/(float)contentLength);

        mHandler.post(() -> listener.onProgress(progress));
    }

    /**
     * 包装请求体用于上传文件的回调
     * @param requestBody 请求体RequestBody
     * @return 包装后的进度回调请求体
     */
    public RequestBody getRequestBody(RequestBody requestBody){
        //包装请求体
        return new ProgressRequestBody(requestBody,this);
    }

}
