package com.sarin.prod.goodshost.item;

public class DefaultAlarmItem {
    private String c_date;
    private String default_title;
    private String default_body;


    public String getC_date() {
        return c_date;
    }

    public void setC_date(String c_date) {
        this.c_date = c_date;
    }

    public String getDefault_title() {
        return default_title;
    }

    public void setDefault_title(String default_title) {
        this.default_title = default_title;
    }

    public String getDefault_body() {
        return default_body;
    }

    public void setDefault_body(String default_body) {
        this.default_body = default_body;
    }

    @Override
    public String toString() {
        return "DefaultAlarmItem{" +
                "c_date='" + c_date + '\'' +
                ", default_title='" + default_title + '\'' +
                ", default_body='" + default_body + '\'' +
                '}';
    }
}
