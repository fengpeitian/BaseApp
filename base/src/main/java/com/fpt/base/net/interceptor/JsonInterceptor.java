package com.fpt.base.net.interceptor;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * <pre>
 *   @author  : lucien.feng
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2018/08/13 09:51
 *   desc    : json打印log拦截器
 * </pre>
 */
public abstract class JsonInterceptor implements Interceptor {
    /**
     * 默认编码 - UTF-8
     */
    private final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(Chain chain) throws IOException {
        // 获得请求体
        Request request = chain.request();
        try {
            // 请求体建造者
            Request.Builder requestBuilder = request.newBuilder();
            // 从新构建请求体
            request = requestBuilder.build();
            // 请求地址
            String url = request.url().toString();
            // 请求方法
            String method = request.method();
            // 记录请求时间
            long t1 = System.nanoTime();

            if (method.equals("GET")){
                // 打印请求体
                outputRequestMsg(url, method, request.headers(),"");
            }else {
                // 请求体
                RequestBody requestBody = request.body();
                if (requestBody != null) {
                    StringBuilder sb = new StringBuilder();
                    Buffer buffer = new Buffer();
                    requestBody.writeTo(buffer);
                    Charset charset = UTF8;
                    MediaType contentType = requestBody.contentType();
                    if (contentType != null) {
                        charset = contentType.charset(charset);
                    }
                    if (isPlaintext(buffer)) {
                        assert charset != null;
                        sb.append(buffer.readString(charset));
                        assert contentType != null;
                        sb.append(" (Content-Type = ").append(contentType.toString()).append(",")
                                .append(requestBody.contentLength()).append("-byte body)");
                    } else {
                        assert contentType != null;
                        sb.append(" (Content-Type = ").append(contentType.toString())
                                .append(",binary ").append(requestBody.contentLength())
                                .append("-byte body omitted)");
                    }

                    // 打印请求体
                    outputRequestMsg(url, method, request.headers(), sb.toString());
                }
            }

            // 构建返回体
            Response response = chain.proceed(request);
            // 返回体
            ResponseBody body = response.body();
            MediaType contentType = body.contentType();
            //如果是json返回格式就显示
            if (contentType != null
                    && contentType.toString().contains("application/json")) {

                // 记录请求耗时
                long t2 = System.nanoTime();
                assert body != null;
                BufferedSource source = body.source();
                // 读取整个返回体
                source.request(Long.MAX_VALUE);
                Buffer buffer = source.buffer();
                Charset charset = Charset.defaultCharset();
                if (contentType != null) {
                    charset = contentType.charset(charset);
                }
                assert charset != null;
                String bodyString = buffer.clone().readString(charset);

                // 打印JSON消息
                outputResponseMsg(response.request().url().toString(), (t2 - t1) / 1e6d,
                        response.headers(), bodyString);

                return response;
            }else {
                return response;
            }
        } catch (Throwable throwable) {
            outputErrorMsg(request.url().toString(),throwable);
        }
        return chain.proceed(request);
    }

    /**
     * 是否为文本数据
     *
     * @param buffer 数据源
     * @return 是否
     */
    private boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false;
        }
    }

    /**
     * 请求体
     *
     * @param url     请求网址
     * @param method  请求方法
     * @param headers 请求头
     * @param params  请求参数
     */
    protected abstract void outputRequestMsg(String url, String method, Headers headers, String params);

    /**
     * 返回体
     *
     * @param url     网址
     * @param time    耗时
     * @param headers 头信息
     * @param receive 返回消息体
     */
    protected abstract void outputResponseMsg(String url, double time, Headers headers, String receive);

    /**
     * 错误
     * @param url
     * @param throwable
     */
    protected abstract void outputErrorMsg(String url, Throwable throwable);

}
