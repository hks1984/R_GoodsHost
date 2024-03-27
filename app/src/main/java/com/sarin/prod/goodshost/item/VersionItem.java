package com.sarin.prod.goodshost.item;

public class VersionItem {

    private String os_rype;
    private String app_version;
    private int req_update;

    public String getOs_rype() {
        return os_rype;
    }

    public void setOs_rype(String os_rype) {
        this.os_rype = os_rype;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public int getReq_update() {
        return req_update;
    }

    public void setReq_update(int req_update) {
        this.req_update = req_update;
    }

    @Override
    public String toString() {
        return "VersionItem{" +
                "os_rype='" + os_rype + '\'' +
                ", app_version='" + app_version + '\'' +
                ", req_update=" + req_update +
                '}';
    }
}
