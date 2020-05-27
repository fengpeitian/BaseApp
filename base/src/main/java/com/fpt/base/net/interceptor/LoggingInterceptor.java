package com.fpt.base.net.interceptor;

import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Headers;

/**
 * <pre>
 *   @author  : lucien.feng
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2018/08/13 09:51
 *   desc    : 网络拦截器
 * </pre>
 */
public abstract class LoggingInterceptor extends JsonInterceptor {

    /**
     * 字符拼接
     */
    private final Map<String, String> mLog = new ConcurrentHashMap<>();

    /**
     * 请求体
     *
     * @param url     请求网址
     * @param method  请求方法
     * @param headers 请求头
     * @param params  请求参数
     */
    @Override
    protected void outputRequestMsg(String url, String method, Headers headers, String params) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Request:--->[url = %s]\n", url));
        sb.append(String.format("Method :--->[method = %s]\n", method));
        for (String name : headers.names()) {
            sb.append(String.format("Headers:--->[%s:%s]\n",
                    name, headers.get(name)));
        }
        if (!TextUtils.isEmpty(params)) {
            sb.append(String.format("Params :--->[params = %s]", params));
        }

        mLog.put(url,sb.toString());
    }

    /**
     * 返回体
     *
     * @param url     网址
     * @param time    耗时
     * @param headers 头信息
     * @param receive 返回消息体
     */
    @Override
    protected void outputResponseMsg(String url, double time, Headers headers, String receive) {
        if (mLog.containsKey(url) && !TextUtils.isEmpty(mLog.get(url))) {

            StringBuilder sb = new StringBuilder();
            sb.append(String.format(Locale.getDefault(),
                    "Response:--->[in %.1fms]\n", time));
            for (String name : headers.names()) {
                sb.append(String.format("Headers:--->[%s:%s]\n",
                        name, headers.get(name)));
            }

            String body = JsonFormat.formatJson(receive);
            // 拼接返回结果
            sb.append(String.format("Body:\n%s", body));

            String responseStr = sb.toString();
            String requestStr = mLog.get(url);

            showResponseMsg(requestStr + "\n" + responseStr);

            mLog.remove(url);
        }
    }

    @Override
    protected void outputErrorMsg(String url, Throwable throwable) {
        if (mLog.containsKey(url)){
            mLog.remove(url);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        throwable.printStackTrace(new PrintStream(baos));
        String eMsg = baos.toString();

        if (!TextUtils.isEmpty(eMsg)) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Error:--->[url = %s]\n", url));
            sb.append(String.format("PrintStackTrace:\n%s", eMsg));

            showErrorMsg(sb.toString());
        }
    }

    /**
     * 显示response信息
     * @param msg
     */
    protected abstract void showResponseMsg(String msg);

    /**
     * 显示错误信息
     * @param eMsg
     */
    protected abstract void showErrorMsg(String eMsg);

}
