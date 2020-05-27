package com.fpt.base.net;

import okhttp3.Interceptor;

/**
 * <pre>
 *   @author  : tocci.feng
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2020/05/14 08:33
 *   desc    : 网络配置
 * </pre>
 */
public interface NetConfig {

    /**
     * 通用请求host(Retrofit2的baseUrl必须以"/"结束)
     * @return
     */
    String baseUrl();

    /**
     * 连接超时时间
     * @return
     */
    long connectTimeoutMills();

    /**
     * 读取超时时间
     * @return
     */
    long readTimeoutMills();

    /**
     * 普通拦截器
     * @return
     */
    Interceptor[] interceptors();

    /**
     * 日志打印拦截器
     * @return
     */
    Interceptor logInterceptor();

}

