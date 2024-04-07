package com.sarin.prod.goodshost.item;

public class ReturnObjMsgItem {

    private ReturnMsgItem returnMsgItem;
    private VersionItem versionItem;

    public ReturnMsgItem getReturnMsgItem() {
        return returnMsgItem;
    }

    public void setReturnMsgItem(ReturnMsgItem returnMsgItem) {
        this.returnMsgItem = returnMsgItem;
    }

    public VersionItem getVersionItem() {
        return versionItem;
    }

    public void setVersionItem(VersionItem versionItem) {
        this.versionItem = versionItem;
    }

    @Override
    public String toString() {
        return "ReturnObjMsgItem{" +
                "returnMsgItem=" + returnMsgItem +
                ", versionItem=" + versionItem +
                '}';
    }
}
