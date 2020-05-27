package com.fpt.base.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.fpt.base.R;
import com.fpt.base.app.ContextProvider;

import org.greenrobot.eventbus.EventBus;

/**
 * <pre>
 *   @author  : tocci.feng
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2020/05/26 13:20
 *   desc    : BasePopupWindow
 * </pre>
 */
public abstract class BasePopupWindow implements IView {
    private View mRootView;
    private Context mContext;
    private PopupWindow mPopupWindow;

    public BasePopupWindow() {
        mContext = ContextProvider.get().getContext();
        mRootView = LayoutInflater.from(mContext).inflate(attachLayoutRes(null), null);
        findViewsById();
        initViews();

        mPopupWindow = new PopupWindow(mRootView);
    }

    /**
     * 设置宽高
     * @param width   宽,单位dp
     * @param height  高,单位dp
     * @return
     */
    public BasePopupWindow setSize(int width, int height) {
        if (width == 0 || height == 0) {
            //如果没设置宽高，默认是WRAP_CONTENT
            mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            mPopupWindow.setWidth(dp2px(width));
            mPopupWindow.setHeight(dp2px(height));
        }
        return this;
    }

    /**
     * 设置背景昏暗度(无效)
     * @param dimAmount (0-1.0f)
     * @return
     */
    public BasePopupWindow setDimAmount(float dimAmount) {
        Window window = new Dialog(mContext, R.style.BaseDialog).getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.alpha = dimAmount;
        window.setAttributes(params);
        return this;
    }

    /**
     * 设置进入退出动画
     * @param animStyle   style资源
     * @return
     */
    public BasePopupWindow setAnimStyle(int animStyle) {
        mPopupWindow.setAnimationStyle(animStyle);
        return this;
    }

    /**
     * 设置是否有点击外部事件
     * @param touchable
     * @return
     */
    public BasePopupWindow setOutsideTouchable(boolean touchable) {
        //设置透明背景
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        //设置outside可点击
        mPopupWindow.setOutsideTouchable(touchable);
        mPopupWindow.setFocusable(touchable);
        return this;
    }

    /**
     * 消失
     */
    public void dismiss() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            if (EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().unregister(this);
            }
            mPopupWindow.dismiss();
        }
    }

    /**
     * 显示
     * @param anchor
     * @param x
     * @param y
     * @param gravity
     */
    public void showAsDropDown(View anchor, int x, int y, int gravity) {
        if (mPopupWindow != null && !mPopupWindow.isShowing()) {
            if (useEventBus()){
                if (!EventBus.getDefault().isRegistered(this)) {
                    EventBus.getDefault().register(this);
                }
            }
            mPopupWindow.showAsDropDown(anchor,x,y,gravity);
        }
    }

    /**
     * 显示
     * @param parent
     * @param gravity
     * @param x
     * @param y
     */
    public void showAtLocation(View parent, int gravity, int x, int y){
        if (mPopupWindow != null && !mPopupWindow.isShowing()) {
            if (useEventBus()){
                if (!EventBus.getDefault().isRegistered(this)) {
                    EventBus.getDefault().register(this);
                }
            }
            mPopupWindow.showAtLocation(parent,gravity,x,y);
        }
    }

    /**
     * 找到控件ID
     */
    protected <T extends View> T findViewById(int id) {
        if (mRootView == null) {
            return null;
        }
        return (T) mRootView.findViewById(id);
    }

    /**
     * dp转px
     * @param dipValue
     * @return
     */
    private int dp2px(float dipValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

}
