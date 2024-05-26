package com.ydd.zhichat.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;
import com.ydd.zhichat.adapter.FindItemsAdapter;
import com.ydd.zhichat.bean.PaySelectBean;
import com.ydd.zhichat.bean.User;
import com.ydd.zhichat.bean.circle.FindItem;
import com.ydd.zhichat.ui.base.CoreManager;
import com.ydd.zhichat.ui.circle.DiscoverActivity;
import com.ydd.zhichat.ui.me.NearPersonActivity;
import com.ydd.zhichat.ui.me.VipServiceActivity;
import com.ydd.zhichat.ui.me.redpacket.PayPasswordVerifyDialog;
import com.ydd.zhichat.util.DisplayUtil;
import com.example.qrcode.Constant;
import com.example.qrcode.ScannerActivity;
import com.ydd.zhichat.R;
import com.ydd.zhichat.helper.DialogHelper;
import com.ydd.zhichat.ui.base.EasyFragment;
import com.ydd.zhichat.util.Md5Util;
import com.ydd.zhichat.util.ToastUtil;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.ListCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ArrayResult;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.ydd.zhichat.view.DiscoverKeywordsDialog;
import com.ydd.zhichat.view.SelectFileDialog;
import com.ydd.zhichat.view.SelectionFrame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;
import okhttp3.Call;

/**
 * 朋友圈的Fragment
 * Created by Administrator
 */

public class DiscoverFragment extends EasyFragment {

    private TextView mTvTitle;
    private RelativeLayout rel_find;
    private RelativeLayout scanning;
    private RelativeLayout near_person;
    private SwipeRecyclerView mRecyclerView;
    private FindItemsAdapter mAdapter;
    private ArrayList<FindItem> findItems = new ArrayList();
    @Override
    protected int inflateLayoutId() {
        return R.layout.fragment_discover;
    }

    @Override
    protected void onActivityCreated(Bundle savedInstanceState, boolean createView) {
        initActionBar();
        initViews();
        initData();
    }

    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initActionBar() {
//        if (coreManager.getConfig().newUi) {
//            findViewById(R.id.iv_title_left).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    requireActivity().finish();
//                }
//            });
//        } else {
//            findViewById(R.id.iv_title_left).setVisibility(View.GONE);
//        }
        mTvTitle = ((TextView) findViewById(R.id.tv_title_left));
//        mTvTitle.setText(getString(R.string.find));
    }

    public void initViews() {
        rel_find = findViewById(R.id.rel_find);
        scanning = findViewById(R.id.scanning);
        near_person = findViewById(R.id.near_person);
        mRecyclerView = findViewById(R.id.rec_more);
        mAdapter = new FindItemsAdapter(R.layout.item_find, getContext(), findItems);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);

        rel_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCircleStatus();
            }
        });
        scanning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestQrCodeScan(getActivity());
            }
        });
        near_person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), NearPersonActivity.class));
            }
        });

    }

    /**
     * 发起二维码扫描，
     * 仅供MainActivity下属Fragment调用，
     */
    public static void requestQrCodeScan(Activity ctx) {
        Intent intent = new Intent(ctx, ScannerActivity.class);
        // 设置扫码框的宽
        intent.putExtra(Constant.EXTRA_SCANNER_FRAME_WIDTH, DisplayUtil.dip2px(ctx, 250));
        // 设置扫码框的高
        intent.putExtra(Constant.EXTRA_SCANNER_FRAME_HEIGHT, DisplayUtil.dip2px(ctx, 250));
        // 设置扫码框距顶部的位置
        intent.putExtra(Constant.EXTRA_SCANNER_FRAME_TOP_PADDING, DisplayUtil.dip2px(ctx, 100));
        // 可以从相册获取
        intent.putExtra(Constant.EXTRA_IS_ENABLE_SCAN_FROM_PIC, true);
        ctx.startActivityForResult(intent, 888);
    }


    public void initData() {
        getMoreItem();
    }

    /**
     * /general/friendsterWebsiteList?secret=4ab55cbde08e797035056c6482ff246a&time=1582820035&access_token=9a2eb891c5ef4fcda27e95aa4b7ac9b8&page=1&limit=15
     */
    private void getMoreItem() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("access_token", coreManager.getSelfStatus().accessToken);
        params.put("page", 1+"");
        params.put("limit", 15+"");
        HttpUtils.get().url(coreManager.getConfig().FIND_MORE_ITEMS)
                .params(params)
                .build()
                .execute(new ListCallback<FindItem>(FindItem.class) {
                    @Override
                    public void onResponse(ArrayResult<FindItem> result) {
                        DialogHelper.dismissProgressDialog();
                        findItems.clear();
                        if (result.getResultCode() == 1 && result.getData() != null) {
                            findItems.addAll(result.getData());
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                        ToastUtil.showErrorNet(getContext());
                    }
                });
    }

    /***
     * 购买朋友圈
     * @param money
     * @param payPassword
     */
    public void sendRed(String money, String payPassword) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", coreManager.getSelfStatus().accessToken);
        params.put("paytype", "0");//支付模式 0余额，1支付宝
        params.put("payPassword", Md5Util.toMD5(payPassword));//支付模式 0余额，1支付宝
        String mAccessToken = coreManager.getSelfStatus().accessToken;
        String mLoginUserId = coreManager.getSelf().getUserId();
            HttpUtils.get().url(coreManager.getConfig().RECHARGE_Circle)
                .params(params)
                .build()
                .execute(new BaseCallback<Void>(Void.class) {

                    @Override
                    public void onResponse(ObjectResult<Void> result) {
                        if (result.getResultCode() == 1) {
                            //购买成功后...
                            getCircleKey();
                            ToastUtil.showToast(coreManager.getConfig().zhifuStr);
                        } else {
                            ToastUtil.showToast(result.getResultMsg());
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                        ToastUtil.showNetError(DiscoverFragment.this.getContext());
                    }
                });
    }

    public void setCircleKey(String keywords) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", coreManager.getSelfStatus().accessToken);
        params.put("keywords", keywords);
        String mAccessToken = coreManager.getSelfStatus().accessToken;
        String mLoginUserId = coreManager.getSelf().getUserId();
        HttpUtils.get().url(coreManager.getConfig().SET_Circle_Keywords)
                .params(params)
                .build()
                .execute(new BaseCallback<Void>(Void.class) {

                    @Override
                    public void onResponse(ObjectResult<Void> result) {
                        if (result.getResultCode() == 1) {
                            //购买成功后...
                            getCircleKey();
                        } else {
                            ToastUtil.showToast(result.getResultMsg());
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                        ToastUtil.showNetError(DiscoverFragment.this.getContext());
                    }
                });
    }

    public void getCircleKey(){
        Map<String, String> params = new HashMap<>();
        params.put("access_token", coreManager.getSelfStatus().accessToken);
        HttpUtils.get().url(coreManager.getConfig().GET_Circle_Keywords)
                .params(params)
                .build()
                .execute(new BaseCallback<JSONObject>(JSONObject.class) {

                    @Override
                    public void onResponse(ObjectResult<JSONObject> result) {
                        JSONObject jsonObject = result.getData();
                        int status = jsonObject.getInteger("status");
                        if(result.getResultCode() == 1) {
                            if(status == 1) {
                                DiscoverActivity.start(getActivity(),jsonObject.getJSONArray("keyList"),jsonObject.getJSONArray("keyText"));
                            }else{
                                //弹关键字设置窗口
                                DiscoverKeywordsDialog dialog = new DiscoverKeywordsDialog(DiscoverFragment.this.getContext());
                                dialog.setSomething("设置朋友圈关键字", "", new DiscoverKeywordsDialog.OnSelectionFrameClickListener() {
                                    @Override
                                    public void cancelClick() {

                                    }

                                    @Override
                                    public void confirmClick(String edxit) {
                                        setCircleKey(edxit);
                                    }
                                });
                                dialog.show();
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                        ToastUtil.showNetError(DiscoverFragment.this.getContext());
                    }
                });
    }

    public void getCircleStatus(){
        Map<String, String> params = new HashMap<>();
        params.put("access_token", coreManager.getSelfStatus().accessToken);
        HttpUtils.get().url(coreManager.getConfig().GET_Circle_STATUS)
                .params(params)
                .build()
                .execute(new BaseCallback<JSONObject>(JSONObject.class) {

                    @Override
                    public void onResponse(ObjectResult<JSONObject> result) {
                        if (result.getResultCode() == 1) {
                            //已付费
                            //获取关键词:0：未设置关键词,1：已设置关键词
                            int status = result.getData().getInteger("status");
                            if(status == 1) {
                                getCircleKey();
                            }else if(status == 2) {
                                DiscoverActivity.start(getActivity());
                            }else{
                                String money = result.getData().getString("money");
                                SelectionFrame mSF = new SelectionFrame(DiscoverFragment.this.getContext());
                                mSF.setSomething("朋友圈服务", "是否开通朋友圈?", new SelectionFrame.OnSelectionFrameClickListener() {
                                    @Override
                                    public void cancelClick() {

                                    }

                                    @Override
                                    public void confirmClick() {
                                        //购买支付
                                        PayPasswordVerifyDialog dialog = new PayPasswordVerifyDialog(DiscoverFragment.this.getContext());
                                        dialog.setAction("开通朋友圈业务");
                                        dialog.setMoney(money);
                                        final String finalMoney = money;
                                        final String finalWords = "words";
                                        final String finalCount = "0";
                                        dialog.setOnInputFinishListener(new PayPasswordVerifyDialog.OnInputFinishListener() {
                                            @Override
                                            public void onInputFinish(final String password) {
                                                sendRed(finalMoney,password);
                                                //与服务端进行交互
                                                //finish();
                                            }
                                        });
                                        dialog.show();
                                    }
                                });
                                mSF.show();
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                        ToastUtil.showNetError(DiscoverFragment.this.getContext());
                    }
                });
    }


}
