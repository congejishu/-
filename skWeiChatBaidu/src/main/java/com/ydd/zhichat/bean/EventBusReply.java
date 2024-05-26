package com.ydd.zhichat.bean;

import com.ydd.zhichat.bean.message.ChatMessage;

public class EventBusReply {
    private int messageType;
    private ChatMessage object;

    public EventBusReply(){

    }

    public EventBusReply(int messageType,ChatMessage object) {
        this.messageType=messageType;
        this.object=object;
    }

    public int getMessageType() {
        return messageType;
    }

    public ChatMessage getObject() {
        return object;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public void setObject(ChatMessage object) {
        this.object = object;
    }
}
