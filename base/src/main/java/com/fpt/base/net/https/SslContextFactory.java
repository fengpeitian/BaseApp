package com.fpt.base.net.https;

import android.content.Context;

import com.fpt.base.app.ContextProvider;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * <pre>
 *   @author  : fpt
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2018/12/14 09:25
 *   desc    : ssl验证( https://blog.csdn.net/lmj623565791/article/details/48129405 )
 * </pre>
 */
public class SslContextFactory {
    private static final String CLIENT_TRUST_MANAGER = "X.509";
    private static final String CLIENT_AGREEMENT = "TLS";
    private static final String CLIENT_TRUST_KEYSTORE = "BKS";

    /**
     * 单项认证
     * @return SSLSocketFactory
     */
    public static SSLSocketFactory getSSLSocketFactory(InputStream... certificates) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance(CLIENT_TRUST_MANAGER);
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
                try {
                    if (certificate != null) {
                        certificate.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            SSLContext sslContext = SSLContext.getInstance(CLIENT_AGREEMENT);
            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 单项认证
     * @param fileName  本地assets文件下的'.car'文件
     * @return
     */
    public static SSLSocketFactory getSSLSocketFactory(String fileName) {
        Context context = ContextProvider.get().getContext();
        try {
            return getSSLSocketFactory(context.getAssets().open(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 双向认证
     * @param trust       服务器授信证书
     * @param trustPsd    服务端证书密码
     * @param client      客户端证书
     * @param psd         客户端证书密码
     * @return
     */
    public static SSLSocketFactory getSSLSocketFactory(String trust, String trustPsd, String client, String psd) {
        Context context = ContextProvider.get().getContext();

        InputStream trust_input = null;
        InputStream client_input = null;
        try {
            trust_input = context.getAssets().open(trust);
            client_input = context.getAssets().open(client);

            SSLContext sslContext = SSLContext.getInstance(CLIENT_AGREEMENT);

            // cer
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(trust_input, trustPsd.toCharArray());
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);

            //bks
            KeyStore keyStore = KeyStore.getInstance(CLIENT_TRUST_KEYSTORE);
            keyStore.load(client_input, psd.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, psd.toCharArray());

            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
            SSLSocketFactory factory = sslContext.getSocketFactory();
            return factory;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                trust_input.close();
                client_input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
