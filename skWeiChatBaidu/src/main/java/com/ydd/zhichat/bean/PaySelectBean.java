package com.ydd.zhichat.bean;

public class PaySelectBean {
    private String name;
    private String des;
    private String oldPrice;
    private String newPrice;
    private boolean isSelect;
    private boolean isRecommond;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(String oldPrice) {
        this.oldPrice = oldPrice;
    }

    public String getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(String newPrice) {
        this.newPrice = newPrice;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public boolean isRecommond() {
        return isRecommond;
    }

    public void setRecommond(boolean recommond) {
        isRecommond = recommond;
    }

}
