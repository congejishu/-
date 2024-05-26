package com.ydd.zhichat.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.fm.openinstall.OpenInstall;
import com.ydd.zhichat.AppConfig;
import com.ydd.zhichat.AppConstant;
import com.ydd.zhichat.BuildConfig;
import com.ydd.zhichat.MyApplication;
import com.ydd.zhichat.R;
import com.ydd.zhichat.Reporter;
import com.ydd.zhichat.adapter.MessageLogin;
import com.ydd.zhichat.bean.ConfigBean;
import com.ydd.zhichat.db.InternationalizationHelper;
import com.ydd.zhichat.helper.DialogHelper;
import com.ydd.zhichat.helper.LoginHelper;
import com.ydd.zhichat.ui.account.LoginActivity;
import com.ydd.zhichat.ui.account.LoginHistoryActivity;
import com.ydd.zhichat.ui.account.RegisterActivity;
import com.ydd.zhichat.ui.base.BaseActivity;
import com.ydd.zhichat.ui.base.CoreManager;
import com.ydd.zhichat.ui.notification.NotificationProxyActivity;
import com.ydd.zhichat.ui.other.PrivacyAgreeActivity;
import com.ydd.zhichat.util.Constants;
import com.ydd.zhichat.util.EventBusHelper;
import com.ydd.zhichat.util.LocaleHelper;
import com.ydd.zhichat.util.LogUtils;
import com.ydd.zhichat.util.PermissionUtil;
import com.ydd.zhichat.util.PreferenceUtils;
import com.ydd.zhichat.util.TimeUtils;
import com.ydd.zhichat.util.ToastUtil;
import com.ydd.zhichat.util.VersionUtil;
import com.ydd.zhichat.view.PermissionExplainDialog;
import com.ydd.zhichat.view.TipDialog;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;
import com.xuan.xuanhttplibrary.okhttp.result.Result;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import okhttp3.Call;

/**
 * 启动页
 */
public class SplashActivity extends BaseActivity implements PermissionUtil.OnRequestPermissionsResultCallbacks {
    // 声明一个数组，用来存储所有需要动态申请的权限
    private static final int REQUEST_CODE = 0;
    private final Map<String, Integer> permissionsMap = new LinkedHashMap<>();

    private LinearLayout mSelectLv;
    private Button mSelectLoginBtn;
    private Button mSelectRegisBtn;

    // 配置是否成功
    private boolean mConfigReady = false;
    // 复用请求权限的说明对话框，
    private PermissionExplainDialog permissionExplainDialog;

    public SplashActivity() {
        // 这个页面不需要已经获取config, 也不需要已经登录，
        noConfigRequired();
        noLoginRequired();

        // 手机状态
        permissionsMap.put(Manifest.permission.READ_PHONE_STATE, R.string.permission_phone_status);
        // 照相
        permissionsMap.put(Manifest.permission.CAMERA, R.string.permission_photo);
        // 麦克风
        permissionsMap.put(Manifest.permission.RECORD_AUDIO, R.string.permission_microphone);
        // 存储权限
        permissionsMap.put(Manifest.permission.READ_EXTERNAL_STORAGE, R.string.permission_storage);
        permissionsMap.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, R.string.permission_storage);
        // 定位权限等获取到配置再看是否要请求，
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //   ActivityStack.getInstance().isExistMainActivity(this);
        PreferenceUtils.putBoolean(this, "isBoot", false);
        Log.d("zxzx", "onCreate: " + SplashActivity.class.getSimpleName());
        Intent intent = getIntent();
        LogUtils.log(TAG, intent);
        if (NotificationProxyActivity.processIntent(intent)) {
            // 如果是通知点击进来的，带上参数转发给NotificationProxyActivity处理，
            intent.setClass(this, NotificationProxyActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        // 如果不是任务栈第一个页面，就直接结束，显示上一个页面，
        // 主要是部分设备上Jitsi_pre页面退后台再回来会打开这个启动页flg=0x10200000，此时应该结束启动页，回到Jitsi_pre,
        if (!isTaskRoot()) {
            finish();
            return;
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_splash);
        // mStartTimeMs = System.currentTimeMillis();
        mSelectLv = (LinearLayout) findViewById(R.id.select_lv);
        mSelectLoginBtn = (Button) findViewById(R.id.select_login_btn);
        mSelectLoginBtn.setText(InternationalizationHelper.getString("JX_Login"));
        mSelectLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, LoginActivity.class));
                finish();
            }
        });
        mSelectRegisBtn = (Button) findViewById(R.id.select_register_btn);
        mSelectRegisBtn.setText(InternationalizationHelper.getString("REGISTERS"));
        mSelectRegisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, RegisterActivity.class));
            }
        });
        mSelectLv.setVisibility(View.INVISIBLE);

        // 注册按钮先隐藏，如果是接口返回开放注册再显示，
        mSelectRegisBtn.setVisibility(View.GONE);

        // 初始化配置
        initConfig();
        // 同时请求定位以外的权限，
        requestPermissions();

        EventBusHelper.register(this);

        OpenInstall.init(this);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void helloEventBus(MessageLogin message) {
        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms, boolean isAllGranted) {
        if (isAllGranted) {// 请求权限返回 已全部授权
            ready();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms, boolean isAllDenied) {
        if (BuildConfig.DEBUG) {
            jump();
            return;
        }
        if (perms != null && perms.size() > 0) {
            // 用set去重，有的复数权限
            Set<String> tipSet = new HashSet<>();
            for (int i = 0; i < perms.size(); i++) {
                tipSet.add(getString(permissionsMap.get(perms.get(i))));
            }
            String tipS = TextUtils.join(", ", tipSet);
            boolean onceAgain = PermissionUtil.deniedRequestPermissionsAgain(this, perms.toArray(new String[perms.size()]));
            TipDialog mTipDialog = new TipDialog(this);
            if (onceAgain) {// 部分 || 所有权限被拒绝且选择了（选择了不再询问 || 部分机型默认为不在询问）
                mTipDialog.setmConfirmOnClickListener(getString(R.string.tip_reject_permission_place_holder, tipS), new TipDialog.ConfirmOnClickListener() {
                    @Override
                    public void confirm() {
                        PermissionUtil.startApplicationDetailsSettings(SplashActivity.this, REQUEST_CODE);
                    }
                });
            } else {// 部分 || 所有权限被拒绝
                mTipDialog.setTip(getString(R.string.tip_permission_reboot_place_holder, tipS));
            }
            mTipDialog.show();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        // 请求权限过程中离开了回来就再请求吧，
        ready();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {// 设置 手动开启权限 返回 再次判断是否获取全部权限
            ready();
        }
    }

    /**
     * 配置参数初始化
     */
    private void initConfig() {
        /*if (!MyApplication.getInstance().isNetworkActive()) {
            ToastUtil.showToast(SplashActivity.this, R.string.tip_get_config_failed);
            // 在断网的情况下，使用已经保存了的配置，
            ConfigBean configBean = coreManager.readConfigBean();
            setConfig(configBean);
            return;
        }*/

        getConfig();
    }

    private void getConfig() {
        String mConfigApi = AppConfig.readConfigUrl(mContext);

        Map<String, String> params = new HashMap<>();
        Reporter.putUserData("configUrl", mConfigApi);
        HttpUtils.get().url(mConfigApi)
                .params(params)
                .build()
                .execute(new BaseCallback<ConfigBean>(ConfigBean.class) {
                    @Override
                    public void onResponse(ObjectResult<ConfigBean> result) {
                        if (result != null) {
                            TimeUtils.responseTime(result.getCurrentTime());
                        }
                        ConfigBean configBean;
                        if (result == null || result.getData() == null || result.getResultCode() != Result.CODE_SUCCESS) {
                            Log.e("zq", "获取网络配置失败，使用已经保存了的配置");
                            if (BuildConfig.DEBUG) {
                                ToastUtil.showToast(SplashActivity.this, R.string.tip_get_config_failed);
                            }
                            // 获取网络配置失败，使用已经保存了的配置，
                            configBean = coreManager.readConfigBean();
                        } else {
                            Log.e("zq", "获取网络配置成功，使用服务端返回的配置并更新本地配置");
                            configBean = result.getData();
                            if (!TextUtils.isEmpty(configBean.getAddress())) {
                                PreferenceUtils.putString(SplashActivity.this, AppConstant.EXTRA_CLUSTER_AREA, configBean.getAddress());
                            }
                            coreManager.saveConfigBean(configBean);
                            MyApplication.IS_OPEN_CLUSTER = configBean.getIsOpenCluster() == 1 ? true : false;
                        }
                        setConfig(configBean);
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        Log.e("zq", "获取网络配置失败，使用已经保存了的配置");
                        // ToastUtil.showToast(SplashActivity.this, R.string.tip_get_config_failed);
                        // 获取网络配置失败，使用已经保存了的配置，
                        ConfigBean configBean = coreManager.readConfigBean();
                        setConfig(configBean);
                    }
                });
    }

    private void setConfig(ConfigBean configBean) {
        if (configBean == null) {
            if (BuildConfig.DEBUG) {
                ToastUtil.showToast(this, R.string.tip_get_config_failed);
            }

            // 如果没有保存配置，也就是第一次使用，就连不上服务器，使用默认配置
            configBean = CoreManager.getDefaultConfig(this);
            if (configBean == null) {
                // 不可到达，本地assets一定要提供默认config,
                DialogHelper.tip(this, getString(R.string.tip_get_config_failed));
                return;
            }
            coreManager.saveConfigBean(configBean);
        }
        LogUtils.log(TAG, configBean);
        // 初始化配置
        if (coreManager.getConfig().isOpenRegister) {
            mSelectRegisBtn.setVisibility(View.VISIBLE);
        } else {
            mSelectRegisBtn.setVisibility(View.GONE);
        }
        if (!coreManager.getConfig().disableLocationServer) {
            // 定位
            permissionsMap.put(Manifest.permission.ACCESS_COARSE_LOCATION, R.string.permission_location);
            permissionsMap.put(Manifest.permission.ACCESS_FINE_LOCATION, R.string.permission_location);
        }
        // 配置完毕
        mConfigReady = true;
        String lang = CoreManager.requireConfig(this).language;
        if(lang.equals("big5")) {
            lang = "TW";
        }
        LocaleHelper.setLocale(this,lang);
        // 如果没有androidDisable字段就不判断，
        // 当前版本没被禁用才继续打开，
        if (TextUtils.isEmpty(configBean.getAndroidDisable()) || !blockVersion(configBean.getAndroidDisable(), configBean.getAndroidAppUrl())) {
            // 进入主界面
            ready();
        }
    }

    /**
     * 如果当前版本被禁用，就自杀，
     *
     * @param disabledVersion 禁用该版本以下的版本，
     * @param appUrl          版本被禁用时打开的地址，
     * @return 返回是否被禁用，
     */
    private boolean blockVersion(String disabledVersion, String appUrl) {
        String currentVersion = BuildConfig.VERSION_NAME;
        if (VersionUtil.compare(currentVersion, disabledVersion) > 0) {
            // 当前版本大于被禁用版本，
            return false;
        } else {
            // 通知一下，
            DialogHelper.tip(this, getString(R.string.tip_version_disabled));
            TipDialog tipDialog = new TipDialog(this);
            tipDialog.setOnDismissListener(dialog -> {
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(appUrl));
                    startActivity(i);
                } catch (Exception e) {
                    // 弹出浏览器失败的话无视，
                    // 比如没有浏览器的情况，
                    // 比如appUrl不合法的情况，
                }
                // 自杀，
                finish();
                MyApplication.getInstance().destory();
            });
            tipDialog.show();
            return true;
        }
    }

    private void ready() {
        if (!mConfigReady) {// 配置失败
            return;
        }

        // 检查 || 请求权限
        boolean hasAll = requestPermissions();
        if (hasAll) {// 已获得所有权限
            jump();
        }
    }

    private boolean requestPermissions() {
        // 请求定位以外的权限，
        return requestPermissions(permissionsMap.keySet().toArray(new String[]{}));
    }

    private boolean requestPermissions(String... permissions) {
        List<String> deniedPermission = PermissionUtil.getDeniedPermissions(this, permissions);
        if (deniedPermission != null) {
            PermissionExplainDialog tip = getPermissionExplainDialog();
            tip.setPermissions(deniedPermission.toArray(new String[0]));
            tip.setOnConfirmListener(() -> {
                PermissionUtil.requestPermissions(this, SplashActivity.REQUEST_CODE, permissions);
            });
            tip.show();
            return false;
        }
        return true;
    }

    private PermissionExplainDialog getPermissionExplainDialog() {
        if (permissionExplainDialog == null) {
            permissionExplainDialog = new PermissionExplainDialog(this);
        }
        return permissionExplainDialog;
    }

    @SuppressLint("NewApi")
    private void jump() {
        if (isDestroyed()) {
            return;
        }
        int userStatus = LoginHelper.prepareUser(mContext, coreManager);
        Intent intent = new Intent();
        switch (userStatus) {
            case LoginHelper.STATUS_USER_FULL:
            case LoginHelper.STATUS_USER_NO_UPDATE:
            case LoginHelper.STATUS_USER_TOKEN_OVERDUE:
                boolean login = PreferenceUtils.getBoolean(this, Constants.LOGIN_CONFLICT, false);
                if (login) {// 登录冲突，退出app再次进入，跳转至历史登录界面
                    intent.setClass(mContext, LoginHistoryActivity.class);
                } else {
                    intent.setClass(mContext, MainActivity.class);

                }
                break;
            case LoginHelper.STATUS_USER_SIMPLE_TELPHONE:
                intent.setClass(mContext, LoginHistoryActivity.class);
                break;
            case LoginHelper.STATUS_NO_USER:
            default:
                stay();
                return;// must return
        }
        startActivity(intent);
        finish();
    }

    // 第一次进入酷聊，显示登录、注册按钮
    private void stay() {
        // 因为启动页有时会替换，无法做到按钮与图片的完美适配，干脆直接进入到登录页面
        startActivity(new Intent(mContext, LoginActivity.class));
        finish();
    }
}