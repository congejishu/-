package com.ydd.zhichat.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class AttentionUser {
    private int blacklist; // 0 表示不是黑名单，1表示是在黑名单
    private int isBeenBlack;// 1表示对方将我拉入黑名单
    private int status;
    private String userId;// 发起关注的人
    private String toUserId;// 被关注的人
    private int toUserType;// 2 公众号
    @JSONField(name = "toNickname")
    private String toNickName;
    private String remarkName;
    private String describe;

    private int offlineNoPushMsg;// 消息免打扰
    private int isOpenSnapchat;// 阅后即焚
    private long openTopChatTime;// 置顶聊天
    private double chatRecordTimeOut;//0 || -1 消息永久保存 单位：day

    private int createTime;
    private int modifyTime;// 修改时间
    private int groupId;// 分组Id
    private String groupName;// 分组名称
    private int companyId;
    private Integer vip;
    private Integer uidIndex;
    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Integer getUidIndex() {
        return uidIndex;
    }

    public void setUidIndex(Integer uidIndex) {
        this.uidIndex = uidIndex;
    }

    public void setVip(Integer vip) {
        this.vip = vip;
    }

    public Integer getVip() {
        return vip;
    }

    public int getBlacklist() {
        return blacklist;
    }

    public void setBlacklist(int blacklist) {
        this.blacklist = blacklist;
    }

    public int getIsBeenBlack() {
        return isBeenBlack;
    }

    public void setIsBeenBlack(int isBeenBlack) {
        this.isBeenBlack = isBeenBlack;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public int getToUserType() {
        return toUserType;
    }

    public void setToUserType(int toUserType) {
        this.toUserType = toUserType;
    }

    public String getToNickName() {
        return toNickName;
    }

    public void setToNickName(String toNickName) {
        this.toNickName = toNickName;
    }

    public String getRemarkName() {
        return remarkName;
    }

    public void setRemarkName(String remarkName) {
        this.remarkName = remarkName;
    }

    public int getOfflineNoPushMsg() {
        return offlineNoPushMsg;
    }

    public void setOfflineNoPushMsg(int offlineNoPushMsg) {
        this.offlineNoPushMsg = offlineNoPushMsg;
    }

    public int getIsOpenSnapchat() {
        return isOpenSnapchat;
    }

    public void setIsOpenSnapchat(int isOpenSnapchat) {
        this.isOpenSnapchat = isOpenSnapchat;
    }

    public long getOpenTopChatTime() {
        return openTopChatTime;
    }

    public void setOpenTopChatTime(long openTopChatTime) {
        this.openTopChatTime = openTopChatTime;
    }

    public double getChatRecordTimeOut() {
        return chatRecordTimeOut;
    }

    public void setChatRecordTimeOut(double chatRecordTimeOut) {
        this.chatRecordTimeOut = chatRecordTimeOut;
    }

    public int getCreateTime() {
        return createTime;
    }

    public void setCreateTime(int createTime) {
        this.createTime = createTime;
    }

    public int getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(int modifyTime) {
        this.modifyTime = modifyTime;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
