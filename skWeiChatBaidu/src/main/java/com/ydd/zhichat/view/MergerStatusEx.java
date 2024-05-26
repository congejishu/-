package com.ydd.zhichat.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.TextView;

import com.ydd.zhichat.R;
import com.ydd.zhichat.util.SkinUtils;

/**
 * Created by Administrator on 2017/12/14 0014.
 * 兼容沉浸式状态栏
 */

public class MergerStatusEx extends Toolbar {
    private String TAG = "MergerStatus";
    private TextView textView;

    public MergerStatusEx(Context context) {
        this(context, null);
    }

    public MergerStatusEx(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MergerStatusEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MergerStatus, defStyleAttr, 0);
        int iconSrc = a.getResourceId(R.styleable.MergerStatus_drawable_status, 0);
        setup(iconSrc);
    }

    private void setup(int resId) {
        int mCompatPaddingTop = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mCompatPaddingTop = getStatusHeight();
        }
        this.setPadding(getPaddingLeft(), getPaddingTop() + mCompatPaddingTop, getPaddingRight(), getPaddingBottom());
        // 更换Toolbar背景颜色
        //this.setBackground();
        if(resId != 0) {
            this.setBackgroundResource(resId);
        }
    }



    private int getStatusHeight() {
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        // Log.e("zq", "状态栏高度：" + px2dp(statusBarHeight) + "dp");
        return statusBarHeight;
    }

    private float px2dp(float pxVal) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }
}
