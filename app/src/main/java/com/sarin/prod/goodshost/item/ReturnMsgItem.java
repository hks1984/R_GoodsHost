package com.sarin.prod.goodshost.item;

public class ReturnMsgItem {
    private int code;
    private String massage;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }

    @Override
    public String toString() {
        return "ReturnMsgItem{" +
                "code=" + code +
                ", massage='" + massage + '\'' +
                '}';
    }
}
