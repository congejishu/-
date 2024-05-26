package com.ydd.zhichat.ui.me.uid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;
import com.ydd.zhichat.AppConstant;
import com.ydd.zhichat.R;
import com.ydd.zhichat.bean.Friend;
import com.ydd.zhichat.bean.UUidShop;
import com.ydd.zhichat.helper.DialogHelper;
import com.ydd.zhichat.ui.base.BaseActivity;
import com.ydd.zhichat.ui.me.VipServiceActivity;
import com.ydd.zhichat.ui.me.redpacket.PayPasswordVerifyDialog;
import com.ydd.zhichat.ui.message.ChatActivity;
import com.ydd.zhichat.util.ToastUtil;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class UidInfoActivity extends BaseActivity implements View.OnClickListener{
    public static final String FRIEND = "UidInfo";
    private UUidShop data;
    TextView tvBuyPay;
    TextView tvBayNum;
    TextView tvBayNum2;
    TextView tvUidText;

    public static void start(Activity ctx, UUidShop vip){
        Intent intent = new Intent(ctx, UidInfoActivity.class);
        intent.putExtra(FRIEND, vip);
        ctx.startActivityForResult(intent,200);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uid_info);
        getSupportActionBar().hide();
        findViewById(R.id.iv_title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        data = (UUidShop) getIntent().getSerializableExtra(FRIEND);
        tvBuyPay = findViewById(R.id.buy_pay);
        tvBayNum = findViewById(R.id.pay_num);
        tvBayNum2 = findViewById(R.id.pay_num2);
        tvUidText = findViewById(R.id.uid_tv);

        updateUI();
    }

    public void updateUI(){
        tvBayNum.setText("价值:"+data.getPrice()+"金币");
        tvBayNum2.setText(data.getPrice()+"金币");
        tvUidText.setText(data.getUid());

        tvBuyPay.setOnClickListener((res)->{
            PayPasswordVerifyDialog dialog = new PayPasswordVerifyDialog(this);
            dialog.setAction("购买靓号");
            dialog.setMoney(data.getPrice()+"");
            dialog.setOnInputFinishListener(new PayPasswordVerifyDialog.OnInputFinishListener() {
                @Override
                public void onInputFinish(final String password) {

                    //与服务端进行交互
                    sendRed(data.getId(), password,data.getPrice()+"");
                    //finish();
                }
            });
            dialog.show();
        });
    }

    public void sendRed(final String id, String payPassword,String money) {
        /*
        if (!ImHelper.checkXmppAuthenticated()) {
            return;
        }

         */
        Map<String, String> params = new HashMap<>();
        params.put("access_token", coreManager.getSelfStatus().accessToken);
        params.put("id", id);//vip类型
        String mAccessToken = coreManager.getSelfStatus().accessToken;
        String mLoginUserId = coreManager.getSelf().getUserId();
        HttpUtils.get().url(coreManager.getConfig().UID_SET)
                .params(params)
                .addSecret(payPassword, money)
                .build()
                .execute(new BaseCallback<Void>(Void.class) {

                    @Override
                    public void onResponse(ObjectResult<Void> result) {
                        if (result.getResultCode() == 1) {
                            ToastUtil.showToast("购买成功");
                            setResult(200);
                            finish();
                        } else {
                            ToastUtil.showToast(result.getResultMsg());
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                        ToastUtil.showNetError(UidInfoActivity.this);
                    }
                });
    }
}
