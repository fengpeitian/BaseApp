package com.fpt.base.glide.progress;

import androidx.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.util.ContentLengthInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * <pre>
 *   @author  : tocci.feng
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2020/05/15 16:38
 *   desc    : 数据抓取
 * </pre>
 */
public class OkHttpUrlFetcher implements DataFetcher<InputStream> {
    private final OkHttpClient client;
    private final GlideUrl url;

    private InputStream stream;
    private ResponseBody responseBody;
    private volatile boolean isCancelled = false;

    public OkHttpUrlFetcher(OkHttpClient client, GlideUrl url) {
        this.client = client;
        this.url = url;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super InputStream> callback) {
        Request.Builder requestBuilder = new Request.Builder()
                .url(url.toStringUrl());
        for (Map.Entry<String, String> headerEntry : url.getHeaders().entrySet()) {
            String key = headerEntry.getKey();
            requestBuilder.addHeader(key, headerEntry.getValue());
        }
        Request request = requestBuilder.build();
        if (isCancelled) {
            callback.onLoadFailed(new IOException("Request is be cancel"));
            return;
        }
        try {
            Response response = client.newCall(request).execute();
            responseBody = response.body();
            if (!response.isSuccessful() || responseBody == null) {
                callback.onLoadFailed(new IOException("Request failed with code: " + response.code()));
            }
            stream = ContentLengthInputStream.obtain(responseBody.byteStream(),
                    responseBody.contentLength());
            callback.onDataReady(stream);
        } catch (IOException e) {
            callback.onLoadFailed(e);
        }

    }

    @Override
    public void cleanup() {
        try {
            if (stream != null) {
                stream.close();
            }
            if (responseBody != null) {
                responseBody.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cancel() {
        isCancelled = true;
    }

    @NonNull
    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.REMOTE;
    }

}
