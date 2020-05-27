package com.fpt.base.net;

import android.text.TextUtils;

import com.fpt.base.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * <pre>
 *   @author  : tocci.feng
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2020/05/14 08:36
 *   desc    : 网络请求
 * </pre>
 */
public class RetrofitManager {
    /**
     * 网络配置项
     */
    private static NetConfig mConfig = null;
    /**
     * Retrofit
     */
    private Retrofit mRetrofit;
    /**
     * OkHttpClient
     */
    private OkHttpClient mClient;
    /**
     * 默认连接超时时间
     */
    private static final long DEFAULT_CONNECT_TIMEOUT_MILLS = 10 * 1000L;
    /**
     * 默认读取超时时间
     */
    private static final long DEFAULT_READ_TIMEOUT_MILLS = 10 * 1000L;

    private RetrofitManager() {
        mRetrofit = null;
        mClient = null;
    }

    private static class RetrofitInstance {
        private static final RetrofitManager INSTANCE = new RetrofitManager();
    }

    public static Retrofit getInstance() {
        return RetrofitInstance.INSTANCE.getRetrofit();
    }

    /**
     * 注册配置(first)
     * @param config  网络配置
     */
    public static void config(NetConfig config) {
        mConfig = config;
    }

    /**
     * 创建接口类
     * @param service 服务class
     * @param <C>     类泛型
     * @return        泛型
     */
    public <C> C getApi(Class<C> service) {
        return getInstance().create(service);
    }

    /**
     * 获取Retrofit实例
     * @return 获取Retrofit
     */
    public Retrofit getRetrofit() {
        if (mConfig == null) {
            throw new IllegalArgumentException("No network configuration for Retrofit.");
        }
        if (TextUtils.isEmpty(mConfig.baseUrl())){
            throw new IllegalArgumentException("Retrofit-BaseUrl is not configured.");
        }
        if (mRetrofit == null) {
            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(mConfig.baseUrl())
                    .client(getOkHttpClient(mConfig))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
            mRetrofit = builder.build();
        }
        return mRetrofit;
    }

    /**
     * 获取okHttpClient
     * @return OkHttpClient
     * @param config
     */
    private OkHttpClient getOkHttpClient(NetConfig config) {
        if (mClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            // 连接超时时间
            builder.connectTimeout(config.connectTimeoutMills() != 0
                    ? config.connectTimeoutMills() : DEFAULT_CONNECT_TIMEOUT_MILLS, TimeUnit.MILLISECONDS);
            // 读取超时时间
            builder.readTimeout(config.readTimeoutMills() != 0
                    ? config.readTimeoutMills() : DEFAULT_READ_TIMEOUT_MILLS, TimeUnit.MILLISECONDS);
            // 允许重定向
            builder.followRedirects(true);
            // 允许失败重连
            builder.retryOnConnectionFailure(true);
            // 拦截器
            Interceptor[] interceptors = config.interceptors();
            if (interceptors != null && interceptors.length > 0) {
                for (Interceptor interceptor : interceptors) {
                    builder.addInterceptor(interceptor);
                }
            }
            // 是否处于debug模式
            if (BuildConfig.DEBUG) {
                // 日志打印
                if (config.logInterceptor() != null) {
                    builder.addNetworkInterceptor(config.logInterceptor());
                }
            }
            mClient = builder.build();
        }
        return mClient;
    }

}
