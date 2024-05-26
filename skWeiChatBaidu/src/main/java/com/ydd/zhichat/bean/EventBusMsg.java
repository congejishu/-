package com.ydd.zhichat.bean;

import com.ydd.zhichat.bean.message.ChatMessage;

public class EventBusMsg {
    private int messageType;
    private ChatMessage object;
    private String content;

    public EventBusMsg() {
    }

    public EventBusMsg(int ebsType) {
        this.messageType = ebsType;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public ChatMessage getObject() {
        return object;
    }

    public void setObject(ChatMessage object) {
        this.object = object;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
