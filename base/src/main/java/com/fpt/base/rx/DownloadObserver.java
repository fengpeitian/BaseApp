package com.fpt.base.rx;

import android.os.Handler;
import android.os.Looper;

import com.fpt.base.OnDownloadListener;
import com.fpt.base.net.progress.ProgressResponseBody;

import java.io.File;

import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * <pre>
 *   @author  : tocci.feng
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2020/05/22 11:30
 *   desc    : 下载observer
 * </pre>
 */
public class DownloadObserver extends BaseObserver<ResponseBody> {
    private Handler mHandler;
    private OnDownloadListener mListener;
    private String filePath;

    /**
     * 构造
     * @param filePath   输出的文件本地路径
     * @param listener   监听
     */
    public DownloadObserver(String filePath, OnDownloadListener listener) {
        this.filePath = filePath;
        this.mListener = listener;
        this.mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void showDialog(boolean isShow) {}

    @Override
    protected void onSuccess(ResponseBody body) throws Exception {
        File outFile = new File(filePath);
        if (outFile.exists()) {
            if (body.contentLength() == outFile.length()){
                mHandler.post(() -> mListener.onSuccess(filePath));
                return;
            } else {
                outFile.delete();
            }
        }

        ProgressResponseBody newBody = new ProgressResponseBody(body
                , (bytes, contentLength, done) -> {
                    int progress = (int)(bytes*100/(float)contentLength);
                    mHandler.post(() -> mListener.onProgress(progress));
                }
        );

        BufferedSource source = newBody.source();
        BufferedSink sink = Okio.buffer(Okio.sink(outFile));
        source.readAll(sink);
        sink.flush();
        source.close();

        mHandler.post(() -> mListener.onSuccess(filePath));
    }

    @Override
    protected void onFailure(String eMsg) {
        mHandler.post(() -> mListener.onFailure(eMsg));
    }

}
