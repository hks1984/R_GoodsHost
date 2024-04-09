package com.sarin.prod.goodshost.item;

public class UserItem {
    private String user_id;
    private String soical;
    private String user_nick;
    private String android_id;
    private String model_name;
    private String os_ver;
    private String fcm_token;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getSoical() {
        return soical;
    }

    public void setSoical(String soical) {
        this.soical = soical;
    }

    public String getFcm_token() {
        return fcm_token;
    }

    public void setFcm_token(String fcm_token) {
        this.fcm_token = fcm_token;
    }

    public String getUser_nick() {
        return user_nick;
    }

    public void setUser_nick(String user_nick) {
        this.user_nick = user_nick;
    }

    public String getAndroid_id() {
        return android_id;
    }

    public void setAndroid_id(String android_id) {
        this.android_id = android_id;
    }

    public String getModel_name() {
        return model_name;
    }

    public void setModel_name(String model_name) {
        this.model_name = model_name;
    }

    public String getOs_ver() {
        return os_ver;
    }

    public void setOs_ver(String os_ver) {
        this.os_ver = os_ver;
    }

    @Override
    public String toString() {
        return "UserItem{" +
                "user_id='" + user_id + '\'' +
                ", soical='" + soical + '\'' +
                ", user_nick='" + user_nick + '\'' +
                ", android_id='" + android_id + '\'' +
                ", model_name='" + model_name + '\'' +
                ", os_ver='" + os_ver + '\'' +
                ", fcm_token='" + fcm_token + '\'' +
                '}';
    }
}
