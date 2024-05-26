package com.ydd.zhichat.ui.message.multi;

public class EventGroupStatus {
    private final int whichStatus;
    private final int groupManagerStatus;
    private String passwrod;

    public String getPasswrod() {
        return passwrod;
    }

    public void setPasswrod(String passwrod) {
        this.passwrod = passwrod;
    }

    public EventGroupStatus(int whichStatus, int groupManagerStatus) {
        this.whichStatus = whichStatus;
        this.groupManagerStatus = groupManagerStatus;
    }

    public int getWhichStatus() {
        return whichStatus;
    }

    public int getGroupManagerStatus() {
        return groupManagerStatus;
    }
}
