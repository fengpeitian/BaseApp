package com.fpt.base.net.https;

import androidx.annotation.NonNull;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * <pre>
 *   @author  : fpt
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2018/12/14 09:25
 *   desc    : ip验证
 * </pre>
 */
public class SafeHostnameVerifier implements HostnameVerifier {
    /**
     * ip地址; "192.168.2.256"
     */
    private String ip;

    public SafeHostnameVerifier(@NonNull String ip) {
        this.ip = ip;
    }

    @Override
    public boolean verify(String hostname, SSLSession session) {
        //校验hostname是否正确，如果正确则建立连接
        return ip.equals(hostname);
    }

}
