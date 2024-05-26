package com.ydd.zhichat.ui.base;

import android.os.Bundle;
import android.util.Log;

import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;
import com.ydd.zhichat.AppConfig;
import com.ydd.zhichat.MyApplication;
import com.ydd.zhichat.bean.User;
import com.ydd.zhichat.util.DateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public abstract class BaseLoginActivity extends ActionBackActivity implements CoreStatusListener {
    public CoreManager coreManager;
    private List<CoreStatusListener> coreStatusListeners;
    private boolean loginRequired = true;
    private boolean configRequired = true;

    protected void noLoginRequired() {
        Log.d(TAG, "noLoginRequired() called");
        loginRequired = false;
    }

    protected void noConfigRequired() {
        Log.d(TAG, "noConfigRequired() called");
        configRequired = false;
    }

    // 注册CoreManager初始化状态的监听，比如fragment可以调用，
    public void addCoreStatusListener(CoreStatusListener listener) {
        coreStatusListeners.add(listener);
    }

    public User getUser() {
        return coreManager.getSelf();
    }

    protected void updatUser(User user) {

    }

    public void Update(){
        Map<String, String> params = new HashMap<>();
        params.put("access_token", CoreManager.requireSelfStatus(MyApplication.getInstance()).accessToken);
        params.put("userId", MyApplication.mCoreManager.getSelf().getUserId());

        HttpUtils.get().url(CoreManager.requireConfig(MyApplication.getInstance()).USER_GET_URL)
                .params(params)
                .build()
                .execute(new BaseCallback<User>(User.class) {
                    @Override
                    public void onResponse(ObjectResult<User> result) {
                        if (result.getResultCode() == 1 && result.getData() != null) {
                            User user = result.getData();
                            MyApplication.mCoreManager.setSelf(user);// = user;
                            updatUser(user);
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {

                    }
                });

    }

    public String getToken() {
        return coreManager.getSelfStatus().accessToken;
    }

    public AppConfig getAppConfig() {
        return coreManager.getConfig();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCore();
    }

    private void initCore() {
        Log.d(TAG, "initCore() called");
        if (coreManager == null) {
            coreManager = new CoreManager(this, this);
        }
        if (coreStatusListeners == null) {
            coreStatusListeners = new ArrayList<>();
        }
        coreManager.init(loginRequired, configRequired);
    }

    @Override
    public void onCoreReady() {
        Log.d(TAG, "onCoreReady() called");
        for (CoreStatusListener listener : coreStatusListeners) {
            listener.onCoreReady();
        }
    }

    @Override
    protected void onDestroy() {
        coreManager.destroy();
        super.onDestroy();
    }
}
