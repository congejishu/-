package com.ydd.zhichat.view.chatHolder;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.ydd.zhichat.MyApplication;
import com.ydd.zhichat.R;
import com.ydd.zhichat.bean.Friend;
import com.ydd.zhichat.bean.RoomMember;
import com.ydd.zhichat.bean.User;
import com.ydd.zhichat.bean.message.ChatMessage;
import com.ydd.zhichat.ui.base.CoreManager;
import com.ydd.zhichat.util.Constants;
import com.ydd.zhichat.util.HtmlUtils;
import com.ydd.zhichat.util.PreferenceUtils;
import com.ydd.zhichat.util.StringUtils;
import com.ydd.zhichat.util.link.HttpTextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextViewHolder extends AChatHolderInterface {

    public HttpTextView mTvContent;
    public TextView tvFireTime;

    @Override
    public int itemLayoutId(boolean isMysend) {
        return isMysend ? R.layout.chat_from_item_text : R.layout.chat_to_item_text;
    }

    @Override
    public void initView(View view) {
        mTvContent = view.findViewById(R.id.chat_text);
        mRootView = view.findViewById(R.id.chat_warp_view);
        if (!isMysend) {
            tvFireTime = view.findViewById(R.id.tv_fire_time);
        }
    }

    @Override
    public void fillData(ChatMessage message) {
        // 修改字体功能
        int size = PreferenceUtils.getInt(mContext, Constants.FONT_SIZE) + 14;
        mTvContent.setTextSize(size);
        //mTvContent.setTextColor(mContext.getResources().getColor(R.color.black));

        String content = StringUtils.replaceSpecialChar(message.getContent());
        CharSequence charSequence = HtmlUtils.transform200SpanString(content, true);
        if (message.getIsReadDel() && !isMysend) {// 阅后即焚
            if (!message.isGroup() && !message.isSendRead()) {
                mTvContent.setText(R.string.tip_click_to_read);
                mTvContent.setTextColor(mContext.getResources().getColor(R.color.redpacket_bg));
            } else {
                // 已经查看了，当适配器再次刷新的时候，不需要重新赋值
                mTvContent.setText(charSequence);
            }
        } else {
            mTvContent.setText(charSequence);
        }

        mTvContent.setUrlText(mTvContent.getText());

        mTvContent.setOnClickListener(v -> mHolderListener.onItemClick(mRootView, TextViewHolder.this, mdata));
        mTvContent.setOnLongClickListener(v -> {
            mHolderListener.onItemLongClick(v, TextViewHolder.this, mdata);
            return true;
        });
    }

    @Override
    protected void fillData(ChatMessage message, RoomMember member) {
        if(member != null) {
            if(member.getVip() > 0) {
                switch (member.getVip()) {
                    case 1: {
                        mTvContent.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_vip1));
                        String myColorString = CoreManager.requireConfig(mContext).vip1TextColo;
                        if (!myColorString.isEmpty()) {
                            Pattern colorPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
                            Matcher m = colorPattern.matcher(myColorString);
                            boolean isColor = m.matches();
                            if (isColor) {
                                mTvContent.setTextColor(Color.parseColor(myColorString));
                            }
                        }
                    }
                    break;
                    case 2: {
                        mTvContent.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_vip2));
                        String myColorString = CoreManager.requireConfig(mContext).vip2TextColo;
                        if (!myColorString.isEmpty()) {
                            Pattern colorPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
                            Matcher m = colorPattern.matcher(myColorString);
                            boolean isColor = m.matches();
                            if (isColor) {
                                mTvContent.setTextColor(Color.parseColor(myColorString));
                            }
                        }
                    }

                    break;
                    case 3: {
                        mTvContent.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_vip3));
                        String myColorString = CoreManager.requireConfig(mContext).vip3TextColo;
                        if (!myColorString.isEmpty()) {
                            Pattern colorPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
                            Matcher m = colorPattern.matcher(myColorString);
                            boolean isColor = m.matches();
                            if (isColor) {
                                mTvContent.setTextColor(Color.parseColor(myColorString));
                            }
                        }
                    }
                    break;
                    default:
                        mTvContent.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_black));
                        break;
                }
            }else{
                mTvContent.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_black));
            }
        }
        fillData(message);
    }

    @Override
    protected void fillData(ChatMessage message, Friend friend) {
        if(friend != null) {
            if(friend.getVip() > 0) {
                switch (friend.getVip()) {
                    case 1: {
                        mTvContent.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_vip1));
                        String myColorString = CoreManager.requireConfig(mContext).vip1TextColo;
                        if (!myColorString.isEmpty()) {
                            Pattern colorPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
                            Matcher m = colorPattern.matcher(myColorString);
                            boolean isColor = m.matches();
                            if (isColor) {
                                mTvContent.setTextColor(Color.parseColor(myColorString));
                            }
                        }
                    }
                    break;
                    case 2: {
                        mTvContent.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_vip2));
                        String myColorString = CoreManager.requireConfig(mContext).vip2TextColo;
                        if (!myColorString.isEmpty()) {
                            Pattern colorPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
                            Matcher m = colorPattern.matcher(myColorString);
                            boolean isColor = m.matches();
                            if (isColor) {
                                mTvContent.setTextColor(Color.parseColor(myColorString));
                            }
                        }
                    }

                    break;
                    case 3: {
                        mTvContent.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_vip3));
                        String myColorString = CoreManager.requireConfig(mContext).vip3TextColo;
                        if (!myColorString.isEmpty()) {
                            Pattern colorPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
                            Matcher m = colorPattern.matcher(myColorString);
                            boolean isColor = m.matches();
                            if (isColor) {
                                mTvContent.setTextColor(Color.parseColor(myColorString));
                            }
                        }
                    }
                    break;
                    default:
                        mTvContent.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_black));
                        break;
                }
            }else{
                mTvContent.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_black));
            }
        }
        fillData(message);
    }

    @Override
    protected void onRootClick(View v) {

    }

    @Override
    public boolean enableFire() {
        return true;
    }

    @Override
    public boolean enableSendRead() {
        return true;
    }

    public void showFireTime(boolean show) {
        if (tvFireTime != null) {
            tvFireTime.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    protected void fillData(ChatMessage message, User friend) {
        if(friend != null) {
            if(CoreManager.requireConfig(this.mContext).isVipCenter) {
                if (friend.getVip() > 0) {
                    switch (friend.getVip()) {
                        case 1: {
                            mTvContent.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_vip1));
                            String myColorString = CoreManager.requireConfig(mContext).vip1TextColo;
                            if (!myColorString.isEmpty()) {
                                Pattern colorPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
                                Matcher m = colorPattern.matcher(myColorString);
                                boolean isColor = m.matches();
                                if (isColor) {
                                    mTvContent.setTextColor(Color.parseColor(myColorString));
                                }
                            }
                        }
                        break;
                        case 2: {
                            mTvContent.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_vip2));
                            String myColorString = CoreManager.requireConfig(mContext).vip2TextColo;
                            if (!myColorString.isEmpty()) {
                                Pattern colorPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
                                Matcher m = colorPattern.matcher(myColorString);
                                boolean isColor = m.matches();
                                if (isColor) {
                                    mTvContent.setTextColor(Color.parseColor(myColorString));
                                }
                            }
                        }

                        break;
                        case 3: {
                            mTvContent.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_vip3));
                            String myColorString = CoreManager.requireConfig(mContext).vip3TextColo;
                            if (!myColorString.isEmpty()) {
                                Pattern colorPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
                                Matcher m = colorPattern.matcher(myColorString);
                                boolean isColor = m.matches();
                                if (isColor) {
                                    mTvContent.setTextColor(Color.parseColor(myColorString));
                                }
                            }
                        }
                        break;
                        default:
                            mTvContent.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_black));
                            break;
                    }
                } else {
                    mTvContent.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_black));
                }
            }else{
                mTvContent.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_black));
            }
        }
        fillData(message);
    }
}
