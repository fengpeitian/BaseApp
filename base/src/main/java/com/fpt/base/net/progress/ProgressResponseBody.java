package com.fpt.base.net.progress;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * <pre>
 *   @author  : tocci.feng
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2020/05/25 09:04
 *   desc    : 下载进度responsBody
 * </pre>
 */
public class ProgressResponseBody extends ResponseBody {
    /**
     * 实际的待包装响应体
     */
    private final ResponseBody responseBody;
    /**
     * 进度回调接口
     */
    private final IProgressListener listener;
    /**
     * 包装完成的BufferedSource
     */
    private BufferedSource bufferedSource;

    /**
     * 构造函数，赋值
     * @param responseBody 待包装的响应体
     * @param listener 回调接口
     */
    public ProgressResponseBody(ResponseBody responseBody, IProgressListener listener) {
        this.responseBody = responseBody;
        this.listener = listener;
    }


    /**
     * 重写调用实际的响应体的contentType
     * @return MediaType
     */
    @Override public MediaType contentType() {
        return responseBody.contentType();
    }

    /**
     * 重写调用实际的响应体的contentLength
     * @return contentLength
     * @throws IOException 异常
     */
    @Override public long contentLength() {
        return responseBody.contentLength();
    }

    /**
     * 重写进行包装source
     * @return BufferedSource
     * @throws IOException 异常
     */
    @Override public BufferedSource source() {
        if (bufferedSource == null) {
            //包装
            Source source = source(responseBody.source());
            bufferedSource = Okio.buffer(source);
        }
        return bufferedSource;
    }

    @Override
    public void close() {
        if (bufferedSource != null) {
            try {
                bufferedSource.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取，回调进度接口
     * @param source Source
     * @return Source
     */
    private Source source(Source source) {

        return new ForwardingSource(source) {
            //当前读取字节数
            long totalBytesRead = 0L;
            @Override public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                //增加当前读取的字节数，如果读取完成了bytesRead会返回-1
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                //回调，如果contentLength()不知道长度，会返回-1
                listener.onProgress(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                return bytesRead;
            }
        };
    }
}
