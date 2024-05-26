package com.ydd.zhichat.bean;

import java.io.Serializable;

public class UUidShop implements Serializable {
    private String id;
    private String uid;//uid号码
    private int price;//金币

    public void setId(String id) {
        this.id = id;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public int getPrice() {
        return price;
    }
}
