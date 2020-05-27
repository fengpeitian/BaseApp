package com.fpt.base.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.fpt.base.OnProgressListener;
import com.fpt.base.app.ContextProvider;
import com.fpt.base.glide.progress.GlideProgress;

/**
 * <pre>
 *   @author  : tocci.feng
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2020/05/15 10:46
 *   desc    : glide封装
 * </pre>
 */
public class GlideImageLoader {
    private Context mContext;

    private GlideImageLoader() {
        this.mContext = ContextProvider.get().getContext();
    }

    private static class GlideImageLoaderInstance {
        private static final GlideImageLoader INSTANCE = new GlideImageLoader();
    }

    public static GlideImageLoader getInstance() {
        return GlideImageLoaderInstance.INSTANCE;
    }

    /**
     * 加载
     * @param url
     * @param imageView
     */
    public void load(String url, ImageView imageView) {
        Glide.with(mContext)
                .load(url)
                .into(imageView);
    }

    /**
     * 加载图片（带transform）
     * @param url
     * @param imageView
     * @param transformations   https://github.com/wasabeef/glide-transformations 里
     */
    public void load(String url, ImageView imageView, @NonNull Transformation<Bitmap>... transformations){
        MultiTransformation multi = new MultiTransformation(transformations);
        Glide.with(mContext)
                .load(url)
                .apply(RequestOptions.bitmapTransform(multi))
                .into(imageView);
    }

    /**
     * 加载（带默认图）
     * @param url
     * @param imageView
     * @param def        默认图
     */
    public void load(String url, ImageView imageView, int def) {
        Glide.with(mContext)
                .load(url)
                .error(def)
                .placeholder(def)
                .into(imageView);
    }

    /**
     * 加载图片至target
     * @param url
     * @param target
     */
    public void load(String url, CustomTarget<Bitmap> target) {
        // 加载
        Glide.with(mContext)
                .asBitmap()
                .load(url)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(target);
    }

    /**
     * 加载图片（带进度,不磁盘缓存）
     * @param url
     * @param imageView
     * @param listener
     */
    public void load(final String url, final ImageView imageView, OnProgressListener listener) {
        // 加载监听
        GlideProgress.addListener(url, listener);
        // 加载
        Glide.with(mContext)
                .asBitmap()
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        GlideProgress.removeListener(url);

                        if (imageView != null) {
                            imageView.setImageBitmap(resource);
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {}

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        GlideProgress.removeListener(url);
                    }

                });
    }

    /**
     * 加载圆形图片
     * @param url
     * @param imageView
     * @param def        默认图
     */
    public void loadCircle(String url, ImageView imageView, int def) {
        Glide.with(mContext)
                .load(url)
                .error(def)
                .placeholder(def)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }

    /**
     * 清除图片磁盘缓存
     */
    private void clearImageDiskCache() {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                // 子线程操作
                new DiskCacheClear().start();
            } else {
                Glide.get(mContext).clearDiskCache();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除图片内存缓存
     */
    private void clearImageMemoryCache() {
        try {
            //只能在主线程执行
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Glide.get(mContext).clearMemory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清理磁盘缓存
     */
    private class DiskCacheClear extends Thread {

        @Override
        public void run() {
            Glide.get(mContext).clearDiskCache();
        }

    }

}
