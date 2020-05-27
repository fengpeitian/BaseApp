package com.fpt.base.glide.progress;

import com.fpt.base.OnProgressListener;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 *   @author  : tocci.feng
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2020/05/18 09:50
 *   desc    : glide加载进度监听
 * </pre>
 */
public class GlideProgress {

    public static final Map<String, OnProgressListener> LISTENER_MAP = new HashMap<>();

    /**
     * 添加进度监听
     * @param url
     * @param listener
     */
    public static void addListener(String url, OnProgressListener listener) {
        LISTENER_MAP.put(url, listener);
    }

    /**
     * 移除进度监听
     * @param url
     */
    public static void removeListener(String url) {
        LISTENER_MAP.remove(url);
    }

}
