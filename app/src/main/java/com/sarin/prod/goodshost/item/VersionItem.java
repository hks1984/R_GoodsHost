package com.sarin.prod.goodshost.item;

public class VersionItem {

    private String os_type;
    private String app_version;
    private int req_update;

    public String getOs_type() {
        return os_type;
    }

    public void setOs_type(String os_type) {
        this.os_type = os_type;
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
                "os_type='" + os_type + '\'' +
                ", app_version='" + app_version + '\'' +
                ", req_update=" + req_update +
                '}';
    }
}
