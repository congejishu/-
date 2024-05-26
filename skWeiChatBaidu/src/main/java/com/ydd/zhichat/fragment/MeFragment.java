package com.ydd.zhichat.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.signature.StringSignature;
import com.google.gson.Gson;
import com.j256.ormlite.stmt.query.In;
import com.ydd.zhichat.AppConstant;
import com.ydd.zhichat.MyApplication;
import com.ydd.zhichat.R;
import com.ydd.zhichat.Reporter;
import com.ydd.zhichat.bean.Friend;
import com.ydd.zhichat.bean.UUidShop;
import com.ydd.zhichat.bean.User;
import com.ydd.zhichat.broadcast.OtherBroadcast;
import com.ydd.zhichat.course.LocalCourseActivity;
import com.ydd.zhichat.db.InternationalizationHelper;
import com.ydd.zhichat.db.dao.FriendDao;
import com.ydd.zhichat.db.dao.UserAvatarDao;
import com.ydd.zhichat.helper.AvatarHelper;
import com.ydd.zhichat.helper.DialogHelper;
import com.ydd.zhichat.ui.MainActivity;
import com.ydd.zhichat.ui.base.CoreManager;
import com.ydd.zhichat.ui.base.EasyFragment;
import com.ydd.zhichat.ui.circle.BusinessCircleActivity;
import com.ydd.zhichat.ui.circle.DiscoverActivity;
import com.ydd.zhichat.ui.circle.range.NewZanActivity;
import com.ydd.zhichat.ui.contacts.RoomActivity;
import com.ydd.zhichat.ui.me.BasicInfoEditActivity;
import com.ydd.zhichat.ui.me.MyCollection;
import com.ydd.zhichat.ui.me.SettingActivity;
import com.ydd.zhichat.ui.me.ShareActivity;
import com.ydd.zhichat.ui.me.redpacket.WxPayBlance;
import com.ydd.zhichat.ui.me.uid.UidShopActivity;
import com.ydd.zhichat.ui.me.vip.MyVipActivity;
import com.ydd.zhichat.ui.message.ChatActivity;
import com.ydd.zhichat.ui.other.QRcodeActivity;
import com.ydd.zhichat.ui.tool.SingleImagePreviewActivity;
import com.ydd.zhichat.util.AsyncUtils;
import com.ydd.zhichat.util.Constants;
import com.ydd.zhichat.util.PreferenceUtils;
import com.ydd.zhichat.util.ToastUtil;
import com.ydd.zhichat.util.UiUtils;
import com.ydd.zhichat.view.HeadMeView;
import com.ydd.zhichat.view.HeadView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MeFragment extends EasyFragment implements View.OnClickListener {

    private HeadMeView mAvatarImg;
    private ImageView imageView3;
    private TextView mNickNameTv;
    private TextView mPhoneNumTv;
    private TextView skyTv, setTv;
    String zs1 = "{\n" +
            "\t\"_id\": 1,\n" +
            "\t\"chatRecordTimeOut\": -1.0,\n" +
            "\t\"companyId\": 0,\n" +
            "\t\"content\": \"欢迎使用本软件！\",\n" +
            "\t\"downloadTime\": 0,\n" +
            "\t\"groupStatus\": 0,\n" +
            "\t\"isAtMe\": 0,\n" +
            "\t\"isDevice\": 0,\n" +
            "\t\"nickName\": \"my customer service\",\n" +
            "\t\"offlineNoPushMsg\": 0,\n" +
            "\t\"ownerId\": \"10000014\",\n" +
            "\t\"remarkName\": \"my customer service\",\n" +
            "\t\"roomFlag\": 0,\n" +
            "\t\"roomTalkTime\": 0,\n" +
            "\t\"status\": 8,\n" +
            "\t\"timeCreate\": 0,\n" +
            "\t\"timeSend\": 0,\n" +
            "\t\"topTime\": 0,\n" +
            "\t\"type\": 0,\n" +
            "\t\"unReadNum\": 0,\n" +
            "\t\"userId\": \"10000\",\n" +
            "\t\"version\": 1,\n" +
            "\t\"vip\": 0\n" +
            "}";
    private BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, OtherBroadcast.SYNC_SELF_DATE_NOTIFY)) {
                Update();
                updateUI();
            }
        }
    };

    public MeFragment() {
    }

    @Override
    protected int inflateLayoutId() {
        return R.layout.fragment_me;
    }

    @Override
    protected void onActivityCreated(Bundle savedInstanceState, boolean createView) {
        if (createView) {
            initView();
            Update();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Update();
        updateUI();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mUpdateReceiver);
    }

    private void initView() {
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
//        TextView mTvTitle = (TextView) findViewById(R.id.tv_title_center);
//        mTvTitle.setText("设置");
        skyTv = (TextView) findViewById(R.id.MySky);
        setTv = (TextView) findViewById(R.id.SettingTv);
        skyTv.setText(R.string.a10);
        setTv.setText(InternationalizationHelper.getString("JXSettingVC_Set"));
        findViewById(R.id.info_rl).setOnClickListener(this);
        findViewById(R.id.live_rl).setOnClickListener(this);
        findViewById(R.id.douyin_rl).setOnClickListener(this);

        findViewById(R.id.correlation_rl).setOnClickListener(this);
        findViewById(R.id.share_rl).setOnClickListener(this);
        findViewById(R.id.customer_rl).setOnClickListener(this);

        findViewById(R.id.ll_more).setVisibility(View.GONE);

        findViewById(R.id.my_monry).setOnClickListener(this);
        // 关闭红包功能，隐藏我的零钱
        if (coreManager.getConfig().displayRedPacket) { // 切换新旧两种ui对应我的页面是否显示视频会议、直播、短视频，
            findViewById(R.id.my_monry).setVisibility(View.GONE);
        }

        if(coreManager.getConfig().isVipCenter) {
            findViewById(R.id.vip_rl).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.vip_rl).setVisibility(View.GONE);
        }

        if(coreManager.getConfig().isUidShop) {
            findViewById(R.id.shop_rl).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.shop_rl).setVisibility(View.GONE);
        }

        findViewById(R.id.vip_rl).setOnClickListener(v -> {
            //MyVipActivity
            startActivityForResult(new Intent(getActivity(), MyVipActivity.class),2000);
        });

        //vip_mark.setVisibility(View.VISIBLE);
        ImageView vip_mark = findViewById(R.id.vip_mark);
        int level = coreManager.getSelf().getVip();
        if(coreManager.getConfig().isVipCenter) {
            if (level == 1) {
                vip_mark.setImageDrawable(MyApplication.getContext().getResources().getDrawable(R.mipmap.bhm));
                AvatarHelper.getInstance().displayUrl(coreManager.getConfig().viplevel1ImgUpdate, vip_mark, R.mipmap.bhm);
            } else if (level == 2) {
                vip_mark.setImageDrawable(MyApplication.getContext().getResources().getDrawable(R.mipmap.bhl));
                AvatarHelper.getInstance().displayUrl(coreManager.getConfig().viplevel2ImgUpdate, vip_mark, R.mipmap.bhl);
            } else if (level == 3) {
                vip_mark.setImageDrawable(MyApplication.getContext().getResources().getDrawable(R.mipmap.bhn));
                AvatarHelper.getInstance().displayUrl(coreManager.getConfig().viplevel3ImgUpdate, vip_mark, R.mipmap.bhn);
            }

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
        }

        ImageView uuid_mark = findViewById(R.id.uid_tv);
        if(coreManager.getConfig().isUidShop) {
            AvatarHelper.getInstance().displayUrl(coreManager.getConfig().lhaoImgUpdate, uuid_mark, R.drawable.l3_1);
        }

        findViewById(R.id.shop_rl).setOnClickListener(v->{
            //靓号商城
            startActivityForResult(new Intent(getActivity(), UidShopActivity.class),3000);
        });


        findViewById(R.id.my_space_rl).setOnClickListener(this);
        findViewById(R.id.my_collection_rl).setOnClickListener(this);
        findViewById(R.id.local_course_rl).setOnClickListener(this);
        findViewById(R.id.setting_rl).setOnClickListener(this);

        mAvatarImg = (HeadMeView) findViewById(R.id.avatar_img);
        imageView3 = (ImageView) findViewById(R.id.imageView3);
        mNickNameTv = (TextView) findViewById(R.id.nick_name_tv);
        mPhoneNumTv = (TextView) findViewById(R.id.phone_number_tv);
        String loginUserId = coreManager.getSelf().getUserId();
        AvatarHelper.getInstance().displayAvatar(coreManager.getSelf().getNickName(), loginUserId, mAvatarImg.getHeadImage(), false);
        mNickNameTv.setText(coreManager.getSelf().getNickName());

        /*
        Drawable left=getResources().getDrawable(R.mipmap.bhl);
        left.setBounds(0,0,50,50);//必须设置图片的大小否则没有作用
        mNickNameTv.setCompoundDrawables(left,null ,null,null);//设置图片left这里如果是右边就放到第二个参数里面依次对应
         */


        updateVip();



        mAvatarImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SingleImagePreviewActivity.class);
                intent.putExtra(AppConstant.EXTRA_IMAGE_URI, coreManager.getSelf().getUserId());
                startActivity(intent);
            }
        });
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User mUser = coreManager.getSelf();
                Intent intent2 = new Intent(getActivity(), QRcodeActivity.class);
                intent2.putExtra("isgroup", false);
                if (!TextUtils.isEmpty(mUser.getAccount())) {
                    intent2.putExtra("userid", mUser.getAccount());
                } else {
                    intent2.putExtra("userid", mUser.getUserId());
                }
                intent2.putExtra("userAvatar", mUser.getUserId());
                intent2.putExtra("userName", mUser.getNickName());
                startActivity(intent2);
            }
        });

        findViewById(R.id.llFriend).setOnClickListener(v -> {
            MainActivity activity = (MainActivity) requireActivity();
            activity.changeTab(R.id.rb_tab_2);
        });

        findViewById(R.id.llGroup).setOnClickListener(v -> RoomActivity.start(requireContext()));

//        initTitleBackground();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(OtherBroadcast.SYNC_SELF_DATE_NOTIFY);
        getActivity().registerReceiver(mUpdateReceiver, intentFilter);


    }

    private void updateVip() {
        if (MyApplication.mCoreManager.getSelf().getVip() > 0) {
            ImageView vipView = findViewById(R.id.vip_mark);
            vipView.setVisibility(View.VISIBLE);
            if(coreManager.getConfig().isVipCenter) {
                int level = MyApplication.mCoreManager.getSelf().getVip();
                if (level == 1) {
                    vipView.setImageDrawable(MyApplication.getContext().getResources().getDrawable(R.mipmap.bhm));
                    AvatarHelper.getInstance().displayUrl(coreManager.getConfig().viplevel1ImgUpdate, vipView, R.mipmap.bhm);
                } else if (level == 2) {
                    vipView.setImageDrawable(MyApplication.getContext().getResources().getDrawable(R.mipmap.bhl));
                    AvatarHelper.getInstance().displayUrl(coreManager.getConfig().viplevel2ImgUpdate, vipView, R.mipmap.bhl);
                } else if (level == 3) {
                    vipView.setImageDrawable(MyApplication.getContext().getResources().getDrawable(R.mipmap.bhn));
                    AvatarHelper.getInstance().displayUrl(coreManager.getConfig().viplevel3ImgUpdate, vipView, R.mipmap.bhn);
                }

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
            }else{
                findViewById(R.id.vip_mark).setVisibility(View.GONE);
                mAvatarImg.setVip(0);
                mNickNameTv.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_black));
            }
        } else {
            findViewById(R.id.vip_mark).setVisibility(View.GONE);
            mAvatarImg.setVip(0);
            mNickNameTv.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_black));
        }
    }

//    private void initTitleBackground() {
//        SkinUtils.Skin skin = SkinUtils.getSkin(requireContext());
//        int primaryColor = skin.getPrimaryColor();
//        findViewById(R.id.tool_bar).setBackgroundColor(primaryColor);
//    }

    @Override
    public void onClick(View v) {
        if (!UiUtils.isNormalClick(v)) {
            return;
        }
        int id = v.getId();
        switch (id) {
            case R.id.info_rl:
                // 我的资料
                startActivityForResult(new Intent(getActivity(), BasicInfoEditActivity.class), 1);
                break;
            case R.id.my_monry:
                // 我的零钱
                startActivity(new Intent(getActivity(), WxPayBlance.class));
                break;
            case R.id.my_space_rl:
                // 我的动态
                Intent intent = new Intent(getActivity(), BusinessCircleActivity.class);
                intent.putExtra(AppConstant.EXTRA_CIRCLE_TYPE, AppConstant.CIRCLE_TYPE_PERSONAL_SPACE);
                startActivity(intent);
                break;
            case R.id.my_collection_rl:
                // 我的收藏
                startActivity(new Intent(getActivity(), MyCollection.class));
                break;
            case R.id.local_course_rl:
                // 我的课件
                startActivity(new Intent(getActivity(), LocalCourseActivity.class));
                break;
            case R.id.setting_rl:
                // 设置
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            case R.id.correlation_rl:
                // 与我相关
                Intent intent2 = new Intent(getActivity(), NewZanActivity.class);
                intent2.putExtra("OpenALL", true);
                startActivity(intent2);
                break;
            case R.id.share_rl:
                // 推广中心
                startActivity(new Intent(getActivity(), ShareActivity.class));
                break;
            case R.id.customer_rl:
                // 我的客服
                // 微聊助手
                Friend friend = new Gson().fromJson(zs1, Friend.class);
                friend.setNickName(getString(R.string.x73));
                friend.setRemarkName(getString(R.string.x73));
                Intent intent1 = new Intent();
                intent1.setClass(getActivity(), ChatActivity.class);
                intent1.putExtra(ChatActivity.FRIEND, friend);
                startActivity(intent1);
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void updatUser(User user) {
        if(coreManager.getConfig().isUidShop) {
            if(user.getUidIndex() > 0) {
                findViewById(R.id.uid_tv).setVisibility(View.VISIBLE);
            }else{
                findViewById(R.id.uid_tv).setVisibility(View.GONE);
            }
        }else{
            findViewById(R.id.uid_tv).setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 || resultCode == Activity.RESULT_OK) {// 个人资料更新了
            Update();
            updateUI();
        }
        if(requestCode == 2000) {
            updateVip();
        }
    }

    public void displayAvatar(final String userId) {

        final String mOriginalUrl = AvatarHelper.getAvatarUrl(userId, false);
        Log.e("zx", "displayAvatar: mOriginalUrl:  " + mOriginalUrl + " uID: " + userId);
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
                            AvatarHelper.getInstance().displayAvatar(coreManager.getSelf().getNickName(), userId, mAvatarImg.getHeadImage(), true);
                        }
                    });
        } else {
            DialogHelper.dismissProgressDialog();
            Log.e("zq", "未获取到原图地址");// 基本上不会走这里
        }
    }

    @Override
    public void show() {
        super.show();
        Update();
        updateVip();
        updateUI();
    }

    /**
     * 用户的信息更改的时候，ui更新
     */
    private void updateUI() {
        if (mAvatarImg != null) {
            // AvatarHelper.getInstance().displayAvatar(coreManager.getSelf().getUserId(), mAvatarImg, true);
            displayAvatar(coreManager.getSelf().getUserId());
        }
        if (mNickNameTv != null) {
            mNickNameTv.setText(coreManager.getSelf().getNickName());
        }

        if (mPhoneNumTv != null) {
            String phoneNumber = coreManager.getSelf().getTelephone();
            int mobilePrefix = PreferenceUtils.getInt(getContext(), Constants.AREA_CODE_KEY, -1);
            String sPrefix = String.valueOf(mobilePrefix);
            // 删除开头的区号，
            if (phoneNumber.startsWith(sPrefix)) {
                phoneNumber = phoneNumber.substring(sPrefix.length());
            }
            mPhoneNumTv.setText(R.string.a12);
        }

        AsyncUtils.doAsync(this, t -> {
            Reporter.post(getString(R.string.a13), t);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    ToastUtil.showToast(requireContext(), R.string.tip_me_query_friend_failed);
                });
            }
        }, ctx -> {
            long count = FriendDao.getInstance().getFriendsCount(coreManager.getSelf().getUserId());
            ctx.uiThread(ref -> {
                TextView tvColleague = findViewById(R.id.tvFriend);
                tvColleague.setText(String.valueOf(count));
            });
        });

        AsyncUtils.doAsync(this, t -> {
            Reporter.post(getString(R.string.a14), t);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    ToastUtil.showToast(requireContext(), R.string.tip_me_query_friend_failed);
                });
            }
        }, ctx -> {
            long count = FriendDao.getInstance().getGroupsCount(coreManager.getSelf().getUserId());
            ctx.uiThread(ref -> {
                TextView tvGroup = findViewById(R.id.tvGroup);
                tvGroup.setText(String.valueOf(count));
            });
        });
    }
}
