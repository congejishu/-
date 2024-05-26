package com.ydd.zhichat.ui.me.vip;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ydd.zhichat.MyApplication;
import com.ydd.zhichat.R;
import com.ydd.zhichat.bean.User;
import com.ydd.zhichat.bean.VipConfigApp;
import com.ydd.zhichat.helper.AvatarHelper;
import com.ydd.zhichat.ui.base.BaseActivity;
import com.ydd.zhichat.ui.me.VipServiceActivity;
import com.ydd.zhichat.util.LocaleHelper;
import com.ydd.zhichat.util.TimeUtils;
import com.ydd.zhichat.view.HeadMeView;
import com.ydd.zhichat.view.HeadView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.srain.cube.views.GridViewWithHeaderAndFooter;

public class MyVipActivity extends BaseActivity implements View.OnClickListener{

    private HeadMeView mAvatarImg;
    private ImageView imageView3;
    private TextView mNickNameTv;
    private TextView mPhoneNumTv;
    private TextView skyTv, setTv;
    private TextView mIDTv;
    private List<vipLei> mCurrentMembers = new ArrayList<>();
    private GridViewAdapter mAdapter;
    private GridViewWithHeaderAndFooter mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleHelper.setLocale(this, LocaleHelper.getLanguage(this));
        setContentView(R.layout.fragment_me_vip);
        getSupportActionBar().hide();
        findViewById(R.id.iv_title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Update();
        updateVip(MyApplication.mCoreManager.getSelf());
    }

    @Override
    protected void updatUser(User user) {
        super.updatUser(user);
        updateVip(user);
    }

    private void initView() {
        mAvatarImg = (HeadMeView) findViewById(R.id.avatar_img);
        imageView3 = (ImageView) findViewById(R.id.imageView3);
        mNickNameTv = (TextView) findViewById(R.id.nick_name_tv);
        mPhoneNumTv = (TextView) findViewById(R.id.phone_number_tv);
        mIDTv = (TextView) findViewById(R.id.id_number_tv);
        String loginUserId = coreManager.getSelf().getUserId();
        AvatarHelper.getInstance().displayAvatar(coreManager.getSelf().getNickName(), loginUserId, mAvatarImg.getHeadImage(), false);
        if(coreManager.getSelf().getUidIndex() > 0) {
            mIDTv.setText("靓号:"+coreManager.getSelf().getAccount());
        }else {
            mIDTv.setText("ID:" + loginUserId);
        }
        if(coreManager.getSelf().getVip() > 0) {
            mPhoneNumTv.setText("到期时间:" + TimeUtils.f_long_2_str(coreManager.getSelf().getEndVipTime()));
        }else{
            mPhoneNumTv.setText("开通VIP会员，尊贵会员权益");
        }
        mNickNameTv.setText(coreManager.getSelf().getNickName());

        TextView tvTitle = (TextView) findViewById(R.id.tv_title_center);
        tvTitle.setText(R.string.vip_asdgqwe);

        updateVip(MyApplication.mCoreManager.getSelf());

        mGridView = findViewById(R.id.grid_view);

        mAdapter = new GridViewAdapter();
        mGridView.setAdapter(mAdapter);

        InitData();
        findViewById(R.id.vip_buy).setOnClickListener(this);
        mAvatarImg.setVip(MyApplication.mCoreManager.getSelf().getVip());
    }

    private void InitData(){
        VipConfigApp vipConfigApp = MyApplication.mCoreManager.getConfig().vipConfig;

        for(int i = 0;i<12;i++) {
            switch (i) {
                case 0:
                    if(vipConfigApp.getDoubleRevoke() == 1) {
                        mCurrentMembers.add(new vipLei("双向撤回",R.mipmap.icon_vip15));
                    }
                    break;
                case 1:
                    if(vipConfigApp.getNameLight() == 1) {
                        mCurrentMembers.add(new vipLei("昵称高亮",R.mipmap.icon_vip21));
                    }
                    break;
                case 2:
                    if(vipConfigApp.getVipIcon() == 1) {
                        mCurrentMembers.add(new vipLei("会员亮标",R.mipmap.icon_vip11));
                    }
                    break;
                case 3:
                    if(vipConfigApp.getAutoReply() == 1) {
                        mCurrentMembers.add(new vipLei("自动回复",R.mipmap.icon_vip27));
                    }
                    break;
                case 4:
                    if(vipConfigApp.getSuperGroup() == 1) {
                        mCurrentMembers.add(new vipLei("超级大群",R.mipmap.icon_vip24));
                    }
                    break;
                case 5:
                    if(vipConfigApp.getVoiceVideo() == 1) {
                        mCurrentMembers.add(new vipLei("语视通话",R.mipmap.icon_vip19));
                    }
                    break;
                case 6:
                    mCurrentMembers.add(new vipLei("头像边框",R.mipmap.icon_vip25));
                    break;
                case 7:
                    mCurrentMembers.add(new vipLei("已读提醒",R.mipmap.icon_vip23));
                    break;
                case 8:
                    mCurrentMembers.add(new vipLei("优先客服",R.mipmap.icon_vip230));
                    break;
                case 9:
                    mCurrentMembers.add(new vipLei("截图无提示",R.mipmap.icon_vip22));
                    break;
                case 10:
                    mCurrentMembers.add(new vipLei("撤回无痕迹",R.mipmap.icon_vip111));
                    break;
                case 11:
                    mCurrentMembers.add(new vipLei("隐藏在线",R.mipmap.icon_vip17));
                    break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Update();
        setResult(2000);
    }


    private void updateVip(User user) {
        if (user.getVip() > 0) {
            ImageView vipView = findViewById(R.id.vip_mark);
            vipView.setVisibility(View.VISIBLE);
            int level = user.getVip();
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


            switch (level) {
                case 1: {
                    mNickNameTv.setTextColor(MyApplication.getContext().getResources().getColor(R.color.color_vip1));
                    String myColorString = coreManager.getConfig().vip1TextColo;
                    if (!myColorString.isEmpty()) {
                        Pattern colorPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
                        Matcher m = colorPattern.matcher(myColorString);
                        boolean isColor = m.matches();
                        if (isColor) {
                            mNickNameTv.setTextColor(Color.parseColor(myColorString));
                        }
                    }
                    break;
                }
                case 2: {
                    mNickNameTv.setTextColor(MyApplication.getContext().getResources().getColor(R.color.color_vip2));
                    String myColorString = coreManager.getConfig().vip2TextColo;
                    if (!myColorString.isEmpty()) {
                        Pattern colorPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
                        Matcher m = colorPattern.matcher(myColorString);
                        boolean isColor = m.matches();
                        if (isColor) {
                            mNickNameTv.setTextColor(Color.parseColor(myColorString));
                        }
                    }
                    break;
                }
                case 3: {
                    mNickNameTv.setTextColor(MyApplication.getContext().getResources().getColor(R.color.color_vip3));
                    String myColorString = coreManager.getConfig().vip3TextColo;
                    if (!myColorString.isEmpty()) {
                        Pattern colorPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
                        Matcher m = colorPattern.matcher(myColorString);
                        boolean isColor = m.matches();
                        if (isColor) {
                            mNickNameTv.setTextColor(Color.parseColor(myColorString));
                        }
                    }
                    break;
                }
                default:
                    mNickNameTv.setTextColor(MyApplication.getContext().getResources().getColor(R.color.color_black));
                    break;
            }
            mAvatarImg.setVip(level);
        } else {
            findViewById(R.id.vip_mark).setVisibility(View.GONE);
            mNickNameTv.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_black));
        }

        if(user.getVip() > 0) {
            mPhoneNumTv.setText("到期时间:" + TimeUtils.f_long_2_str(user.getEndVipTime()));
        }else{
            mPhoneNumTv.setText("开通VIP会员，尊贵会员权益");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                startActivityForResult(new Intent(this.mContext, VipServiceActivity.class),200);
                break;
        }
    }

    class GridViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mCurrentMembers.size();
        }

        @Override
        public Object getItem(int position) {
            return mCurrentMembers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_room_info_view, parent, false);
                VipGridViewHolder vh = new VipGridViewHolder(convertView);
                convertView.setTag(vh);
            }

            VipGridViewHolder vh = (VipGridViewHolder) convertView.getTag();
            HeadView imageView = vh.imageView;
            TextView memberName = vh.memberName;

            memberName.setText(mCurrentMembers.get(position).text);
            imageView.getHeadImage().setImageResource(mCurrentMembers.get(position).getRid());



            return convertView;
        }
    }


    class VipGridViewHolder {
        HeadView imageView;
        TextView memberName;

        VipGridViewHolder(View itemView) {
            imageView = itemView.findViewById(R.id.content);
            memberName = itemView.findViewById(R.id.member_name);
        }
    }

    public static class vipLei{
        private final String text;
        private final int Rid;

        public vipLei(String text, int rId) {
            this.text = text;
            this.Rid = rId;
        }

        public String getText() {
            return text;
        }

        public int getRid() {
            return Rid;
        }
    }

}
