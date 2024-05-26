package com.ydd.zhichat.ui.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.ydd.zhichat.util.LocaleHelper;

public abstract class BaseActivity extends BaseLoginActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleHelper.setLocale(this, LocaleHelper.getLanguage(this));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
    }
}
