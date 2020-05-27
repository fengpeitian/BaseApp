package com.fpt.base.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fpt.base.receiver.NetConnectReceiver;

import org.greenrobot.eventbus.EventBus;

/**
 * <pre>
 *   @author : tocci.feng
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2020/05/14 11:08
 *   desc    : BaseFragment
 * </pre>
 */
public abstract class BaseFragment<T extends BaseActivity> extends Fragment implements IView, NetConnectReceiver.OnNetworkStateChangeListener {
    protected Context mContext;
    protected T mActivity;
    private View mRootView;
    private boolean isViewInitiated = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        //注册外网络状态变化监听
        new NetConnectReceiver(mContext,this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (T) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        int layoutResId = attachLayoutRes(savedInstanceState);
        mRootView = inflater.inflate(layoutResId, container, false);
        findViewsById();
        initViews();
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (useEventBus()){
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
        }

        onViewCreated();
        isViewInitiated = true;
    }

    /**
     * add/hide/show 显示fragment 这个方法会调用
     * @param hidden
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden && isViewInitiated){
            onRefresh();
        }
    }

    /**
     * 在fragment结合viewpager使用的时候 这个方法会调用
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getUserVisibleHint() && isViewInitiated) {
            onRefresh();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    /**
     * 找到控件ID
     */
    protected <R extends View> R findViewById(int id) {
        if (mRootView == null) {
            return null;
        }
        return (R) mRootView.findViewById(id);
    }

    /**
     * 布局创建好
     */
    protected void onViewCreated() {}

    /**
     * 从别的页面return回来后刷新页面
     */
    protected void onRefresh(){}

    /**
     * 网络状态变化
     * @param connect
     */
    @Override
    public void onNetworkStateChange(boolean connect) {}

}
