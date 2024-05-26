package com.ydd.zhichat.ui.me;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;
import com.ydd.zhichat.MyApplication;
import com.ydd.zhichat.R;
import com.ydd.zhichat.bean.AttentionUser;
import com.ydd.zhichat.bean.Friend;
import com.ydd.zhichat.bean.PaySelectBean;
import com.ydd.zhichat.bean.User;
import com.ydd.zhichat.broadcast.CardcastUiUpdateUtil;
import com.ydd.zhichat.db.dao.FriendDao;
import com.ydd.zhichat.helper.AvatarHelper;
import com.ydd.zhichat.helper.DialogHelper;
import com.ydd.zhichat.helper.FriendHelper;
import com.ydd.zhichat.ui.base.BaseActivity;
import com.ydd.zhichat.ui.base.CoreManager;
import com.ydd.zhichat.ui.me.redpacket.ChangePayPasswordActivity;
import com.ydd.zhichat.ui.me.redpacket.PayPasswordVerifyDialog;
import com.ydd.zhichat.ui.me.redpacket.alipay.AlipayHelper;
import com.ydd.zhichat.util.Constants;
import com.ydd.zhichat.util.DateUtils;
import com.ydd.zhichat.util.PreferenceUtils;
import com.ydd.zhichat.util.TimeUtils;
import com.ydd.zhichat.util.ToastUtil;
import com.ydd.zhichat.view.HeadMeView;
import com.ydd.zhichat.view.PaySelectAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;

public class VipServiceActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mIvBack;
    private RecyclerView mRvConent;
    private TextView mTvPayTotoal;
    private LinearLayout mLlWeixin;
    private ImageView mIvWeichat;
    private ImageView mIvAli;
    private LinearLayout mLlAlipay;
    private ArrayList<PaySelectBean> mlist = new ArrayList<>();
    private PaySelectAdapter paySelectAdapter;
    private int index_Pay = 0;
    private int index_Price = 0;//0余额支付，1支付宝支付
    private HeadMeView mAvatarImg;
    private TextView mNickNameTv;
    private TextView mVipEndTv;
    private TextView mIdNameTv;
    private TextView mTimeTv;


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_buy) {

            if (index_Price == 1) {
                //AlipayHelper.recharge();
                PaySelectBean PaySelect = mlist.get(index_Pay);
                AlipayHelper.recharge(VipServiceActivity.this, coreManager, PaySelect.getNewPrice());
            } else {
                final Bundle bundle = new Bundle();
                final Intent intent = new Intent(this, VipServiceActivity.class);
                PaySelectBean PaySelect = mlist.get(index_Pay);
                PayPasswordVerifyDialog dialog = new PayPasswordVerifyDialog(this);
                dialog.setAction("购买vip");
                dialog.setMoney(PaySelect.getNewPrice());
                final String finalMoney = PaySelect.getNewPrice();
                final String finalWords = "words";
                final String finalCount = index_Pay + "";
                dialog.setOnInputFinishListener(new PayPasswordVerifyDialog.OnInputFinishListener() {
                    @Override
                    public void onInputFinish(final String password) {

                        //与服务端进行交互
                        sendRed(index_Pay + "", PaySelect.getNewPrice() + "", password);
                        //finish();
                    }
                });
                dialog.show();
            }

        }
    }

    private void checkHasPayPassword() {
        boolean hasPayPassword = PreferenceUtils.getBoolean(this, Constants.IS_PAY_PASSWORD_SET + coreManager.getSelf().getUserId(), true);
        if (!hasPayPassword) {
            Intent intent = new Intent(this, ChangePayPasswordActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Update();
    }

    public void InitData() {
        /*
        if (!ImHelper.checkXmppAuthenticated()) {
            return;
        }
         */

        getUserData();
        mIdNameTv.setText("ID:"+MyApplication.mCoreManager.getSelf().getUserId());

        for (int i = 0; i < 3; i++) {
            PaySelectBean bean = new PaySelectBean();
            switch (i) {
                case 0: {
                    bean.setDes("aaaa");
                    bean.setName("月费");
                    bean.setSelect(true);
                    bean.setNewPrice(MyApplication.mCoreManager.getConfig().vipMoonCost.toString());
                    bean.setOldPrice(MyApplication.mCoreManager.getConfig().vipMoonCost.toString());
                    bean.setRecommond(false);
                    break;
                }
                case 1: {
                    bean.setDes("aaaa");
                    bean.setName("季费");
                    bean.setSelect(false);
                    bean.setNewPrice(MyApplication.mCoreManager.getConfig().vipSeasonCost.toString());
                    bean.setOldPrice(MyApplication.mCoreManager.getConfig().vipSeasonCost.toString());
                    bean.setRecommond(false);
                    break;
                }
                case 2: {
                    bean.setDes("aaaa");
                    bean.setName("年费");
                    bean.setSelect(false);
                    bean.setNewPrice(MyApplication.mCoreManager.getConfig().vipYearCost.toString());
                    bean.setOldPrice(MyApplication.mCoreManager.getConfig().vipYearCost.toString());
                    bean.setRecommond(false);
                    break;
                }
            }

            mlist.add(bean);

        }


        initData();

        mAvatarImg.setVip(MyApplication.mCoreManager.getSelf().getVip());


        /*
        Map<String, String> params = new HashMap<>();
        String time = String.valueOf(TimeUtils.time_current_time());
        params.put("access_token", coreManager.getSelfStatus().accessToken);
        params.put("time", time);

        HttpUtils.get().url(coreManager.getConfig().RECHARGE_GET_VIP)
                .params(params)
                .build()
                .execute(new BaseCallback<String>(String.class) {

                    @Override
                    public void onResponse(ObjectResult<String> result) {
                        //RedPacket redPacket = result.getData();
                        JSONArray arr = JSON.parseArray(result.getData());//esult.getData()
                        if (result.getResultCode() == 1) {
                            for(int i = 0;i<arr.size();i++) {
                                PaySelectBean bean = arr.getObject(i,PaySelectBean.class);
                                mlist.add(bean);
                            }
                            initData();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                        ToastUtil.showNetError(VipServiceActivity.this);
                    }
                });
         */
    }


    public void sendRed(final String type, String money, String payPassword) {
        /*
        if (!ImHelper.checkXmppAuthenticated()) {
            return;
        }

         */
        Map<String, String> params = new HashMap<>();
        params.put("access_token", coreManager.getSelfStatus().accessToken);
        params.put("Type", type);//vip类型
        params.put("level", type);//vip类型
        params.put("paytype", index_Price + "");//支付模式 0余额，1支付宝
        String mAccessToken = coreManager.getSelfStatus().accessToken;
        String mLoginUserId = coreManager.getSelf().getUserId();
        HttpUtils.get().url(coreManager.getConfig().RECHARGE_VIP)
                .params(params)
                .addSecret(payPassword, money)
                .build()
                .execute(new BaseCallback<Void>(Void.class) {

                    @Override
                    public void onResponse(ObjectResult<Void> result) {
                        if (result.getResultCode() == 1) {
                            setResult(200);
                            finish();
                        } else {
                            ToastUtil.showToast(result.getResultMsg());
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                        ToastUtil.showNetError(VipServiceActivity.this);
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyvip);
        getSupportActionBar().hide();
        findViewById(R.id.iv_title_left).setOnClickListener(v -> finish());
        TextView tvTitle = (TextView) findViewById(R.id.tv_title_center);
        tvTitle.setText("会员服务");
        initView();
        InitData();
        checkHasPayPassword();

    }


    private void initData() {

        paySelectAdapter = new PaySelectAdapter(mlist, this);
        paySelectAdapter.setOnPlayClick(new PaySelectAdapter.OnPlayClick() {
            @Override
            public void OnPlayClick(int position) {
                for (int i = 0; i < mlist.size(); i++) {
                    mlist.get(i).setSelect(false);
                }
                mlist.get(position).setSelect(true);
                paySelectAdapter.setNotesList(mlist);
                index_Pay = position;
                //index_Price = mlist.get(position).getNewPrice();
                //tv_pay_totoal
                mTvPayTotoal.setText("需要支付：" + mlist.get(position).getNewPrice());
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            //重写此方法，返回false就可以去除滑动
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mRvConent.setLayoutManager(linearLayoutManager);
        mRvConent.setAdapter(paySelectAdapter);

        mLlAlipay.setOnClickListener(view -> {
            index_Price = 1;
            mIvAli.setImageResource(R.mipmap.ic_select);
            mIvWeichat.setImageResource(R.mipmap.ic_unselect);
        });

        mLlWeixin.setOnClickListener(view -> {
            index_Price = 0;
            mIvWeichat.setImageResource(R.mipmap.ic_select);
            mIvAli.setImageResource(R.mipmap.ic_unselect);
        });

        mLlAlipay.setVisibility(View.GONE);

        findViewById(R.id.button_buy).setOnClickListener(this);
        String loginUserId = MyApplication.mCoreManager.getSelf().getUserId();
        AvatarHelper.getInstance().displayAvatar(MyApplication.mCoreManager.getSelf().getNickName(), loginUserId, mAvatarImg.getHeadImage(), false);
        mNickNameTv.setText(MyApplication.mCoreManager.getSelf().getNickName());
    }

    private void initView() {
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mRvConent = (RecyclerView) findViewById(R.id.rv_conent);
        mTvPayTotoal = (TextView) findViewById(R.id.tv_pay_totoal);
        mLlWeixin = (LinearLayout) findViewById(R.id.ll_weixin);
        mIvWeichat = (ImageView) findViewById(R.id.iv_weichat);
        mLlAlipay = (LinearLayout) findViewById(R.id.ll_alipay);
        mIvAli = (ImageView) findViewById(R.id.iv_ali);
        mAvatarImg = (HeadMeView) findViewById(R.id.avatar_img);
        mNickNameTv = (TextView) findViewById(R.id.nick_name_tv);
        mVipEndTv = (TextView) findViewById(R.id.vip_end_date);
        mIdNameTv = findViewById(R.id.id_name_tv);
    }



    public void getUserData(){
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
                            updateVip(user);
                            if (user.getEndVipTime() <= 0) {
                                mVipEndTv.setText("您还不是神聪享聊的会员，无法使用特权功能！");
                            } else {
                                mVipEndTv.setText("会员有效期 " + DateUtils.time(user.getEndVipTime() / 1000 + ""));
                            }

                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {

                    }
                });
    }

    private void updateVip(User user) {
        if (user.getVip() > 0) {
            int level = user.getVip();
            TextView nick_name_tv = findViewById(R.id.nick_name_tv);
            switch (level) {
                case 1: {
                    nick_name_tv.setTextColor(MyApplication.getContext().getResources().getColor(R.color.color_vip1));
                    String myColorString = coreManager.getConfig().vip1TextColo;
                    if (!myColorString.isEmpty()) {
                        Pattern colorPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
                        Matcher m = colorPattern.matcher(myColorString);
                        boolean isColor = m.matches();
                        if (isColor) {
                            nick_name_tv.setTextColor(Color.parseColor(myColorString));
                        }
                    }
                    break;
                }
                case 2: {
                    nick_name_tv.setTextColor(MyApplication.getContext().getResources().getColor(R.color.color_vip2));
                    String myColorString = coreManager.getConfig().vip2TextColo;
                    if (!myColorString.isEmpty()) {
                        Pattern colorPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
                        Matcher m = colorPattern.matcher(myColorString);
                        boolean isColor = m.matches();
                        if (isColor) {
                            nick_name_tv.setTextColor(Color.parseColor(myColorString));
                        }
                    }
                    break;
                }
                case 3: {
                    nick_name_tv.setTextColor(MyApplication.getContext().getResources().getColor(R.color.color_vip3));
                    String myColorString = coreManager.getConfig().vip3TextColo;
                    if (!myColorString.isEmpty()) {
                        Pattern colorPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
                        Matcher m = colorPattern.matcher(myColorString);
                        boolean isColor = m.matches();
                        if (isColor) {
                            nick_name_tv.setTextColor(Color.parseColor(myColorString));
                        }
                    }
                    break;
                }
                default:
                    nick_name_tv.setTextColor(MyApplication.getContext().getResources().getColor(R.color.color_black));
                    break;
            }
            mAvatarImg.setVip(level);
        } else {
            //findViewById(R.id.vip_mark).setVisibility(View.GONE);
            mAvatarImg.setVip(0);
            mNickNameTv.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_black));
        }
    }

}
