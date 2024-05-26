package com.ydd.zhichat.view.chatHolder;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.ydd.zhichat.R;
import com.ydd.zhichat.bean.Friend;
import com.ydd.zhichat.bean.RoomMember;
import com.ydd.zhichat.bean.User;
import com.ydd.zhichat.bean.message.ChatMessage;

class LiveChatViewHolder extends AChatHolderInterface {

    TextView tvName;
    TextView tvContent;

    @Override
    public int itemLayoutId(boolean isMysend) {
        return R.layout.chat_item_live_system;
    }

    @Override
    public void initView(View view) {
        tvName = view.findViewById(R.id.tv_name);
        tvContent = view.findViewById(R.id.tv_content);
    }

    @Override
    @SuppressLint("SetTextI18n")
    public void fillData(ChatMessage message) {
        String fromUserName = message.getFromUserName();
        if (TextUtils.equals(message.getFromUserId(), Friend.ID_SYSTEM_NOTIFICATION)) {
            fromUserName = mContext.getString(R.string.system_notification_user_name);
        }
        tvName.setText(fromUserName + ":");
        tvContent.setText(message.getContent());
    }

    @Override
    protected void onRootClick(View v) {

    }

    @Override
    public boolean isLongClick() {
        return false;
    }

    @Override
    public boolean isOnClick() {
        return false;
    }

    @Override
    public boolean enableNormal() {
        return false;
    }

    @Override
    protected void fillData(ChatMessage message, RoomMember member) {
        fillData(message);
    }

    @Override
    protected void fillData(ChatMessage message, Friend friend) {
        fillData(message);
    }

    @Override
    protected void fillData(ChatMessage message, User friend) {
        fillData(message);
    }
}