package com.ydd.zhichat.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.ydd.zhichat.AppConstant;
import com.ydd.zhichat.MyApplication;
import com.ydd.zhichat.R;
import com.ydd.zhichat.bean.Friend;
import com.ydd.zhichat.helper.AvatarHelper;
import com.ydd.zhichat.sortlist.BaseSortModel;
import com.ydd.zhichat.ui.base.CoreManager;
import com.ydd.zhichat.ui.message.multi.RoomInfoActivity;
import com.ydd.zhichat.ui.other.BasicInfoActivity;
import com.ydd.zhichat.util.UiUtils;
import com.ydd.zhichat.util.ViewHolder;
import com.ydd.zhichat.view.HeadView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FriendSortAdapter1 extends BaseAdapter implements SectionIndexer {

    private Context mContext;
    private List<BaseSortModel<Friend>> mSortFriends;

    public FriendSortAdapter1(Context context, List<BaseSortModel<Friend>> sortFriends) {
        mContext = context;
        mSortFriends = sortFriends;

    }

    public void setData(List<BaseSortModel<Friend>> sortFriends) {
        mSortFriends = sortFriends;
        notifyDataSetChanged();
    }

    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public int getCount() {
        return mSortFriends.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.row_sort_friend1, parent, false);
        }
        TextView catagoryTitleTv = ViewHolder.get(convertView, R.id.catagory_title);
        HeadView avatar_img = ViewHolder.get(convertView, R.id.avatar_img);
        HeadView avatar_imgS = ViewHolder.get(convertView, R.id.avatar_imgS);
        RoundedImageView roundedImageView = avatar_imgS.findViewById(R.id.ivHead);
        TextView nick_name_tv = ViewHolder.get(convertView, R.id.nick_name_tv);
        ImageView vip_mark = ViewHolder.get(convertView, R.id.vip_mark);
        ImageView uuid_mark = ViewHolder.get(convertView, R.id.uuid_mark);
        TextView uuid = ViewHolder.get(convertView, R.id.uuid_tv);
        // TextView des_tv = ViewHolder.get(convertView, R.id.des_tv);

        //动态设置图片宽高
//        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(dip2px(mContext,54),dip2px(mContext,54));
//        layoutParams.setMargins(0,dip2px(mContext,4),0,dip2px(mContext,5));
//        roundedImageView.setLayoutParams(layoutParams);

        // 根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);
        // 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            catagoryTitleTv.setVisibility(View.VISIBLE);
            catagoryTitleTv.setText(mSortFriends.get(position).getFirstLetter());
        } else {
            catagoryTitleTv.setVisibility(View.GONE);
        }

        final Friend friend = mSortFriends.get(position).getBean();
        if (friend.getRoomFlag() == 0) {// 单人
            avatar_img.setVisibility(View.VISIBLE);
            avatar_imgS.setVisibility(View.GONE);
            if (friend.getUserId().equals(Friend.ID_SYSTEM_MESSAGE)) {
                avatar_img.getHeadImage().setImageResource(R.drawable.im_notice);
            } else if (friend.getUserId().equals(Friend.ID_NEW_FRIEND_MESSAGE)) {
                avatar_img.getHeadImage().setImageResource(R.drawable.im_new_friends);
            } else if (friend.getIsDevice() == 1) {
                if ("android".equals(friend.getUserId()) || "ios".equals(friend.getUserId())) {
                    avatar_img.getHeadImage().setImageResource(R.drawable.fdy);
                } else if ("pc".equals(friend.getUserId()) || "mac".equals(friend.getUserId()) || "web".equals(friend.getUserId())) {
                    avatar_img.getHeadImage().setImageResource(R.drawable.feb);
                }
            } else {
                AvatarHelper.getInstance().displayAvatar(friend.getUserId(), avatar_img.getHeadImage(), true);
            }
        } else {// 群组
            avatar_img.setVisibility(View.GONE);
            avatar_imgS.setVisibility(View.VISIBLE);
            AvatarHelper.getInstance().displayAvatar(CoreManager.requireSelf(mContext).getUserId(), friend, avatar_imgS);
        }

        // 昵称
        nick_name_tv.setText(!TextUtils.isEmpty(friend.getRemarkName()) ? friend.getRemarkName() : friend.getNickName());
        // 个性签名
        // des_tv.setText(friend.getDescription());

        if(CoreManager.requireConfig(this.mContext).isUidShop) {
            if (friend.getUidIndex() > 0) {
                uuid_mark.setVisibility(View.VISIBLE);
                ViewHolder.get(convertView, R.id.ll_uuid).setVisibility(View.VISIBLE);
                uuid.setText(friend.getUid());
                if (friend.getUid().isEmpty()) {
                    ViewHolder.get(convertView, R.id.uuid_tv).setVisibility(View.GONE);
                }
            } else {
                uuid_mark.setVisibility(View.GONE);
                ViewHolder.get(convertView, R.id.ll_uuid).setVisibility(View.GONE);
            }
        }else{
            ViewHolder.get(convertView, R.id.uuid_tv).setVisibility(View.GONE);
            uuid_mark.setVisibility(View.GONE);
        }

        //vip标志
        if (CoreManager.requireConfig(this.mContext).isVipCenter) {
            if (friend.getVip() > 0) {
                if (CoreManager.requireConfig(this.mContext).isVipCenter) {
                    vip_mark.setVisibility(View.VISIBLE);
                    int level = friend.getVip();
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
                    avatar_img.setVip(friend.getVip());
                    switch (level) {
                        case 1: {
                            nick_name_tv.setTextColor(MyApplication.getContext().getResources().getColor(R.color.color_vip1));
                            String myColorString = CoreManager.requireConfig(mContext).vip1TextColo;
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
                            String myColorString = CoreManager.requireConfig(mContext).vip2TextColo;
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
                            String myColorString = CoreManager.requireConfig(mContext).vip3TextColo;
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
                } else {
                    vip_mark.setVisibility(View.GONE);
                    nick_name_tv.setTextColor(MyApplication.getContext().getResources().getColor(R.color.black_2));
                }
            } else {
                avatar_img.setVip(0);
                avatar_imgS.setVip(0);
                vip_mark.setVisibility(View.GONE);
                nick_name_tv.setTextColor(MyApplication.getContext().getResources().getColor(R.color.black_2));
            }
        }else{
            avatar_img.setVip(0);
            avatar_imgS.setVip(0);
            vip_mark.setVisibility(View.GONE);
            nick_name_tv.setTextColor(MyApplication.getContext().getResources().getColor(R.color.black_2));
        }


        //OK
        if(CoreManager.requireConfig(this.mContext).isUidShop) {
            if (friend.getUidIndex() > 0) {
                uuid_mark.setVisibility(View.VISIBLE);
            } else {
                uuid_mark.setVisibility(View.GONE);
            }
        }else{
            ViewHolder.get(convertView, R.id.uuid_tv).setVisibility(View.GONE);
            uuid_mark.setVisibility(View.GONE);
        }

        // 点击头像跳转详情
        avatar_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!UiUtils.isNormalClick(view)) {
                    return;
                }
                if (friend.getRoomFlag() == 0) {  // 单人
                    if (!friend.getUserId().equals(Friend.ID_SYSTEM_MESSAGE)
                            && !friend.getUserId().equals(Friend.ID_NEW_FRIEND_MESSAGE)
                            && friend.getIsDevice() != 1) {
                        Intent intent = new Intent(mContext, BasicInfoActivity.class);
                        intent.putExtra(AppConstant.EXTRA_USER_ID, friend.getUserId());
                        mContext.startActivity(intent);
                    }
                } else {  // 群组
                    Intent intent = new Intent(mContext, RoomInfoActivity.class);
                    intent.putExtra(AppConstant.EXTRA_USER_ID, friend.getUserId());
                    mContext.startActivity(intent);
                }
            }
        });

        // 点击头像跳转详情
        avatar_imgS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, RoomInfoActivity.class);
                intent.putExtra(AppConstant.EXTRA_USER_ID, friend.getUserId());
                mContext.startActivity(intent);
            }
        });

        //加载后台图片
        AvatarHelper.getInstance().displayUrl(CoreManager.requireConfig(mContext).lhaoImgUpdate, uuid_mark, R.drawable.l3_1);

        return convertView;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = mSortFriends.get(i).getFirstLetter();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return mSortFriends.get(position).getFirstLetter().charAt(0);
    }

    public void updateUI() {

    }


}
