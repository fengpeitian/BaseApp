package com.fpt.base.net.progress;

/**
 * <pre>
 *   @author  : tocci.feng
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2020/05/25 09:02
 *   desc    : 上传/下载进度监听
 * </pre>
 */
public interface IProgressListener {

    /**
     * 上传和下载的进度
     * @param bytes               已完成的字节
     * @param contentLength       总长
     * @param done                是否完成
     */
    void onProgress(long bytes, long contentLength, boolean done);

}
