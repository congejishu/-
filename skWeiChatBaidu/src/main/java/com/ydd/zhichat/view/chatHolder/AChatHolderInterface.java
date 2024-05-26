package com.ydd.zhichat.view.chatHolder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ydd.zhichat.MyApplication;
import com.ydd.zhichat.R;
import com.ydd.zhichat.bean.Friend;
import com.ydd.zhichat.bean.RoomMember;
import com.ydd.zhichat.bean.User;
import com.ydd.zhichat.bean.message.ChatMessage;
import com.ydd.zhichat.db.dao.ChatMessageDao;
import com.ydd.zhichat.helper.AvatarHelper;
import com.ydd.zhichat.ui.base.CoreManager;
import com.ydd.zhichat.view.HeadView;
import com.ydd.zhichat.xmpp.listener.ChatMessageListener;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AChatHolderInterface implements View.OnLongClickListener, View.OnClickListener {

    public Context mContext;

    public boolean isMysend;
    public List<ChatMessage> chatMessages;
    public boolean isGounp; // 是否是群聊
    public boolean isMultiple; // 多选
    public boolean showPerson;
    public int position, mouseX, mouseY;
    public ChatHolderFactory.ChatHolderType mHolderType;

    public String mLoginUserId;
    public String mLoginNickName;
    public String mToUserId;
    public ChatMessage mdata;
    // 在群里的身份，为null表示没有身份，比如单聊，
    @Nullable
    public Integer selfGroupRole;

    public TextView mTvTime; // 时间
    public HeadView mIvHead; // 头像
    public TextView mTvName; // 名字
    public View mRootView; // 根布局
    public ImageView mIvFire; // 消息阅后即焚
    public TextView mTvSendState; // 消息发送状态
    public ImageView mIvFailed; // 消息发送失败感叹号
    public ProgressBar mSendingBar; // 发送中的转圈
    public CheckBox mCboxSelect; // 多选
    public ImageView ivUnRead; // 未读消息红点

    public ChatHolderListener mHolderListener;

    /**
     * 分析聊天holder的共性
     * 每一个holder都要去 findviewbyid
     * 90%的holder都要加载头像，处理时间控件,显示名字，消息发送状态
     */

    protected abstract int itemLayoutId(boolean isMysend);

    protected abstract void initView(View view);

    protected abstract void fillData(ChatMessage message);

    protected abstract void fillData(ChatMessage message, RoomMember member);

    protected abstract void fillData(ChatMessage message, Friend friend);

    protected abstract void fillData(ChatMessage message, User friend);

    protected abstract void onRootClick(View v); // 重写此方法获得包裹布局的子view

    public int getLayoutId(boolean isMysend) {
        this.isMysend = isMysend;
        return itemLayoutId(isMysend);
    }

    public void findView(View convertView) {
        // 初始化 公共的布局
        if (enableNormal()) {
            inflateNormal(convertView);
        }

        // 初始化 子类view 自己的布局
        initView(convertView);

        if (enableUnRead()) {
            ivUnRead = convertView.findViewById(R.id.unread_img_view);
        }

        if (enableFire()) {
            mIvFire = convertView.findViewById(R.id.iv_fire);
        }
    }

    public void prepare(ChatMessage message, @Nullable Integer role, boolean secret, @Nullable RoomMember member) {
        mdata = message;
        if (enableNormal()) {
            // 显示消息状态
            changeMessageState(message);
            // 显示头像,管理员角标，我的设备显示我的设备
            mIvHead.setGroupRole(role);
            mIvHead.setVip(member.getVip());

            String toId = message.getToId();
            String types = !TextUtils.isEmpty(toId) && toId.length() < 8 ? message.getFromId() : message.getFromUserId();
            AvatarHelper.getInstance().displayAvatar(message.getFromUserName(), types, mIvHead.getHeadImage(), true);
            // 显示昵称
            changeNickName(message, secret);

            mCboxSelect.setChecked(message.isMoreSelected);
        }

        //会员昵称
        if (member != null) {
            if (member.getVip() > 0) {
                if (mTvName != null) {
                    switch (member.getVip()) {
                        case 1: {
                            mTvName.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_vip1));
                            String myColorString = CoreManager.requireConfig(mContext).vip1TextColo;
                            if (!myColorString.isEmpty()) {
                                Pattern colorPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
                                Matcher m = colorPattern.matcher(myColorString);
                                boolean isColor = m.matches();
                                if (isColor) {
                                    mTvName.setTextColor(Color.parseColor(myColorString));
                                }
                            }
                        }
                        break;
                        case 2: {
                            mTvName.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_vip2));
                            String myColorString = CoreManager.requireConfig(mContext).vip2TextColo;
                            if (!myColorString.isEmpty()) {
                                Pattern colorPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
                                Matcher m = colorPattern.matcher(myColorString);
                                boolean isColor = m.matches();
                                if (isColor) {
                                    mTvName.setTextColor(Color.parseColor(myColorString));
                                }
                            }
                        }

                        break;
                        case 3: {
                            mTvName.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_vip3));
                            String myColorString = CoreManager.requireConfig(mContext).vip3TextColo;
                            if (!myColorString.isEmpty()) {
                                Pattern colorPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
                                Matcher m = colorPattern.matcher(myColorString);
                                boolean isColor = m.matches();
                                if (isColor) {
                                    mTvName.setTextColor(Color.parseColor(myColorString));
                                }
                            }
                        }
                        break;
                        default:
                            mTvName.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_red));
                            break;
                    }

                }
            } else {
                if (mTvName != null) {
                    mTvName.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_black));
                }
            }
        }
        fillData(message, member);

        if (mRootView != null) {
            if (isOnClick()) {
                mRootView.setOnClickListener(this);
            }

            if (isLongClick()) {
                mRootView.setOnTouchListener((v, event) -> {
                    mouseX = (int) event.getX();
                    mouseY = (int) event.getY();
                    return false;
                });
                mRootView.setOnLongClickListener(this);
            } else {
                mRootView.setOnLongClickListener(null);
            }
        }

        // 显示阅后即焚
        if (enableFire()) {
            mIvFire.setVisibility(message.getIsReadDel() ? View.VISIBLE : View.GONE);
        }

        // 开启自动发回执
        if (enableSendRead() && !message.getIsReadDel() && !isMysend) {
            sendReadMessage(message);
        }
    }

    public void prepare(ChatMessage message, @Nullable Integer role, boolean secret, @Nullable User member) {
        mdata = message;
        if (enableNormal()) {
            // 显示消息状态
            changeMessageState(message);
            // 显示头像,管理员角标，我的设备显示我的设备
            mIvHead.setGroupRole(role);
            mIvHead.setVip(member.getVip());

            String toId = message.getToId();
            String types = !TextUtils.isEmpty(toId) && toId.length() < 8 ? message.getFromId() : message.getFromUserId();
            AvatarHelper.getInstance().displayAvatar(message.getFromUserName(), types, mIvHead.getHeadImage(), true);
            // 显示昵称
            changeNickName(message, secret);

            mCboxSelect.setChecked(message.isMoreSelected);
        }

        //会员昵称
        if (member != null) {
            if(CoreManager.requireConfig(this.mContext).isVipCenter) {
                if (member.getVip() > 0) {
                    if (mTvName != null) {
                        switch (member.getVip()) {
                            case 1: {
                                mTvName.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_vip1));
                                String myColorString = CoreManager.requireConfig(mContext).vip1TextColo;
                                if (!myColorString.isEmpty()) {
                                    Pattern colorPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
                                    Matcher m = colorPattern.matcher(myColorString);
                                    boolean isColor = m.matches();
                                    if (isColor) {
                                        mTvName.setTextColor(Color.parseColor(myColorString));
                                    }
                                }
                            }
                            break;
                            case 2: {
                                mTvName.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_vip2));
                                String myColorString = CoreManager.requireConfig(mContext).vip2TextColo;
                                if (!myColorString.isEmpty()) {
                                    Pattern colorPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
                                    Matcher m = colorPattern.matcher(myColorString);
                                    boolean isColor = m.matches();
                                    if (isColor) {
                                        mTvName.setTextColor(Color.parseColor(myColorString));
                                    }
                                }
                            }

                            break;
                            case 3: {
                                mTvName.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_vip3));
                                String myColorString = CoreManager.requireConfig(mContext).vip3TextColo;
                                if (!myColorString.isEmpty()) {
                                    Pattern colorPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
                                    Matcher m = colorPattern.matcher(myColorString);
                                    boolean isColor = m.matches();
                                    if (isColor) {
                                        mTvName.setTextColor(Color.parseColor(myColorString));
                                    }
                                }
                            }
                            break;
                            default:
                                mTvName.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_red));
                                break;
                        }
                    }
                } else {
                    if (mTvName != null) {
                        mTvName.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_black));
                    }
                }
            }else{
                mTvName.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_black));
            }
        }
        fillData(message, member);

        if (mRootView != null) {
            if (isOnClick()) {
                mRootView.setOnClickListener(this);
            }

            if (isLongClick()) {
                mRootView.setOnTouchListener((v, event) -> {
                    mouseX = (int) event.getX();
                    mouseY = (int) event.getY();
                    return false;
                });
                mRootView.setOnLongClickListener(this);
            } else {
                mRootView.setOnLongClickListener(null);
            }
        }

        // 显示阅后即焚
        if (enableFire()) {
            mIvFire.setVisibility(message.getIsReadDel() ? View.VISIBLE : View.GONE);
        }

        // 开启自动发回执
        if (enableSendRead() && !message.getIsReadDel() && !isMysend) {
            sendReadMessage(message);
        }
    }

    public void prepare(ChatMessage message, @Nullable Integer role, boolean secret, @Nullable Friend friend) {
        mdata = message;
        if (enableNormal()) {
            // 显示消息状态
            changeMessageState(message);
            // 显示头像,管理员角标，我的设备显示我的设备
            mIvHead.setGroupRole(role);

            String toId = message.getToId();
            String types = !TextUtils.isEmpty(toId) && toId.length() < 8 ? message.getFromId() : message.getFromUserId();
            AvatarHelper.getInstance().displayAvatar(message.getFromUserName(), types, mIvHead.getHeadImage(), true);
            // 显示昵称
            changeNickName(message, secret);

            mCboxSelect.setChecked(message.isMoreSelected);
        }

        //会员昵称
        if (friend != null) {
            if (friend.getVip() > 0) {
                if (mTvName != null) {
                    if(CoreManager.requireConfig(this.mContext).isVipCenter) {
                        switch (friend.getVip()) {
                            case 1: {
                                mTvName.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_vip1));
                                String myColorString = CoreManager.requireConfig(mContext).vip1TextColo;
                                if (!myColorString.isEmpty()) {
                                    Pattern colorPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
                                    Matcher m = colorPattern.matcher(myColorString);
                                    boolean isColor = m.matches();
                                    if (isColor) {
                                        mTvName.setTextColor(Color.parseColor(myColorString));
                                    }
                                }
                            }
                            break;
                            case 2: {
                                mTvName.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_vip2));
                                String myColorString = CoreManager.requireConfig(mContext).vip2TextColo;
                                if (!myColorString.isEmpty()) {
                                    Pattern colorPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
                                    Matcher m = colorPattern.matcher(myColorString);
                                    boolean isColor = m.matches();
                                    if (isColor) {
                                        mTvName.setTextColor(Color.parseColor(myColorString));
                                    }
                                }
                            }

                            break;
                            case 3: {
                                mTvName.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_vip3));
                                String myColorString = CoreManager.requireConfig(mContext).vip3TextColo;
                                if (!myColorString.isEmpty()) {
                                    Pattern colorPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
                                    Matcher m = colorPattern.matcher(myColorString);
                                    boolean isColor = m.matches();
                                    if (isColor) {
                                        mTvName.setTextColor(Color.parseColor(myColorString));
                                    }
                                }
                            }
                            break;
                            default:
                                mTvName.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_red));
                                break;
                        }
                    }else{
                        mTvName.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_black));
                    }
                }
                if (mIvHead != null) {
                    if(CoreManager.requireConfig(this.mContext).isVipCenter) {
                        mIvHead.setVip(friend.getVip());
                    }else{
                        mIvHead.setVip(0);
                    }
                }
            } else {
                if (mTvName != null) {
                    mTvName.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.color_black));
                }
                if (mIvHead != null) {
                    if(CoreManager.requireConfig(this.mContext).isVipCenter) {
                        mIvHead.setVip(friend.getVip());
                    }else{
                        mIvHead.setVip(0);
                    }
                }
            }
        }
        fillData(message, friend);

        if (mRootView != null) {
            if (isOnClick()) {
                mRootView.setOnClickListener(this);
            }

            if (isLongClick()) {
                mRootView.setOnTouchListener((v, event) -> {
                    mouseX = (int) event.getX();
                    mouseY = (int) event.getY();
                    return false;
                });
                mRootView.setOnLongClickListener(this);
            } else {
                mRootView.setOnLongClickListener(null);
            }
        }

        // 显示阅后即焚
        if (enableFire()) {
            mIvFire.setVisibility(message.getIsReadDel() ? View.VISIBLE : View.GONE);
        }

        // 开启自动发回执
        if (enableSendRead() && !message.getIsReadDel() && !isMysend) {
            sendReadMessage(message);
        }
    }

    public void prepare(ChatMessage message, @Nullable Integer role, boolean secret) {
        mdata = message;
        if (enableNormal()) {
            // 显示消息状态
            changeMessageState(message);
            // 显示头像,管理员角标，我的设备显示我的设备
            mIvHead.setGroupRole(role);

            String toId = message.getToId();
            String types = !TextUtils.isEmpty(toId) && toId.length() < 8 ? message.getFromId() : message.getFromUserId();
            AvatarHelper.getInstance().displayAvatar(message.getFromUserName(), types, mIvHead.getHeadImage(), true);
            // 显示昵称
            changeNickName(message, secret);

            mCboxSelect.setChecked(message.isMoreSelected);
        }

        fillData(message);

        if (mRootView != null) {
            if (isOnClick()) {
                mRootView.setOnClickListener(this);
            }

            if (isLongClick()) {
                mRootView.setOnTouchListener((v, event) -> {
                    mouseX = (int) event.getX();
                    mouseY = (int) event.getY();
                    return false;
                });
                mRootView.setOnLongClickListener(this);
            } else {
                mRootView.setOnLongClickListener(null);
            }
        }

        // 显示阅后即焚
        if (enableFire()) {
            mIvFire.setVisibility(message.getIsReadDel() ? View.VISIBLE : View.GONE);
        }

        // 开启自动发回执
        if (enableSendRead() && !message.getIsReadDel() && !isMysend) {
            sendReadMessage(message);
        }


    }

    private void changeNickName(ChatMessage message, boolean secret) {
        if (isGounp) {
            mTvName.setVisibility(isMysend ? View.GONE : View.VISIBLE);

            // Todo 有点多此一举，反而会造成其他昵称显示问题，已经在ChatContentView的chagneNameRemark方法内处理好了
/*
            if (!isMysend && !message.isLoadRemark()) {
                Friend friend = FriendDao.getInstance().getFriend(mLoginUserId, message.getFromUserId());
                if (friend != null && !TextUtils.isEmpty(friend.getRemarkName())) {
                    message.setFromUserName(friend.getRemarkName());
                }
                message.setLoadRemark(true);
            }
*/
            String name = message.getFromUserName();
            if (!TextUtils.isEmpty(name) && secret) {
                name = name.substring(0, name.length() - 1) + "*";
            }
            mTvName.setText(name);
        }
    }

    private void changeMessageState(ChatMessage message) {
        if (!isMysend && message.getMessageState() != ChatMessageListener.MESSAGE_SEND_SUCCESS) {
            message.setMessageState(ChatMessageListener.MESSAGE_SEND_SUCCESS);
        }

        int state = message.getMessageState();

        boolean read = false;
        if (ChatMessageListener.MESSAGE_SEND_SUCCESS == state) {
            // 单聊中，我发的显示已读或者送达， 群聊中开始显示人数
            if ((!isGounp && isMysend) || (isGounp && showPerson)) {
                read = true;
            }

            // 未读消息显示小红点
            if (enableUnRead()) {
                boolean show = message.isSendRead() || isMysend || message.getIsReadDel();
                ivUnRead.setVisibility(show ? View.GONE : View.VISIBLE);
            }

        } else {
            if (enableUnRead()) {
                ivUnRead.setVisibility(View.GONE);
            }
        }

        changeVisible(mTvSendState, read);
        changeVisible(mIvFailed, state == ChatMessageListener.MESSAGE_SEND_FAILED);
        changeVisible(mSendingBar, state == ChatMessageListener.MESSAGE_SEND_ING);
        changeSendText(message);
    }

    private void changeSendText(ChatMessage message) {
        mTvSendState.setOnClickListener(null);
        if (isGounp) {
            if (showPerson) {
                int count = message.getReadPersons();
                mTvSendState.setText(count + getString(R.string.people));
                mTvSendState.setBackgroundResource(R.drawable.bg_send_read);
                mTvSendState.setOnClickListener(this);
            }
        } else {
            if (message.isSendRead()) {
                mTvSendState.setText(R.string.status_read);
                mTvSendState.setBackgroundResource(R.drawable.bg_send_read);
            } else {
                mTvSendState.setText(R.string.status_send);
                mTvSendState.setBackgroundResource(R.drawable.bg_send_to);
            }
        }
    }

    /**
     * 如果是一条普通的消息，那么就会有发送状态和头像等，如果是特殊的消息需要重写此方法并清空方法体
     *
     * @param view
     * @see SystemViewHolder
     */
    private void inflateNormal(View view) {
        mTvTime = view.findViewById(R.id.time_tv);
        mIvHead = view.findViewById(R.id.chat_head_iv);
        mTvName = view.findViewById(R.id.nick_name);
        mSendingBar = view.findViewById(R.id.progress);
        mIvFailed = view.findViewById(R.id.iv_failed);
        mTvSendState = view.findViewById(R.id.tv_read);

        mCboxSelect = view.findViewById(R.id.chat_msc);

        mIvHead.setOnClickListener(this);
        mIvFailed.setOnClickListener(this);
        mCboxSelect.setOnClickListener(this);
        if (isGounp) {
            mIvHead.setOnLongClickListener(this);
        }
    }

    public void showTime(String time) {
        if (mTvTime == null) {
            return;
        }

        if (!TextUtils.isEmpty(time)) {
            mTvTime.setVisibility(View.VISIBLE);
            mTvTime.setText(time);
        } else {
            mTvTime.setVisibility(View.GONE);
        }
    }

    public void setMultiple(boolean multiple) {
        isMultiple = multiple;
        if (enableNormal()) {
            mCboxSelect.setVisibility(isMultiple ? View.VISIBLE : View.GONE);
        }
    }

    public void setShowPerson(boolean showPerson) {
        this.showPerson = showPerson;
    }

    public void changeVisible(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public String getString(@StringRes int sid) {
        return mContext.getResources().getString(sid);
    }

    public String getString(@StringRes int sid, String splice) {
        return mContext.getResources().getString(sid, splice);
    }

    public int dp2px(float dpValue) {
        float density = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }

    @Override
    public void onClick(View v) {
        if (v == mRootView && !isMultiple) {
            // 让子类获得点击事件
            onRootClick(v);
        }
        callOnItemClick(v);
    }

    private void callOnItemClick(View v) {
        if (mHolderListener != null) {
            // 让外界获得点击事件
            mHolderListener.onItemClick(v, this, mdata);
        }
    }

    protected void callOnReplayClick(View v) {
        if (mHolderListener != null) {
            // 让外界获得点击事件
            mHolderListener.onReplayClick(v, this, mdata);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mHolderListener != null) {
            mHolderListener.onItemLongClick(v, this, mdata);
        }
        return true;
    }

    public void setBoxSelect(boolean select) {
        if (mCboxSelect != null) {
            // mCboxSelect.setVisibility(visible ? View.VISIBLE : View.GONE);
            mCboxSelect.setChecked(select);
        }
    }

    public void sendReadMessage(ChatMessage message) {
        if (ivUnRead != null) {
            ivUnRead.setVisibility(View.GONE);
        }

        if (message.isMySend()) {
            return;
        }

        if (message.isSendRead()) {
            return;
        }

        // 群里的隐身人不发已读，
        if (!RoomMember.shouldSendRead(selfGroupRole)) {
            return;
        }

        if (isGounp && !showPerson) {// 自己发送的消息不发已读
            message.setSendRead(true);
            ChatMessageDao.getInstance().updateMessageRead(mLoginUserId, mToUserId, message.getPacketId(), true);
            return;
        }

        Intent intent = new Intent();
        intent.setAction(com.ydd.zhichat.broadcast.OtherBroadcast.Read);
        Bundle bundle = new Bundle();
        bundle.putString("packetId", message.getPacketId());
        bundle.putBoolean("isGroup", isGounp);
        if (message.getFromUserId().equals(message.getToUserId())) {// 我的设备
            bundle.putString("friendId", mLoginUserId);
        } else {
            bundle.putString("friendId", mToUserId);
        }
        bundle.putString("fromUserName", mLoginNickName);
        intent.putExtras(bundle);
        mContext.sendBroadcast(intent);
        message.setSendRead(true); // 自动发送的已读消息，先给一个已读标志，等有消息回执确认发送成功后在去修改数据库
    }

    // 默认关闭阅后即焚消息, 需要子类自行开启
    public boolean enableFire() {
        return false;
    }

    // 默认关闭未读消息显示红点功能,
    public boolean enableUnRead() {
        return false;
    }

    // 是否是普通消息 普通消息有发送状态，头像，昵称
    public boolean enableNormal() {
        return true;
    }

    // 默认开启长按事件，如果不需要可以子类重写 返回false
    public boolean isLongClick() {
        return true;
    }

    // 默认开启长按事件，如果不需要可以子类重写 返回false
    public boolean isOnClick() {
        return true;
    }

    // 默认关闭自动发送已读消息
    public boolean enableSendRead() {
        return false;
    }

    public void addChatHolderListener(ChatHolderListener listener) {
        mHolderListener = listener;
    }
}
