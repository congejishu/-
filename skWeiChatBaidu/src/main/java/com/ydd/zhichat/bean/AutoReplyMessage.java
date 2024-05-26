package com.ydd.zhichat.bean;

import java.util.ArrayList;
import java.util.List;

public class AutoReplyMessage implements java.io.Serializable {
    private Integer userId;
    private Integer restoreIndex;
    private ArrayList<Restores> restoreList;
    private Integer restoreStatue;
    private Integer restoreStatue01;

    public Integer getRestoreStatue01() {
        return restoreStatue01;
    }

    public void setRestoreStatue01(Integer restoreStatue01) {
        this.restoreStatue01 = restoreStatue01;
    }

    public Integer getRestoreStatue() {
        return restoreStatue;
    }

    public void setRestoreStatue(Integer restoreStatue) {
        this.restoreStatue = restoreStatue;
    }

    public Integer getRestoreIndex() {
        return restoreIndex;
    }

    public Integer getUserId() {
        return userId;
    }

    public ArrayList<Restores> getRestoreList() {
        return restoreList;
    }

    public void setRestoreIndex(Integer restoreIndex) {
        this.restoreIndex = restoreIndex;
    }

    public void setRestoreList(ArrayList<Restores> restoreList) {
        this.restoreList = restoreList;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public static final class Restores implements java.io.Serializable{
        private String text;//内容
        private Integer status;// 状态（1=关注；2=好友；0=陌生人 ；-1=黑名单）

        public Restores(){

        }

        public Integer getStatus() {
            return status;
        }

        public String getText() {
            return text;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

}
