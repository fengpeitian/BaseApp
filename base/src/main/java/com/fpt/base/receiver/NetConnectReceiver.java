package com.fpt.base.receiver;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;

import org.jetbrains.annotations.NotNull;

/**
 * <pre>
 *   @author  : tocci.feng
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2020/05/14 11:08
 *   desc    : 网络连接变化监听器
 * </pre>
 */
public class NetConnectReceiver {

    /**
     * 防重复状态标志位
     */
    private boolean flag = false;

    private Context mContext;
    private OnNetworkStateChangeListener mListener;

    public NetConnectReceiver(Context context, OnNetworkStateChangeListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    /**
     * 注册
     */
    public void register(){
        if (mContext == null){
            return;
        }
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkRequest nr = new NetworkRequest.Builder().build();

        assert cm != null;
        cm.requestNetwork(nr, new ConnectivityManager.NetworkCallback(){

            @Override
            public void onAvailable(@NotNull Network network) {
                super.onAvailable(network);

                if (!flag){
                    flag = true;
                    if (mListener != null){
                        mListener.onNetworkStateChange(true);
                    }
                }
            }

            @Override
            public void onLost(@NotNull Network network) {
                super.onLost(network);

                if (flag){
                    flag = false;
                    if (mListener != null){
                        mListener.onNetworkStateChange(false);
                    }
                }
            }

        });
    }

    public interface OnNetworkStateChangeListener {

        /**
         * 网络状态变化
         * @param connect
         */
        void onNetworkStateChange(boolean connect);

    }

}
