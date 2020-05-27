package com.fpt.base.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.fpt.base.R;

/**
 * <pre>
 *   @author  : tocci.feng
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2020/05/26 14:51
 *   desc    : 加载view
 * </pre>
 */
public class LoadingView extends View {

    private Context mContext;

    private int mWidth;

    private Paint mPaint;

    private float[] mArray;

    public LoadingView(Context context) {
        super(context);
        init(context,null, 0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs, 0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context,attrs,defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mContext = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LoadingView, defStyleAttr, 0);
        int radius = ta.getDimensionPixelSize(R.styleable.LoadingView_radius, dp2px(20));
        int color = ta.getColor(R.styleable.LoadingView_color, Color.WHITE);
        ta.recycle();

        mWidth = 2*radius + dp2px(10);
        mArray = new float[]{1.5f,2,2.5f,3,3.5f,4,4.5f,5};

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(color);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 设置宽高
        setMeasuredDimension(mWidth, mWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 旋转数组
        mArray = rotate(mArray);
        // 显示进度
        for (int i = 0; i < 8; i++){

            canvas.save();
            canvas.rotate(45*i%360, mWidth / 2, mWidth / 2);

            float r = mArray[i];

            canvas.drawCircle(mWidth/2,dp2px(5),dp2px(r),mPaint);

            canvas.restore();
        }

    }

    /**
     * 数组旋转
     * @param arr
     * @return
     */
    private float[] rotate(float[] arr) {
        float[] newArr = new float[arr.length];
        newArr[0] = arr[arr.length-1];
        for (int j = 1; j < arr.length; j++) {
            newArr[j] = arr[j-1];
        }
        return newArr;
    }

    /**
     * dp转px
     * @param dp
     * @return
     */
    private int dp2px(float dp){
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

    /**
     * 刷新视图
     */
    public void refresh() {
        postInvalidate();
    }

}
