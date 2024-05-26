package com.ydd.zhichat.ui.other;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.signature.StringSignature;
import com.longsh.longshlibrary.PagerSlidingTabStrip;
import com.xuan.xuanhttplibrary.okhttp.callback.ListCallback;
import com.ydd.zhichat.AppConfig;
import com.ydd.zhichat.AppConstant;
import com.ydd.zhichat.MyApplication;
import com.ydd.zhichat.R;
import com.ydd.zhichat.bean.AddAttentionResult;
import com.ydd.zhichat.bean.AgoraInfo;
import com.ydd.zhichat.bean.Area;
import com.ydd.zhichat.bean.Friend;
import com.ydd.zhichat.bean.Label;
import com.ydd.zhichat.bean.Report;
import com.ydd.zhichat.bean.User;
import com.ydd.zhichat.bean.circle.PublicMessage;
import com.ydd.zhichat.bean.message.ChatMessage;
import com.ydd.zhichat.bean.message.NewFriendMessage;
import com.ydd.zhichat.bean.message.XmppMessage;
import com.ydd.zhichat.broadcast.CardcastUiUpdateUtil;
import com.ydd.zhichat.broadcast.MsgBroadcast;
import com.ydd.zhichat.broadcast.OtherBroadcast;
import com.ydd.zhichat.call.CallManager;
import com.ydd.zhichat.call.ImVideoCallActivity;
import com.ydd.zhichat.call.ImVoiceCallActivity;
import com.ydd.zhichat.db.InternationalizationHelper;
import com.ydd.zhichat.db.dao.ChatMessageDao;
import com.ydd.zhichat.db.dao.FriendDao;
import com.ydd.zhichat.db.dao.LabelDao;
import com.ydd.zhichat.db.dao.NewFriendDao;
import com.ydd.zhichat.db.dao.UserAvatarDao;
import com.ydd.zhichat.helper.AvatarHelper;
import com.ydd.zhichat.helper.DialogHelper;
import com.ydd.zhichat.helper.FriendHelper;
import com.ydd.zhichat.helper.UsernameHelper;
import com.ydd.zhichat.ui.MainActivity;
import com.ydd.zhichat.ui.base.BaseActivity;
import com.ydd.zhichat.ui.base.CoreManager;
import com.ydd.zhichat.ui.circle.BusinessCircleActivity;
import com.ydd.zhichat.ui.map.MapActivity;
import com.ydd.zhichat.ui.message.ChatActivity;
import com.ydd.zhichat.ui.message.single.SetChatBackActivity;
import com.ydd.zhichat.ui.message.single.SetRemarkActivity;
import com.ydd.zhichat.ui.other.frament.FileFragment;
import com.ydd.zhichat.ui.tool.SingleImagePreviewActivity;
import com.ydd.zhichat.util.BitmapUtil;
import com.ydd.zhichat.util.Constants;
import com.ydd.zhichat.util.ContextUtil;
import com.ydd.zhichat.util.HtmlUtils;
import com.ydd.zhichat.util.PreferenceUtils;
import com.ydd.zhichat.util.StringUtils;
import com.ydd.zhichat.util.TimeUtils;
import com.ydd.zhichat.util.ToastUtil;
import com.ydd.zhichat.view.BasicInfoWindow;
import com.ydd.zhichat.view.ChatTextClickPpWindow001;
import com.ydd.zhichat.view.HeadMeView;
import com.ydd.zhichat.view.MsgSaveDaysDialog;
import com.ydd.zhichat.view.NoDoubleClickListener;
import com.ydd.zhichat.view.ReportDialog;
import com.ydd.zhichat.view.SelectionFrame;
import com.ydd.zhichat.xmpp.ListenerManager;
import com.ydd.zhichat.xmpp.listener.ChatMessageListener;
import com.ydd.zhichat.xmpp.listener.NewFriendListener;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;
import com.xuan.xuanhttplibrary.okhttp.result.Result;
import com.zyyoona7.popup.EasyPopup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;

/**
 * 基本信息next_step_btn
 */
public class BasicInfoActivity extends BaseActivity implements NewFriendListener {
    public static final String KEY_FROM_ADD_TYPE = "KEY_FROM_ADD_TYPE";
    public static final int FROM_ADD_TYPE_QRCODE = 1;
    public static final int FROM_ADD_TYPE_CARD = 2;
    public static final int FROM_ADD_TYPE_GROUP = 3;
    public static final int FROM_ADD_TYPE_PHONE = 4;
    public static final int FROM_ADD_TYPE_NAME = 5;
    public static final int FROM_ADD_TYPE_OTHER = 6;
    private static final int REQUEST_CODE_SET_REMARK = 475;
    private String fromAddType;

    private String mUserId;
    private String mLoginUserId;
    private boolean isMyInfo = false;
    private User mUser;
    private Friend mFriend;

    private ImageView ivRight;
    private BasicInfoWindow menuWindow;

    private HeadMeView mAvatarImg;
    private TextView tv_remarks;
    private ImageView iv_remarks;
    private LinearLayout ll_nickname;
    private TextView tv_name_basic;
    private TextView tv_communication;
    private TextView tv_number;
    private LinearLayout ll_place;
    private TextView tv_place;
    private TextView photo_tv;
    private LinearLayout photo_rl;
    private TextView userNameText;
    private TextView user_age_str;//年龄
    private TextView user_status_str;//在线时间
    private TextView user_address_str;//地区

    private RelativeLayout mRemarkLayout;
    private TextView tv_setting_name;
    private TextView tv_lable_basic;
    private RelativeLayout rl_describe;
    private TextView tv_describe_basic;
    private TextView birthday_tv;
    private TextView online_tv;
    private RelativeLayout online_rl;
    private RelativeLayout look_location_rl;
    private RelativeLayout erweima;
    ChatTextClickPpWindow001 mChatPpWindow;

    private TextView mNextStepBtn;
    private TextView mPingbiLaBtn;

    /**
     * Todo All NewFriendMessage packetId
     */
    private String addhaoyouid = null;
    private String addblackid = null;
    private String removeblack = null;
    private String deletehaoyou = null;

    private EasyPopup mCirclePop;

    private int isyanzheng = 0;// 该好友是否需要验证
    private PagerSlidingTabStrip mTabLayout;
    private List<String> listTitles = new ArrayList<>();
    private ArrayList<Fragment> fragments = new ArrayList<>();
    RefreshBroadcastReceiver receiver = new RefreshBroadcastReceiver();

    private List<imageClass> imagesList = new ArrayList<>();
    RecyclerView recyclerView;

    public static class imageClass {
        public final String url;
        public final Integer type;

        public imageClass(String url, Integer type) {
            this.url = url;
            this.type = type;
        }
    }


    // 为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            menuWindow.dismiss();
            if (mFriend == null) {
                mFriend = FriendDao.getInstance().getFriend(mLoginUserId, mUserId);
            }
            switch (v.getId()) {
                case R.id.set_remark_nameS:
                    start();
                    break;
                case R.id.add_blacklist:
                    // 加入黑名单
                    showBlacklistDialog(mFriend);
                    break;
                case R.id.remove_blacklist:
                    // 移除黑名单
                    removeBlacklist(mFriend);
                    break;
                case R.id.delete_tv:
                    // 彻底删除
                    showDeleteAllDialog(mFriend);
                    break;
                case R.id.report_tv:
                    ReportDialog mReportDialog = new ReportDialog(BasicInfoActivity.this, false, new ReportDialog.OnReportListItemClickListener() {
                        @Override
                        public void onReportItemClick(Report report) {
                            report(mUserId, report);
                        }
                    });
                    mReportDialog.show();
                    break;
            }
        }
    };


    public static void start(Context ctx, String userId) {
        Intent intent = new Intent(ctx, BasicInfoActivity.class);
        intent.putExtra(AppConstant.EXTRA_USER_ID, userId);
        ctx.startActivity(intent);
    }

    public static void start(Context ctx, String userId, int fromAddType) {
        Intent intent = new Intent(ctx, BasicInfoActivity.class);
        intent.putExtra(AppConstant.EXTRA_USER_ID, userId);
        intent.putExtra(KEY_FROM_ADD_TYPE, String.valueOf(fromAddType));
        ctx.startActivity(intent);
    }

    private void start() {
        String name = "";
        String desc = "";
        if (mUser != null && mUser.getFriends() != null) {
            name = mUser.getFriends().getRemarkName();
            desc = mUser.getFriends().getDescribe();
        }
        SetRemarkActivity.startForResult(BasicInfoActivity.this, mUserId, name, desc, REQUEST_CODE_SET_REMARK);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_info_new_new_ui);
        if (getIntent() != null) {
            mUserId = getIntent().getStringExtra(AppConstant.EXTRA_USER_ID);
            fromAddType = getIntent().getStringExtra(KEY_FROM_ADD_TYPE);
        }

        mLoginUserId = coreManager.getSelf().getUserId();
        if (TextUtils.isEmpty(mUserId)) {
            mUserId = mLoginUserId;
        }
        mFriend = FriendDao.getInstance().getFriend(mLoginUserId, mUserId);
        initActionBar();
        initView();

        initEvent();

        if (mLoginUserId.equals(mUserId)) { // 显示自己资料
            isMyInfo = true;
            loadMyInfoFromDb();
        } else { // 显示其他用户的资料
            isMyInfo = false;
            loadOthersInfoFromNet();
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(OtherBroadcast.SYNC_PAYL);
        intentFilter.addAction(OtherBroadcast.SYNC_PAYL1);
        intentFilter.addAction(OtherBroadcast.SYNC_PAYL2);
        intentFilter.addAction(OtherBroadcast.SYNC_PAYL3);
        registerReceiver(receiver, intentFilter);

        ListenerManager.getInstance().addNewFriendListener(this);


        requestSpace(false);

    }

    private void initEvent() {
        findViewById(R.id.more_handle_btn).setOnClickListener(v -> {
            if (mUser != null) {
                mChatPpWindow = new ChatTextClickPpWindow001(mContext, new ClickListener(mContext, mFriend), mUser.getUserId(), mFriend);
                mChatPpWindow.showAsDropDown(v);
                Activity activity = ContextUtil.findActivity(mContext);
                if (activity != null) {
                    View view = activity.findViewById(android.R.id.content);
                    if (view instanceof FrameLayout) {
                        FrameLayout root = (FrameLayout) view;
                        View content = root.getChildAt(0);
                        new Thread(() -> {
                            try {
                                Bitmap bitmap = Bitmap.createBitmap(content.getWidth(), content.getHeight(), Bitmap.Config.ARGB_8888);
                                Canvas canvas = new Canvas(bitmap);
                                content.draw(canvas);
                                Bitmap blur = BitmapUtil.blur(mContext, bitmap, 1f, 0.5f);
                                bitmap.recycle();
                                view.post(() -> {
                                    if (mChatPpWindow.isShowing()) {
                                        View blurView = new View(mContext);
                                        blurView.setBackground(new BitmapDrawable(mContext.getResources(), blur));
                                        root.addView(blurView, -1, -1);
                                        mChatPpWindow.setOnDismissListener(() -> {
                                            root.removeView(blurView);
                                        });
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }
                }
            }
        });

        //消息免打扰
        findViewById(R.id.mute_voice_btn).setOnClickListener(new offlineNoPushMsg(this));
        //视频通话
        findViewById(R.id.video_call_btn_btn).setOnClickListener(new SendMsgvoice(0));
        //语音通话
        findViewById(R.id.voice_call_btn).setOnClickListener(new SendMsgvoice(1));

        //发消息
        findViewById(R.id.sendMsgLayout).setOnClickListener(new SendMsgListener());


    }

    private void initView() {
        UsernameHelper.initTextView(findViewById(R.id.user_mobile_value), coreManager.getConfig().registerUsername);
        mAvatarImg = (HeadMeView) findViewById(R.id.avatar_img);
        tv_remarks = (TextView) findViewById(R.id.tv_remarks);
        iv_remarks = findViewById(R.id.sexTagIv);
        photo_tv = findViewById(R.id.user_mobile_value);//手机号码
        photo_rl = findViewById(R.id.user_mobile_layout);
        userNameText = findViewById(R.id.userNameText);
        mTabLayout = findViewById(R.id.tablayout);
        mNextStepBtn = findViewById(R.id.addLayout);
        mPingbiLaBtn = findViewById(R.id.pingbiLayout);
        user_age_str = findViewById(R.id.user_age_str);
        user_status_str = findViewById(R.id.user_status_str);
        user_address_str = findViewById(R.id.user_address_str);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(new MsgAdapter());

    }

    private void initActionBar() {
        getSupportActionBar().hide();
        findViewById(R.id.iv_title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView tvTitle = (TextView) findViewById(R.id.tv_title_center);
        tvTitle.setText(InternationalizationHelper.getString("JX_BaseInfo"));

        ivRight = (ImageView) findViewById(R.id.iv_title_right);
        ivRight.setImageResource(R.drawable.title_moress);
        ivRight.setOnClickListener(v -> {
            menuWindow = new BasicInfoWindow(BasicInfoActivity.this, itemsOnClick, mFriend);
            // 显示窗口
            menuWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            // +x右,-x左,+y下,-y上
            // pop向左偏移显示
            menuWindow.showAsDropDown(v,
                    -(menuWindow.getContentView().getMeasuredWidth() - v.getWidth() / 2 - 40),
                    0);
        });
    }

    // 加载自己的信息
    private void loadMyInfoFromDb() {
        mUser = coreManager.getSelf();
        updateUI();
    }

    // 加载好友的信息
    private void loadOthersInfoFromNet() {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", coreManager.getSelfStatus().accessToken);
        params.put("userId", mUserId);

        HttpUtils.get().url(coreManager.getConfig().USER_GET_URL)
                .params(params)
                .build()
                .execute(new BaseCallback<User>(User.class) {

                    @Override
                    public void onResponse(ObjectResult<User> result) {
                        if (Result.checkSuccess(BasicInfoActivity.this, result)) {
                            mUser = result.getData();
                            if (mUser.getUserType() != 2) {// 公众号不做该操作 否则会将公众号的status变为好友
                                // 服务器的状态 与本地状态对比
                                if (FriendHelper.updateFriendRelationship(mLoginUserId, mUser)) {
                                    CardcastUiUpdateUtil.broadcastUpdateUi(mContext);
                                }
                            }
                            updateUI();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showNetError(mContext);
                    }
                });
    }

    private void updateFriendName(User user) {
        if (user != null) {
            Friend friend = FriendDao.getInstance().getFriend(mLoginUserId, mUserId);
            if (friend != null) {
                FriendDao.getInstance().updateNickName(mLoginUserId, mUserId, user.getNickName());
            }
        }
    }

    private void updateUI() {
        if (mUser == null) {
            return;
        }
        if (isFinishing()) {
            return;
        }

        //vip
        if (mFriend != null) {
            if (coreManager.getConfig().isVipCenter) {
                if (mFriend.getVip() > 0) {
                    ImageView vip_mark = findViewById(R.id.vip_mark);
                    vip_mark.setVisibility(View.VISIBLE);
                    int level = mFriend.getVip();
                    if (level == 1) {
                        vip_mark.setImageDrawable(MyApplication.getContext().getResources().getDrawable(R.mipmap.bhm));
                        AvatarHelper.getInstance().displayUrl(CoreManager.requireConfig(mContext).viplevel1ImgUpdate, vip_mark, R.mipmap.bhm);
                    } else if (level == 2) {
                        vip_mark.setImageDrawable(MyApplication.getContext().getResources().getDrawable(R.mipmap.bhl));
                        AvatarHelper.getInstance().displayUrl(CoreManager.requireConfig(mContext).viplevel2ImgUpdate, vip_mark, R.mipmap.bhl);
                    } else if (level == 3) {
                        vip_mark.setImageDrawable(MyApplication.getContext().getResources().getDrawable(R.mipmap.bhn));
                        AvatarHelper.getInstance().displayUrl(CoreManager.requireConfig(mContext).viplevel3ImgUpdate, vip_mark, R.mipmap.bhn);
                    }
                    switch (level) {
                        case 1: {
                            tv_remarks.setTextColor(MyApplication.getContext().getResources().getColor(R.color.color_vip1));
                            String myColorString = CoreManager.requireConfig(mContext).vip1TextColo;
                            if (!myColorString.isEmpty()) {
                                Pattern colorPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
                                Matcher m = colorPattern.matcher(myColorString);
                                boolean isColor = m.matches();
                                if (isColor) {
                                    tv_remarks.setTextColor(Color.parseColor(myColorString));
                                }
                            }
                            break;
                        }
                        case 2: {
                            tv_remarks.setTextColor(MyApplication.getContext().getResources().getColor(R.color.color_vip2));
                            String myColorString = CoreManager.requireConfig(mContext).vip2TextColo;
                            if (!myColorString.isEmpty()) {
                                Pattern colorPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
                                Matcher m = colorPattern.matcher(myColorString);
                                boolean isColor = m.matches();
                                if (isColor) {
                                    tv_remarks.setTextColor(Color.parseColor(myColorString));
                                }
                            }
                            break;
                        }
                        case 3: {
                            tv_remarks.setTextColor(MyApplication.getContext().getResources().getColor(R.color.color_vip3));
                            String myColorString = CoreManager.requireConfig(mContext).vip3TextColo;
                            if (!myColorString.isEmpty()) {
                                Pattern colorPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
                                Matcher m = colorPattern.matcher(myColorString);
                                boolean isColor = m.matches();
                                if (isColor) {
                                    tv_remarks.setTextColor(Color.parseColor(myColorString));
                                }
                            }
                            break;
                        }
                        default:
                            tv_remarks.setTextColor(MyApplication.getContext().getResources().getColor(R.color.color_black));
                            break;
                    }
                    mAvatarImg.setVip(mFriend.getVip());
                }
            } else {
                mAvatarImg.setVip(0);
                tv_remarks.setTextColor(MyApplication.getContext().getResources().getColor(R.color.color_black));
            }

            if (coreManager.getConfig().isUidShop) {
                if (mFriend.getUidIndex() > 0) {
                    findViewById(R.id.uuid_mark).setVisibility(View.VISIBLE);
                    ImageView uuid_mark = findViewById(R.id.uuid_mark);
                    AvatarHelper.getInstance().displayUrl(CoreManager.requireConfig(mContext).lhaoImgUpdate, uuid_mark, R.drawable.l3_1);
                } else {
                    findViewById(R.id.uuid_mark).setVisibility(View.GONE);
                }
            } else {
                findViewById(R.id.uuid_mark).setVisibility(View.GONE);
            }
        }

        if (mUser != null) {
            if (coreManager.getConfig().isVipCenter) {
                if (mUser.getVip() > 0) {
                    ImageView vip_mark = findViewById(R.id.vip_mark);
                    vip_mark.setVisibility(View.VISIBLE);
                    int level = mUser.getVip();
                    if (level == 1) {
                        vip_mark.setImageDrawable(MyApplication.getContext().getResources().getDrawable(R.mipmap.bhm));
                        AvatarHelper.getInstance().displayUrl(CoreManager.requireConfig(mContext).viplevel1ImgUpdate, vip_mark, R.mipmap.bhm);
                    } else if (level == 2) {
                        vip_mark.setImageDrawable(MyApplication.getContext().getResources().getDrawable(R.mipmap.bhl));
                        AvatarHelper.getInstance().displayUrl(CoreManager.requireConfig(mContext).viplevel2ImgUpdate, vip_mark, R.mipmap.bhl);
                    } else if (level == 3) {
                        vip_mark.setImageDrawable(MyApplication.getContext().getResources().getDrawable(R.mipmap.bhn));
                        AvatarHelper.getInstance().displayUrl(CoreManager.requireConfig(mContext).viplevel3ImgUpdate, vip_mark, R.mipmap.bhn);
                    }
                    switch (level) {
                        case 1: {
                            tv_remarks.setTextColor(MyApplication.getContext().getResources().getColor(R.color.color_vip1));
                            String myColorString = CoreManager.requireConfig(mContext).vip1TextColo;
                            if (!myColorString.isEmpty()) {
                                Pattern colorPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
                                Matcher m = colorPattern.matcher(myColorString);
                                boolean isColor = m.matches();
                                if (isColor) {
                                    tv_remarks.setTextColor(Color.parseColor(myColorString));
                                }
                            }
                            break;
                        }
                        case 2: {
                            tv_remarks.setTextColor(MyApplication.getContext().getResources().getColor(R.color.color_vip2));
                            String myColorString = CoreManager.requireConfig(mContext).vip2TextColo;
                            if (!myColorString.isEmpty()) {
                                Pattern colorPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
                                Matcher m = colorPattern.matcher(myColorString);
                                boolean isColor = m.matches();
                                if (isColor) {
                                    tv_remarks.setTextColor(Color.parseColor(myColorString));
                                }
                            }
                            break;
                        }
                        case 3: {
                            tv_remarks.setTextColor(MyApplication.getContext().getResources().getColor(R.color.color_vip3));
                            String myColorString = CoreManager.requireConfig(mContext).vip3TextColo;
                            if (!myColorString.isEmpty()) {
                                Pattern colorPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
                                Matcher m = colorPattern.matcher(myColorString);
                                boolean isColor = m.matches();
                                if (isColor) {
                                    tv_remarks.setTextColor(Color.parseColor(myColorString));
                                }
                            }
                            break;
                        }
                        default:
                            tv_remarks.setTextColor(MyApplication.getContext().getResources().getColor(R.color.color_black));
                            break;
                    }
                    mAvatarImg.setVip(mUser.getVip());
                }
            } else {
                mAvatarImg.setVip(0);
                tv_remarks.setTextColor(MyApplication.getContext().getResources().getColor(R.color.color_black));
            }

            if (coreManager.getConfig().isUidShop) {
                if (mUser.getUidIndex() > 0) {
                    findViewById(R.id.uuid_mark).setVisibility(View.VISIBLE);
                    ImageView uuid_mark = findViewById(R.id.uuid_mark);
                    AvatarHelper.getInstance().displayUrl(CoreManager.requireConfig(mContext).lhaoImgUpdate, uuid_mark, R.drawable.l3_1);
                } else {
                    findViewById(R.id.uuid_mark).setVisibility(View.GONE);
                }
            } else {
                findViewById(R.id.uuid_mark).setVisibility(View.GONE);
            }
        }

        // 更新用户的头像
        AvatarHelper.updateAvatar(mUser.getUserId());
        displayAvatar(mUser.getUserId());

        updateFriendName(mUser);

        iv_remarks.setImageResource(mUser.getSex() == 0 ? R.mipmap.nearby_women : R.mipmap.nearby_man);

        if (!TextUtils.isEmpty(mUser.getPhone())) {
            photo_tv.setText(mUser.getPhone());  //手机号不为空显示手机号
            photo_rl.setVisibility(View.VISIBLE);
        } else photo_rl.setVisibility(View.GONE);

        if (mFriend != null) {
            findViewById(R.id.mute_voice_btn).setActivated(mFriend.getOfflineNoPushMsg() == 1);
            // 备注为空只显示昵称
            if (TextUtils.isEmpty(mFriend.getRemarkName())) {
                tv_remarks.setText(mFriend.getNickName());
                //ll_nickname.setVisibility(View.GONE);
            } else {  // 备注不为空  显示备注  同时也显示昵称
                tv_remarks.setText(mFriend.getRemarkName());
                //ll_nickname.setVisibility(View.VISIBLE);
                //tv_name_basic.setText(mFriend.getNickName());
            }
        } else {
            tv_remarks.setText(mUser.getNickName());
        }

        //我的用户名
        userNameText.setText("@" + mUser.getAccount());
        userNameText.setOnClickListener(v -> {
            String s = StringUtils.replaceSpecialChar(userNameText.getText().toString());
            CharSequence charSequence = HtmlUtils.transform200SpanString(s.replaceAll("\n", "\r\n"), true);
            // 获得剪切板管理者,复制文本内容
            ClipboardManager cmb = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(charSequence);
            ToastUtil.showToast("复制成功");
        });
        user_age_str.setText(TimeUtils.sk_time_s_long_2_str(mUser.getBirthday()));
        String place = Area.getProvinceCityString(mUser.getProvinceId(), mUser.getCityId());
        if (!TextUtils.isEmpty(place)) {
            user_address_str.setVisibility(View.VISIBLE);
            user_address_str.setText(place);
        } else {
            user_address_str.setText("中国");
//            ll_place.setVisibility(View.GONE);
        }


        if (isMyInfo) {
            mNextStepBtn.setVisibility(View.GONE);
            mPingbiLaBtn.setVisibility(View.GONE);
            findViewById(R.id.addLayout01).setVisibility(View.GONE);
            //findViewById(R.id.rn_rl).setVisibility(View.GONE);
            //rl_describe.setVisibility(View.GONE);
        } else {
            mNextStepBtn.setVisibility(View.GONE);
            if (mUser.getFriends() == null) {// 陌生人
                mNextStepBtn.setVisibility(View.VISIBLE);
                //findViewById(R.id.look_bussic_cicle_rl).setVisibility(View.GONE);
                mNextStepBtn.setText(InternationalizationHelper.getString("JX_AddFriend"));
                mNextStepBtn.setOnClickListener(new AddAttentionListener());
            } else if (mUser.getFriends().getBlacklist() == 1) {  //  需显示移除黑名单
                mPingbiLaBtn.setVisibility(View.VISIBLE);
                mPingbiLaBtn.setText(InternationalizationHelper.getString("TO_BLACKLIST"));
                mPingbiLaBtn.setOnClickListener(new RemoveBlacklistListener());
            } else if (mUser.getFriends().getIsBeenBlack() == 1) {//  需显示加入黑名单
                mPingbiLaBtn.setVisibility(View.VISIBLE);
                mPingbiLaBtn.setText(InternationalizationHelper.getString("TO_BLACKLIST"));
                //findViewById(R.id.look_bussic_cicle_rl).setVisibility(View.GONE);
            } else if (mUser.getFriends().getStatus() == 2 || mUser.getFriends().getStatus() == 4) {// 好友
                mNextStepBtn.setVisibility(View.GONE);
                mPingbiLaBtn.setVisibility(View.GONE);
                //findViewById(R.id.look_bussic_cicle_rl).setVisibility(View.VISIBLE);
                mNextStepBtn.setText(InternationalizationHelper.getString("JXUserInfoVC_SendMseeage"));
                mNextStepBtn.setOnClickListener(new SendMsgListener());
            } else {
                mNextStepBtn.setVisibility(View.VISIBLE);
                //findViewById(R.id.look_bussic_cicle_rl).setVisibility(View.GONE);
                mNextStepBtn.setText(InternationalizationHelper.getString("JX_AddFriend"));
                mNextStepBtn.setOnClickListener(new AddAttentionListener());
            }
        }

        if (mUser.getShowLastLoginTime() > 0) {
            user_status_str.setVisibility(View.VISIBLE);
            user_status_str.setText(TimeUtils.getFriendlyTimeDesc(this, mUser.getShowLastLoginTime()));
        } else {
            user_status_str.setVisibility(View.GONE);
        }


        if (!isMyInfo) {
            ViewPager vp_video = findViewById(R.id.viewPager);
            List<ChatMessage> fileList = ChatMessageDao.getInstance().queryChatMessageByType(coreManager.getSelf().getUserId(), mFriend.getUserId(), XmppMessage.TYPE_FILE);
            FileFragment fileFragment = new FileFragment(fileList, mFriend.getUserId());
            List<ChatMessage> fileList1 = ChatMessageDao.getInstance().queryChatMessageByType(coreManager.getSelf().getUserId(), mFriend.getUserId(), XmppMessage.TYPE_LINK);
            FileFragment linkFragment = new FileFragment(fileList1, mFriend.getUserId());
            List<ChatMessage> fileList2 = ChatMessageDao.getInstance().queryChatMessageByType(coreManager.getSelf().getUserId(), mFriend.getUserId(), XmppMessage.TYPE_VIDEO);
            FileFragment videoFragment = new FileFragment(fileList2, mFriend.getUserId());
            List<ChatMessage> fileList3 = ChatMessageDao.getInstance().queryChatMessageByType(coreManager.getSelf().getUserId(), mFriend.getUserId(), XmppMessage.TYPE_IMAGE);
            FileFragment imageFragment = new FileFragment(fileList3, mFriend.getUserId());
            fragments.clear();
            fragments.add(imageFragment);
            fragments.add(videoFragment);
            fragments.add(fileFragment);
            fragments.add(linkFragment);

            listTitles.clear();
            listTitles.add("图片");
            listTitles.add("视频");
            listTitles.add("文件");
            listTitles.add("链接");
            vp_video.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), fragments, listTitles));
            mTabLayout.setViewPager(vp_video);


            DisplayMetrics dm = getResources().getDisplayMetrics();
            // 设置Tab底部选中的指示器Indicator的高度
            mTabLayout.setIndicatorHeight(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2.5f, dm));
            // 设置Tab底部选中的指示器 Indicator的颜色
            mTabLayout.setIndicatorColorResource(R.color.colorPrimary);
            //设置指示器Indicatorin是否跟文本一样宽，默认false
            mTabLayout.setIndicatorinFollowerTv(false);
            //设置小红点提示，item从0开始计算，true为显示，false为隐藏，默认为全部隐藏
            //    tabs.setMsgToast(2, true);
            //设置红点滑动到当前页面自动消失,默认为true
            mTabLayout.setMsgToastPager(true);
            //设置Tab标题文字的颜色
            //tabs.setTextColor(R.color.***);
            // 设置Tab标题文字的大小
            mTabLayout.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, dm));
            // 设置选中的Tab文字的颜色
            mTabLayout.setSelectedTextColorResource(R.color.colorPrimary);
            //设置Tab底部分割线的高度
            mTabLayout.setUnderlineHeight(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, dm));
            //设置Tab底部分割线的颜色
            //tabs.setUnderlineColorResource(R.color.colorGray);
            // 设置点击某个Tab时的背景色,设置为0时取消背景色tabs.setTabBackground(0);
            //        tabs.setTabBackground(R.drawable.bg_tab);
            mTabLayout.setTabBackground(0);
            // 设置Tab是自动填充满屏幕的
            mTabLayout.setShouldExpand(true);

            //设置标签是否需要滑动，多个tab文字不够一屏显示的时候使用，后面会改成内部自动判断-----------------------------------------//todo
            //必须设置，1.0.0不需要设置这行
            //mTabLayout.setTabsScroll(true);

        }


    }

    public void displayAvatar(final String userId) {
        DialogHelper.showDefaulteMessageProgressDialog(this);
        final String mOriginalUrl = AvatarHelper.getAvatarUrl(userId, false);
        if (!TextUtils.isEmpty(mOriginalUrl)) {
            String time = UserAvatarDao.getInstance().getUpdateTime(userId);

            Glide.with(MyApplication.getContext())
                    .load(mOriginalUrl)
                    .placeholder(R.drawable.avatar_normal)
                    .signature(new StringSignature(time))
                    .dontAnimate()
                    .error(R.drawable.avatar_normal)
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            DialogHelper.dismissProgressDialog();
                            mAvatarImg.getHeadImage().setImageDrawable(resource);
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            DialogHelper.dismissProgressDialog();
                            Log.e("zq", "加载原图失败：" + mOriginalUrl);// 该用户未设置头像，网页访问该URL也是404
                            if (mUser.getFriends() != null && !TextUtils.isEmpty(mUser.getFriends().getRemarkName())) {
                                AvatarHelper.getInstance().displayAvatar(mUser.getFriends().getRemarkName(), mUser.getUserId(), mAvatarImg.getHeadImage(), true);
                            } else {
                                AvatarHelper.getInstance().displayAvatar(mUser.getNickName(), mUser.getUserId(), mAvatarImg.getHeadImage(), true);
                            }

                        }
                    });
        } else {
            DialogHelper.dismissProgressDialog();
            Log.e("zq", "未获取到原图地址");// 基本上不会走这里
        }
    }

    @Override
    public void onNewFriendSendStateChange(String toUserId, NewFriendMessage message, int messageState) {
        if (messageState == ChatMessageListener.MESSAGE_SEND_SUCCESS) {
            msgSendSuccess(message, message.getPacketId());
        } else if (messageState == ChatMessageListener.MESSAGE_SEND_FAILED) {
            msgSendFailed(message.getPacketId());
        }
    }

    @Override
    public boolean onNewFriend(NewFriendMessage message) {
        if (!TextUtils.equals(mUserId, mLoginUserId)
                && TextUtils.equals(message.getUserId(), mUserId)) {// 当前对象正在对我进行操作
            loadOthersInfoFromNet();
            return false;
        }
        if (message.getType() == XmppMessage.TYPE_PASS) {// 对方同意好友请求 更新当前界面
            loadOthersInfoFromNet();
        }
        return false;
    }

    // xmpp消息发送成功最终回调到这，
    // 在这里调整ui,
    // 还有存本地数据库，
    public void msgSendSuccess(NewFriendMessage message, String packet) {
        if (addhaoyouid != null && addhaoyouid.equals(packet)) {
            if (isyanzheng == 0) {// 需要验证
                Toast.makeText(getApplicationContext(), InternationalizationHelper.getString("JXAlert_SayHiOK"), Toast.LENGTH_SHORT).show();
                //findViewById(R.id.look_bussic_cicle_rl).setVisibility(View.GONE);

                ChatMessage sayChatMessage = new ChatMessage();
                sayChatMessage.setContent(InternationalizationHelper.getString("JXFriendObject_WaitPass"));
                sayChatMessage.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
                // 各种Dao都是本地数据库的操作，
                // 这个是朋友表里更新最后一条消息，
                FriendDao.getInstance().updateLastChatMessage(mLoginUserId, Friend.ID_NEW_FRIEND_MESSAGE, sayChatMessage);

                // 这个是新的朋友界面用的，等验证什么的都在新的朋友页面，
                NewFriendDao.getInstance().changeNewFriendState(mUser.getUserId(), Friend.STATUS_10);// 朋友状态
                ListenerManager.getInstance().notifyNewFriend(mLoginUserId, message, true);
            } else if (isyanzheng == 1) {
                Toast.makeText(getApplicationContext(), InternationalizationHelper.getString("JX_AddSuccess"), Toast.LENGTH_SHORT).show();
                //findViewById(R.id.look_bussic_cicle_rl).setVisibility(View.VISIBLE);
                mNextStepBtn.setText(InternationalizationHelper.getString("JXUserInfoVC_SendMseeage"));
                mNextStepBtn.setOnClickListener(new SendMsgListener());

                NewFriendDao.getInstance().ascensionNewFriend(message, Friend.STATUS_FRIEND);
                FriendHelper.addFriendExtraOperation(mLoginUserId, mUser.getUserId());// 加好友

                ChatMessage addChatMessage = new ChatMessage();
                addChatMessage.setContent(InternationalizationHelper.getString("JXNearVC_AddFriends") + ":" + mUser.getNickName());
                addChatMessage.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
                FriendDao.getInstance().updateLastChatMessage(mLoginUserId, Friend.ID_NEW_FRIEND_MESSAGE, addChatMessage);

                NewFriendDao.getInstance().changeNewFriendState(mUser.getUserId(), Friend.STATUS_22);//添加了xxx
                FriendDao.getInstance().updateFriendContent(mLoginUserId, mUser.getUserId(), InternationalizationHelper.getString("JXMessageObject_BeFriendAndChat"), XmppMessage.TYPE_TEXT, TimeUtils.sk_time_current_time());
                ListenerManager.getInstance().notifyNewFriend(mLoginUserId, message, true);

                loadOthersInfoFromNet();
                CardcastUiUpdateUtil.broadcastUpdateUi(mContext);
            }
            // 已经是好友了，mFriend不能为空，
            mFriend = FriendDao.getInstance().getFriend(mLoginUserId, mUserId);
        } else if (addblackid != null && addblackid.equals(packet)) {
            Toast.makeText(getApplicationContext(), getString(R.string.add_blacklist_succ), Toast.LENGTH_SHORT).show();
            //findViewById(R.id.look_bussic_cicle_rl).setVisibility(View.GONE);
            mNextStepBtn.setText(InternationalizationHelper.getString("REMOVE"));
            mNextStepBtn.setOnClickListener(new RemoveBlacklistListener());

            // 更新当前持有的Friend对象，
            mFriend.setStatus(Friend.STATUS_BLACKLIST);
            FriendDao.getInstance().updateFriendStatus(message.getOwnerId(), message.getUserId(), mFriend.getStatus());
            FriendHelper.addBlacklistExtraOperation(message.getOwnerId(), message.getUserId());

            ChatMessage addBlackChatMessage = new ChatMessage();
            addBlackChatMessage.setContent(InternationalizationHelper.getString("JXFriendObject_AddedBlackList") + " " + mUser.getNickName());
            addBlackChatMessage.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
            FriendDao.getInstance().updateLastChatMessage(mLoginUserId, Friend.ID_NEW_FRIEND_MESSAGE, addBlackChatMessage);

            NewFriendDao.getInstance().createOrUpdateNewFriend(message);
            NewFriendDao.getInstance().changeNewFriendState(mUser.getUserId(), Friend.STATUS_18);
            ListenerManager.getInstance().notifyNewFriend(mLoginUserId, message, true);

            CardcastUiUpdateUtil.broadcastUpdateUi(mContext);

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (removeblack != null && removeblack.equals(packet)) {
            Toast.makeText(getApplicationContext(), InternationalizationHelper.getString("REMOVE_BLACKLIST"), Toast.LENGTH_SHORT).show();
            //findViewById(R.id.look_bussic_cicle_rl).setVisibility(View.VISIBLE);
            mNextStepBtn.setText(InternationalizationHelper.getString("JXUserInfoVC_SendMseeage"));
            mNextStepBtn.setOnClickListener(new SendMsgListener());

            // 更新当前持有的Friend对象，
            if (mFriend != null) {
                mFriend.setStatus(Friend.STATUS_FRIEND);
            }
            NewFriendDao.getInstance().ascensionNewFriend(message, Friend.STATUS_FRIEND);
            FriendHelper.beAddFriendExtraOperation(message.getOwnerId(), message.getUserId());

            ChatMessage removeChatMessage = new ChatMessage();
            removeChatMessage.setContent(coreManager.getSelf().getNickName() + InternationalizationHelper.getString("REMOVE"));
            removeChatMessage.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
            FriendDao.getInstance().updateLastChatMessage(mLoginUserId, Friend.ID_NEW_FRIEND_MESSAGE, removeChatMessage);

            NewFriendDao.getInstance().createOrUpdateNewFriend(message);
            NewFriendDao.getInstance().changeNewFriendState(message.getUserId(), Friend.STATUS_24);
            ListenerManager.getInstance().notifyNewFriend(mLoginUserId, message, true);

            CardcastUiUpdateUtil.broadcastUpdateUi(mContext);

            loadOthersInfoFromNet();
        } else if (deletehaoyou != null && deletehaoyou.equals(packet)) {
            Toast.makeText(getApplicationContext(), InternationalizationHelper.getString("JXAlert_DeleteOK"), Toast.LENGTH_SHORT).show();

            FriendHelper.removeAttentionOrFriend(mLoginUserId, message.getUserId());

            ChatMessage deleteChatMessage = new ChatMessage();
            deleteChatMessage.setContent(InternationalizationHelper.getString("JXAlert_DeleteFirend") + " " + mUser.getNickName());
            deleteChatMessage.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
            FriendDao.getInstance().updateLastChatMessage(mLoginUserId, Friend.ID_NEW_FRIEND_MESSAGE, deleteChatMessage);

            NewFriendDao.getInstance().createOrUpdateNewFriend(message);
            NewFriendDao.getInstance().changeNewFriendState(mUser.getUserId(), Friend.STATUS_16);
            ListenerManager.getInstance().notifyNewFriend(mLoginUserId, message, true);

            CardcastUiUpdateUtil.broadcastUpdateUi(mContext);

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void msgSendFailed(String packet) {
        DialogHelper.dismissProgressDialog();
        if (packet.equals(addhaoyouid)) {
            Toast.makeText(this, R.string.tip_hello_failed, Toast.LENGTH_SHORT).show();
        } else if (packet.equals(addblackid)) {
            Toast.makeText(this, R.string.tip_put_black_failed, Toast.LENGTH_SHORT).show();
        } else if (packet.equals(removeblack)) {
            Toast.makeText(this, R.string.tip_remove_black_failed, Toast.LENGTH_SHORT).show();
        } else if (packet.equals(deletehaoyou)) {
            Toast.makeText(this, R.string.tip_remove_friend_failed, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Todo NextStep && ivRight Operating
     */

    // 点击加好友就调用这个方法，
    private void doAddAttention() {
        if (mUser == null) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("access_token", coreManager.getSelfStatus().accessToken);
        params.put("toUserId", mUser.getUserId());
        if (TextUtils.isEmpty(fromAddType)) {
            // 默认就是其他方式，
            fromAddType = String.valueOf(FROM_ADD_TYPE_OTHER);
        }
        params.put("fromAddType", fromAddType);
        DialogHelper.showDefaulteMessageProgressDialog(this);

        // 首先是调接口，
        HttpUtils.get().url(coreManager.getConfig().FRIENDS_ATTENTION_ADD)
                .params(params)
                .build()
                .execute(new BaseCallback<AddAttentionResult>(AddAttentionResult.class) {

                    @Override
                    public void onResponse(ObjectResult<AddAttentionResult> result) {
                        DialogHelper.dismissProgressDialog();
                        if (result.getResultCode() == 1 && result.getData() != null) {
                            if (result.getData().getType() == 1 || result.getData().getType() == 3) {
                                isyanzheng = 0;// 需要验证
                                // 需要验证就发送打招呼的消息，
                                doSayHello(InternationalizationHelper.getString("JXUserInfoVC_Hello"));
                            } else if (result.getData().getType() == 2 || result.getData().getType() == 4) {// 已经是好友了
                                isyanzheng = 1;// 不需要验证
                                NewFriendMessage message = NewFriendMessage.createWillSendMessage(
                                        coreManager.getSelf(), XmppMessage.TYPE_FRIEND, null, mUser);
                                NewFriendDao.getInstance().createOrUpdateNewFriend(message);
                                // 不需要验证的话直接加上，就发个xmpp消息，
                                // 这里最终调用smack的方法发送xmpp消息，
                                coreManager.sendNewFriendMessage(mUser.getUserId(), message);

                                addhaoyouid = message.getPacketId();
                            } else if (result.getData().getType() == 5) {
                                ToastUtil.showToast(mContext, R.string.add_attention_failed);
                            }
                        } else {
                            Toast.makeText(BasicInfoActivity.this, result.getResultMsg() + "", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                        Toast.makeText(mContext, R.string.tip_hello_failed, Toast.LENGTH_SHORT).show();
                        ToastUtil.showErrorNet(mContext);
                    }
                });
    }

    // 打招呼
    private void doSayHello(String text) {
        if (TextUtils.isEmpty(text)) {
            text = InternationalizationHelper.getString("HEY-HELLO");
        }
        NewFriendMessage message = NewFriendMessage.createWillSendMessage(coreManager.getSelf(),
                XmppMessage.TYPE_SAYHELLO, text, mUser);
        NewFriendDao.getInstance().createOrUpdateNewFriend(message);
        // 这里最终调用smack的发送消息，
        coreManager.sendNewFriendMessage(mUser.getUserId(), message);

        addhaoyouid = message.getPacketId();

        // 发送打招呼的消息
        ChatMessage sayMessage = new ChatMessage();
        sayMessage.setFromUserId(coreManager.getSelf().getUserId());
        sayMessage.setFromUserName(coreManager.getSelf().getNickName());
        sayMessage.setContent(InternationalizationHelper.getString("HEY-HELLO"));
        sayMessage.setType(XmppMessage.TYPE_TEXT); //文本类型
        sayMessage.setMySend(true);
        sayMessage.setMessageState(ChatMessageListener.MESSAGE_SEND_SUCCESS);
        sayMessage.setPacketId(UUID.randomUUID().toString().replaceAll("-", ""));
        sayMessage.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
        ChatMessageDao.getInstance().saveNewSingleChatMessage(message.getOwnerId(), message.getUserId(), sayMessage);
    }

    // 加入、移除黑名单
    private void showBlacklistDialog(final Friend friend) {
/*
        if (friend.getStatus() == Friend.STATUS_BLACKLIST) {
            removeBlacklist(friend);
        } else if (friend.getStatus() == Friend.STATUS_ATTENTION || friend.getStatus() == Friend.STATUS_FRIEND) {
            addBlacklist(friend);
        }
*/
        SelectionFrame mSF = new SelectionFrame(this);
        mSF.setSomething(getString(R.string.add_black_list), getString(R.string.sure_add_friend_blacklist), new SelectionFrame.OnSelectionFrameClickListener() {
            @Override
            public void cancelClick() {

            }

            @Override
            public void confirmClick() {
                addBlacklist(friend);
            }
        });
        mSF.show();
    }

    private void addBlacklist(final Friend friend) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", coreManager.getSelfStatus().accessToken);
        params.put("toUserId", friend.getUserId());
        DialogHelper.showDefaulteMessageProgressDialog(this);

        HttpUtils.get().url(coreManager.getConfig().FRIENDS_BLACKLIST_ADD)
                .params(params)
                .build()
                .execute(new BaseCallback<Void>(Void.class) {

                    @Override
                    public void onResponse(ObjectResult<Void> result) {
                        DialogHelper.dismissProgressDialog();
                        if (result.getResultCode() == 1) {
                            if (friend.getStatus() == Friend.STATUS_FRIEND) {
                                NewFriendMessage message = NewFriendMessage.createWillSendMessage(
                                        coreManager.getSelf(), XmppMessage.TYPE_BLACK, null, friend);
                                coreManager.sendNewFriendMessage(friend.getUserId(), message);// 加入黑名单

                                // 记录加入黑名单消息packet，如收到消息回执，在做后续操作
                                addblackid = message.getPacketId();
                            }
                        } else if (!TextUtils.isEmpty(result.getResultMsg())) {
                            ToastUtil.showToast(mContext, result.getResultMsg());
                        } else {
                            ToastUtil.showToast(mContext, R.string.tip_server_error);
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                        Toast.makeText(mContext, getString(R.string.add_blacklist_fail), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeBlacklist(final Friend friend) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", coreManager.getSelfStatus().accessToken);
        params.put("toUserId", mUser.getUserId());
        DialogHelper.showDefaulteMessageProgressDialog(this);

        HttpUtils.get().url(coreManager.getConfig().FRIENDS_BLACKLIST_DELETE)
                .params(params)
                .build()
                .execute(new BaseCallback<Void>(Void.class) {

                    @Override
                    public void onResponse(ObjectResult<Void> result) {
                        DialogHelper.dismissProgressDialog();
                        if (result.getResultCode() == 1) {
                            NewFriendMessage message = NewFriendMessage.createWillSendMessage(
                                    coreManager.getSelf(), XmppMessage.TYPE_REFUSED, null, friend);
                            coreManager.sendNewFriendMessage(friend.getUserId(), message);// 移除黑名单

                            // 记录移除黑名单消息packet，如收到消息回执，在做后续操作
                            removeblack = message.getPacketId();
                        } else if (!TextUtils.isEmpty(result.getResultMsg())) {
                            ToastUtil.showToast(mContext, result.getResultMsg());
                        } else {
                            ToastUtil.showToast(mContext, R.string.tip_server_error);
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                        Toast.makeText(mContext, R.string.tip_remove_black_failed, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 删除好友
    private void showDeleteAllDialog(final Friend friend) {
        if (friend.getStatus() == Friend.STATUS_UNKNOW) {// 陌生人
            return;
        }
        SelectionFrame mSF = new SelectionFrame(this);
        mSF.setSomething(getString(R.string.delete_friend), getString(R.string.sure_delete_friend), new SelectionFrame.OnSelectionFrameClickListener() {
            @Override
            public void cancelClick() {

            }

            @Override
            public void confirmClick() {
                deleteFriend(friend, 1);
            }
        });
        mSF.show();
    }

    private void deleteFriend(final Friend friend, final int type) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", coreManager.getSelfStatus().accessToken);
        params.put("toUserId", friend.getUserId());
        DialogHelper.showDefaulteMessageProgressDialog(this);

        HttpUtils.get().url(coreManager.getConfig().FRIENDS_DELETE)
                .params(params)
                .build()
                .execute(new BaseCallback<Void>(Void.class) {

                    @Override
                    public void onResponse(ObjectResult<Void> result) {
                        DialogHelper.dismissProgressDialog();
                        if (Result.checkSuccess(mContext, result)) {
                            NewFriendMessage message = NewFriendMessage.createWillSendMessage(
                                    coreManager.getSelf(), XmppMessage.TYPE_DELALL, null, friend);
                            coreManager.sendNewFriendMessage(mUser.getUserId(), message); // 删除好友

                            // 记录删除好友消息packet，如收到消息回执，在做后续操作
                            deletehaoyou = message.getPacketId();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                        Toast.makeText(mContext, R.string.tip_remove_friend_failed, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void report(String userId, Report report) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", coreManager.getSelfStatus().accessToken);
        params.put("toUserId", userId);
        params.put("reason", String.valueOf(report.getReportId()));
        DialogHelper.showDefaulteMessageProgressDialog(this);

        HttpUtils.get().url(coreManager.getConfig().USER_REPORT)
                .params(params)
                .build()
                .execute(new BaseCallback<Void>(Void.class) {

                    @Override
                    public void onResponse(ObjectResult<Void> result) {
                        DialogHelper.dismissProgressDialog();
                        if (result.getResultCode() == 1) {
                            ToastUtil.showToast(BasicInfoActivity.this, R.string.report_success);
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    protected void onDestroy() {
        super.onDestroy();
        ListenerManager.getInstance().removeNewFriendListener(this);
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
            // 无论如何不应该在destroy崩溃，
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_SET_REMARK) {
            loadOthersInfoFromNet();
        }
    }

    // 加关注
    private class AddAttentionListener extends NoDoubleClickListener {
        @Override
        public void onNoDoubleClick(View view) {
            doAddAttention();
        }
    }

    // 移除黑名单  直接变为好友  不需要验证
    private class RemoveBlacklistListener extends NoDoubleClickListener {
        @Override
        public void onNoDoubleClick(View view) {
            Friend friend = FriendDao.getInstance().getFriend(mLoginUserId, mUser.getUserId());// 更新好友的状态
            removeBlacklist(friend);
        }
    }

    // 发消息
    private class SendMsgListener extends NoDoubleClickListener {
        @Override
        public void onNoDoubleClick(View view) {
            Friend friend = FriendDao.getInstance().getFriend(mLoginUserId, mUser.getUserId());
            MsgBroadcast.broadcastMsgUiUpdate(BasicInfoActivity.this);
            MsgBroadcast.broadcastMsgNumReset(BasicInfoActivity.this);

            Intent intent = new Intent(mContext, ChatActivity.class);
            intent.putExtra(ChatActivity.FRIEND, friend);
            startActivity(intent);
        }
    }


    // 发语音/视频
    private class SendMsgvoice extends NoDoubleClickListener {

        final int type;

        public SendMsgvoice(int type) {
            this.type = type;
        }

        @Override
        public void onNoDoubleClick(View view) {
            Friend friend = FriendDao.getInstance().getFriend(mLoginUserId, mUser.getUserId());
            MsgBroadcast.broadcastMsgUiUpdate(BasicInfoActivity.this);
            MsgBroadcast.broadcastMsgNumReset(BasicInfoActivity.this);
            //

            call(type == 1 ? CallManager.TYPE_CALL_AUDIO : CallManager.TYPE_CALL_VEDIO,
                    mLoginUserId,
                    mFriend.getUserId(),
                    coreManager.getSelf().getUserId(),
                    mFriend.getNickName(),
                    CallManager.CALL, "");
        }

        public void call(int type, String formUid, String toUid, String myName, String friendName, int callOrReceive, String channel) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("access_token", coreManager.getSelfStatus().accessToken);
            if (callOrReceive == CallManager.RECEIVE_CALL) {
                params.put("channel", channel);
            }
            Log.e("hm----CALL", coreManager.getConfig().CALL);
            HttpUtils.get().url(coreManager.getConfig().CALL)
                    .params(params)
                    .build()
                    .execute(new BaseCallback<AgoraInfo>(AgoraInfo.class) {

                        @Override
                        public void onResponse(ObjectResult<AgoraInfo> result) {
                            if (result.getData() != null) {
                                AgoraInfo agoraInfo = result.getData();

                                String ch = "";
                                if (callOrReceive == CallManager.CALL) {
                                    ch = agoraInfo.getChannel();
                                } else {
                                    ch = channel;
                                }
                                if (type == CallManager.TYPE_CALL_AUDIO) {
                                    callAudio(ch, agoraInfo.getAppId(), agoraInfo.getOwnToken(), formUid, toUid, myName, friendName, callOrReceive);
                                } else {
                                    callVideo(ch, agoraInfo.getAppId(), agoraInfo.getOwnToken(), formUid, toUid, myName, friendName, callOrReceive);
                                }
                            } else {
                                Toast.makeText(mContext, result.getResultMsg(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Call call, Exception e) {

                        }
                    });

        }

        private void callAudio(String channel, String appid, String token, String formUid, String toUid, String myName, String friendName, int callOrReceive) {
            ImVoiceCallActivity.Companion.start(mContext,
                    formUid,
                    toUid,
                    myName,
                    friendName,
                    channel,
                    appid,
                    token,
                    callOrReceive);
        }

        private void callVideo(String channel, String appid, String token, String formUid, String toUid, String myName, String friendName, int callOrReceive) {
            ImVideoCallActivity.Companion.start(mContext,
                    formUid,
                    toUid,
                    myName,
                    friendName,
                    channel,
                    appid,
                    token,
                    callOrReceive);
        }
    }


    //消息免打扰
    private class offlineNoPushMsg extends NoDoubleClickListener {

        final Activity activity;

        private offlineNoPushMsg(Activity activity) {
            this.activity = activity;
        }


        @Override
        public void onNoDoubleClick(View view) {
            Friend friend = FriendDao.getInstance().getFriend(mLoginUserId, mUser.getUserId());
            MsgBroadcast.broadcastMsgUiUpdate(BasicInfoActivity.this);
            MsgBroadcast.broadcastMsgNumReset(BasicInfoActivity.this);
            updateTopChatStatus(mFriend, friend.getOfflineNoPushMsg() != 1);
        }

        private void updateTopChatStatus(Friend friend,boolean isChecked) {
            DialogHelper.showDefaulteMessageProgressDialog(activity);

            Map<String, String> params = new HashMap<>();
            params.put("access_token", CoreManager.requireSelfStatus(MyApplication.getContext()).accessToken);
            params.put("userId", mLoginUserId);
            if (friend.getRoomFlag() == 0) {
                params.put("toUserId", friend.getUserId());
            } else {
                params.put("roomId", friend.getRoomId());
            }
            if (friend.getRoomFlag() == 0) {
                params.put("type", String.valueOf(2));
            } else {
                params.put("type", String.valueOf(0));
            }
            params.put("type", String.valueOf(0));

            /*
            params.put("access_token", coreManager.getSelfStatus().accessToken);
            params.put("userId", mLoginUserId);
            params.put("toUserId", mFriend.getUserId());
            params.put("type", String.valueOf(type));
            params.put("offlineNoPushMsg", isChecked ? String.valueOf(1) : String.valueOf(0));
             */

            params.put("offlineNoPushMsg", isChecked ? String.valueOf(1) : String.valueOf(0));

            String url;
            if (friend.getRoomFlag() == 0) {
                url = CoreManager.requireConfig(MyApplication.getContext()).FRIENDS_NOPULL_MSG;
            } else {
                url = CoreManager.requireConfig(MyApplication.getContext()).ROOM_DISTURB;
            }
            HttpUtils.get().url(url)
                    .params(params)
                    .build()
                    .execute(new BaseCallback<Void>(Void.class) {

                        @Override
                        public void onResponse(ObjectResult<Void> result) {
                            DialogHelper.dismissProgressDialog();
                            if (result.getResultCode() == 1) {
                                FriendDao.getInstance().updateOfflineNoPushMsgStatus(mFriend.getUserId(), isChecked ? 1 : 0);
                                Intent dads = new Intent(com.ydd.zhichat.broadcast.OtherBroadcast.SYNC_PAYL2);
                                dads.putExtra("isChecked", isChecked);
                                sendBroadcast(dads);// 刷新个人信息
                                activity.findViewById(R.id.mute_voice_btn).setActivated(isChecked ? true : false);
                                PreferenceUtils.putInt(mContext, Constants.MESSAGE_READ_FIRE_2 + mFriend.getUserId() + mLoginUserId, isChecked ? 1 : 0);
                            }
                        }

                        @Override
                        public void onError(Call call, Exception e) {
                            DialogHelper.dismissProgressDialog();
                        }
                    });
        }
    }


    class MyPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mfragmentList;
        private List<String> listTitles;

        public MyPagerAdapter(FragmentManager fm, List<Fragment> fragmentList, List<String> list) {
            super(fm);
            this.mfragmentList = fragmentList;
            this.listTitles = list;
        }

        @Override
        public Fragment getItem(int position) {
            return mfragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return listTitles.get(position);
        }

        @Override
        public int getCount() {
            return mfragmentList.size();
        }
    }

    //=======================================================================


    public class ClickListener implements View.OnClickListener {

        private final Friend mFriend;
        private final Context mContext;

        public ClickListener(Context context, Friend mFriend) {
            this.mFriend = mFriend;
            mContext = context;
        }

        MsgSaveDaysDialog.OnMsgSaveDaysDialogClickListener onMsgSaveDaysDialogClickListener = new MsgSaveDaysDialog.OnMsgSaveDaysDialogClickListener() {
            @Override
            public void tv1Click() {
                updateChatRecordTimeOut(-1);
            }

            @Override
            public void tv2Click() {
                updateChatRecordTimeOut(0.04);
                // updateChatRecordTimeOut(0.00347); // 五分钟过期
            }

            @Override
            public void tv3Click() {
                updateChatRecordTimeOut(1);
            }

            @Override
            public void tv4Click() {
                updateChatRecordTimeOut(7);
            }

            @Override
            public void tv5Click() {
                updateChatRecordTimeOut(30);
            }

            @Override
            public void tv6Click() {
                updateChatRecordTimeOut(90);
            }

            @Override
            public void tv7Click() {
                updateChatRecordTimeOut(365);
            }
        };


        @Override
        public void onClick(View v) {
            mChatPpWindow.dismiss();
            switch (v.getId()) {
                case R.id.item_chat_copy_tv://阅后即焚
                    int isReadDel = PreferenceUtils.getInt(this.mContext, Constants.MESSAGE_READ_FIRE + mFriend.getUserId() + CoreManager.requireSelf(mContext).getUserId(), 0);
                    updateDisturbStatus(1, isReadDel == 1 ? false : true);
                    break;
                case R.id.item_chat_relay_tv://消息过期自动销毁
                    MsgSaveDaysDialog msgSaveDaysDialog = new MsgSaveDaysDialog(this.mContext, onMsgSaveDaysDialogClickListener);
                    msgSaveDaysDialog.show();
                    break;
                case R.id.item_chat_collection_tv://清空聊天记录
                    clean(false);
                    break;
                case R.id.item_chat_replay_tv: {//置顶聊天
                    int isReadDeld = PreferenceUtils.getInt(this.mContext, Constants.MESSAGE_READ_FIRE_1 + mFriend.getUserId() + CoreManager.requireSelf(mContext).getUserId(), 0);
                    SelectionFrame mSF = new SelectionFrame(this.mContext);
                    mSF.setSomething("置顶聊天", isReadDeld == 1 ? "是否关置顶聊天?" : "是否开启置顶聊天?", new SelectionFrame.OnSelectionFrameClickListener() {
                        @Override
                        public void cancelClick() {

                        }

                        @Override
                        public void confirmClick() {
                            updateDisturbStatus(2, isReadDeld == 1 ? false : true);
                        }
                    });
                    mSF.show();
                }
                break;
                case R.id.item_chat_more_select://清空双方聊天记录
                    clean(true);
                    break;
                case R.id.item_chat_edit: {//消息免打扰
                    int isReadDelda = PreferenceUtils.getInt(this.mContext, Constants.MESSAGE_READ_FIRE_2 + mFriend.getUserId() + CoreManager.requireSelf(mContext).getUserId(), 0);
                    SelectionFrame mSF = new SelectionFrame(this.mContext);
                    mSF.setSomething("免打扰", isReadDelda == 1 ? "是否关闭免打扰?" : "是否开启免打扰?", new SelectionFrame.OnSelectionFrameClickListener() {
                        @Override
                        public void cancelClick() {

                        }

                        @Override
                        public void confirmClick() {
                            updateDisturbStatus(0, isReadDelda == 1 ? false : true);
                        }
                    });
                    mSF.show();
                }
                break;
                case R.id.collection_other://聊天背景
                    Intent intentBackground = new Intent(this.mContext, SetChatBackActivity.class);
                    intentBackground.putExtra(AppConstant.EXTRA_USER_ID, mFriend.getUserId());
                    startActivity(intentBackground);
                    break;
            }

        }

        // 更新消息免打扰状态
        private void updateDisturbStatus(final int type, final boolean isChecked) {
            Map<String, String> params = new HashMap<>();
            params.put("access_token", coreManager.getSelfStatus().accessToken);
            params.put("userId", mLoginUserId);
            params.put("toUserId", mFriend.getUserId());
            params.put("type", String.valueOf(type));
            params.put("offlineNoPushMsg", isChecked ? String.valueOf(1) : String.valueOf(0));
            DialogHelper.showDefaulteMessageProgressDialog(this.mContext);

            HttpUtils.get().url(coreManager.getConfig().FRIENDS_NOPULL_MSG)
                    .params(params)
                    .build()
                    .execute(new BaseCallback<Void>(Void.class) {

                        @Override
                        public void onResponse(ObjectResult<Void> result) {
                            DialogHelper.dismissProgressDialog();
                            if (result.getResultCode() == 1) {
                                if (type == 0) {// 消息免打扰
                                    PreferenceUtils.putInt(mContext, Constants.MESSAGE_READ_FIRE_2 + mFriend.getUserId() + mLoginUserId, isChecked ? 1 : 0);
                                    FriendDao.getInstance().updateOfflineNoPushMsgStatus(mFriend.getUserId(), isChecked ? 1 : 0);
                                    Intent dads = new Intent(com.ydd.zhichat.broadcast.OtherBroadcast.SYNC_PAYL2);
                                    dads.putExtra("isChecked", isChecked);
                                    sendBroadcast(dads);// 刷新个人信息
                                } else if (type == 1) {// 阅后即焚
                                    PreferenceUtils.putInt(mContext, Constants.MESSAGE_READ_FIRE + mFriend.getUserId() + mLoginUserId, isChecked ? 1 : 0);
                                    if (isChecked) {
                                        ToastUtil.showToast(BasicInfoActivity.this, R.string.tip_status_burn);
                                    }
                                    Intent dads = new Intent(com.ydd.zhichat.broadcast.OtherBroadcast.SYNC_PAYL1);
                                    dads.putExtra("isChecked", isChecked);
                                    sendBroadcast(dads);// 刷新个人信息
                                } else {// 置顶聊天
                                    PreferenceUtils.putInt(mContext, Constants.MESSAGE_READ_FIRE_1 + mFriend.getUserId() + mLoginUserId, isChecked ? 1 : 0);
                                    if (isChecked) {
                                        FriendDao.getInstance().updateTopFriend(mFriend.getUserId(), mFriend.getTimeSend());
                                    } else {
                                        FriendDao.getInstance().resetTopFriend(mFriend.getUserId());
                                    }
                                    Intent dads = new Intent(com.ydd.zhichat.broadcast.OtherBroadcast.SYNC_PAYL3);
                                    dads.putExtra("isChecked", isChecked);
                                    sendBroadcast(dads);// 刷新个人信息
                                }
                            } else {
                                Toast.makeText(BasicInfoActivity.this, R.string.tip_edit_failed, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Call call, Exception e) {
                            DialogHelper.dismissProgressDialog();
                            ToastUtil.showNetError(BasicInfoActivity.this);
                        }
                    });
        }

        // 服务器上与该人的聊天记录也需要删除
        private void emptyServerMessage() {
            Map<String, String> params = new HashMap<>();
            params.put("access_token", coreManager.getSelfStatus().accessToken);
            params.put("type", String.valueOf(0));// 0 清空单人 1 清空所有
            params.put("toUserId", mUserId);

            HttpUtils.get().url(coreManager.getConfig().EMPTY_SERVER_MESSAGE)
                    .params(params)
                    .build()
                    .execute(new BaseCallback<Void>(Void.class) {

                        @Override
                        public void onResponse(ObjectResult<Void> result) {

                        }

                        @Override
                        public void onError(Call call, Exception e) {

                        }
                    });
        }

        private void clean(boolean isSync) {
            String tittle = isSync ? getString(R.string.sync_chat_history_clean) : getString(R.string.clean_chat_history);
            String tip = isSync ? getString(R.string.tip_sync_chat_history_clean) : getString(R.string.tip_confirm_clean_history);

            SelectionFrame selectionFrame = new SelectionFrame(mContext);
            selectionFrame.setSomething(tittle, tip, new SelectionFrame.OnSelectionFrameClickListener() {
                @Override
                public void cancelClick() {

                }

                @Override
                public void confirmClick() {
                    if (isSync) {
                        // 发送一条双向清除的消息给对方，对方收到消息后也将本地消息删除
                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setFromUserId(mLoginUserId);
                        chatMessage.setFromUserName(coreManager.getSelf().getNickName());
                        chatMessage.setToUserId(mUserId);
                        chatMessage.setType(XmppMessage.TYPE_SYNC_CLEAN_CHAT_HISTORY);
                        chatMessage.setPacketId(UUID.randomUUID().toString().replaceAll("-", ""));
                        chatMessage.setDoubleTimeSend(TimeUtils.sk_time_current_time_double());
                        coreManager.sendChatMessage(mUserId, chatMessage);
                    }
                    emptyServerMessage();

                    FriendDao.getInstance().resetFriendMessage(mLoginUserId, mUserId);
                    ChatMessageDao.getInstance().deleteMessageTable(mLoginUserId, mUserId);
                    sendBroadcast(new Intent(Constants.CHAT_HISTORY_EMPTY));// 清空聊天界面
                    MsgBroadcast.broadcastMsgUiUpdate(mContext);
                    Toast.makeText(BasicInfoActivity.this, getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
                }
            });
            selectionFrame.show();
        }

        // 更新消息保存天数
        private void updateChatRecordTimeOut(final double outTime) {
            Map<String, String> params = new HashMap<>();
            params.put("access_token", coreManager.getSelfStatus().accessToken);
            params.put("toUserId", mUserId);
            params.put("chatRecordTimeOut", String.valueOf(outTime));

            HttpUtils.get().url(coreManager.getConfig().FRIENDS_UPDATE)
                    .params(params)
                    .build()
                    .execute(new BaseCallback<Void>(Void.class) {

                        @Override
                        public void onResponse(ObjectResult<Void> result) {
                            DialogHelper.dismissProgressDialog();
                            if (result.getResultCode() == 1) {
                                Toast.makeText(BasicInfoActivity.this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                                //mMsgSaveDays.setText(conversion(outTime));
                                FriendDao.getInstance().updateChatRecordTimeOut(mFriend.getUserId(), outTime);
                                Intent dads = new Intent(com.ydd.zhichat.broadcast.OtherBroadcast.SYNC_PAYL);
                                dads.putExtra("outTime", outTime);
                                sendBroadcast(dads);// 刷新个人信息
                                sendBroadcast(new Intent(com.ydd.zhichat.broadcast.OtherBroadcast.NAME_CHANGE));// 刷新聊天界面
                            } else {
                                Toast.makeText(BasicInfoActivity.this, result.getResultMsg(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Call call, Exception e) {
                            DialogHelper.dismissProgressDialog();
                            ToastUtil.showErrorNet(mContext);
                        }
                    });
        }
    }


    //=======================================================================

    public class RefreshBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(com.ydd.zhichat.broadcast.OtherBroadcast.SYNC_PAYL)) {
                //重新加载用户
                double outTime = intent.getDoubleExtra("outTime", 0);//免打扰UI
                mFriend.setChatRecordTimeOut(outTime);
            }else if(action.equals(com.ydd.zhichat.broadcast.OtherBroadcast.SYNC_PAYL3)) {
                boolean isChecked = intent.getBooleanExtra("isChecked",false);
                mFriend.setTopTime(isChecked ? mFriend.getTimeSend() : 0);
            }else if(action.equals(com.ydd.zhichat.broadcast.OtherBroadcast.SYNC_PAYL2)) {
                boolean isChecked = intent.getBooleanExtra("isChecked",false);
                mFriend.setOfflineNoPushMsg(isChecked ? 1 : 0);
                findViewById(R.id.mute_voice_btn).setActivated(mFriend.getOfflineNoPushMsg() == 1);
            }

        }
    }

    //动态数据
    public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {


        @Override
        public MsgAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MsgAdapter.ViewHolder holder, int position) {
            //holder.mTextView;
            imageClass resource = imagesList.get(position);
            if (resource.type == 0) {
                AvatarHelper.getInstance().displayUrl(resource.url, holder.mImage);
            } else {
                AvatarHelper.getInstance().displayUrl(resource.url, holder.mImage);
            }

            holder.mImage.setOnClickListener(v -> {
                if (mUser != null) {
                    Intent intent = new Intent(BasicInfoActivity.this, BusinessCircleActivity.class);
                    intent.putExtra(AppConstant.EXTRA_CIRCLE_TYPE, AppConstant.CIRCLE_TYPE_PERSONAL_SPACE);
                    intent.putExtra(AppConstant.EXTRA_USER_ID, mUserId);
                    intent.putExtra(AppConstant.EXTRA_NICK_NAME, mUser.getNickName());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return imagesList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView mImage;

            public ViewHolder(View itemView) {
                super(itemView);
                mImage = itemView.findViewById(R.id.image_view);
            }
        }
    }

    private void requestSpace(final boolean isPullDwonToRefersh) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", coreManager.getSelfStatus().accessToken);
        params.put("userId", mUserId);
        params.put("flag", PublicMessage.FLAG_NORMAL + "");

        params.put("pageSize", String.valueOf(AppConfig.PAGE_SIZE));

        HttpUtils.get().url(coreManager.getConfig().MSG_USER_LIST)
                .params(params)
                .build()
                .execute(new ListCallback<PublicMessage>(PublicMessage.class) {
                    @Override
                    public void onResponse(com.xuan.xuanhttplibrary.okhttp.result.ArrayResult<PublicMessage> result) {
                        List<PublicMessage> data = result.getData();

                        for (PublicMessage message : data) {
                            if (message.getBody().getVideos() != null) {
                                for (PublicMessage.Resource image : message.getBody().getImages()) {
                                    imagesList.add(new imageClass(image.getOriginalUrl(), 1));
                                }
                            } else {
                                if (message.getBody().getImages().size() > 0) {
                                    for (PublicMessage.Resource image : message.getBody().getImages()) {
                                        imagesList.add(new imageClass(image.getOriginalUrl(), 1));
                                    }
                                }
                            }

                        }
                        recyclerView.getAdapter().notifyDataSetChanged();
                        int aaa = 0;
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showErrorNet(getApplicationContext());
                    }
                });
    }


}