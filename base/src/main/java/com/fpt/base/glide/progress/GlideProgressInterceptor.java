package com.fpt.base.glide.progress;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * <pre>
 *   @author  : tocci.feng
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2020/05/15 13:35
 *   desc    : 拦截器（监听进度）
 * </pre>
 */
public class GlideProgressInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        Response response = chain.proceed(request);

        String url = request.url().toString();
        ResponseBody body = response.body();

        Response newResponse = response.newBuilder()
                .body(new GlideProgressBody(url, body))
                .build();
        return newResponse;
    }

}
