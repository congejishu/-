package com.ydd.zhichat.view.spinner.model;

public class SpinnerModel {
    private String text ;
    private String value;
    //是否选中
    private boolean selectd=false;

    public SpinnerModel(String text,String value){
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isSelectd() {
        return selectd;
    }

    public void setSelectd(boolean state) {
        this.selectd = state;
    }

    public void clickd(){
        selectd = !selectd;
    }
}
