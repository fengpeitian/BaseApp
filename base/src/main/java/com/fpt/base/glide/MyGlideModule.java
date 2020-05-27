package com.fpt.base.glide;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.fpt.base.glide.progress.GlideProgressInterceptor;
import com.fpt.base.glide.progress.OkHttpGlideUrlLoader;

import java.io.InputStream;

import okhttp3.OkHttpClient;

/**
 * <pre>
 *   @author  : tocci.feng
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2020/05/15 11:31
 *   desc    : 配置GlideModule
 * </pre>
 */
@GlideModule
public class MyGlideModule extends AppGlideModule {

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .addInterceptor(new GlideProgressInterceptor())
                        .build();
        OkHttpGlideUrlLoader.Factory factory = new OkHttpGlideUrlLoader.Factory(okHttpClient);
        // 替换okHttp
        registry.replace(GlideUrl.class, InputStream.class,factory);
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

}
