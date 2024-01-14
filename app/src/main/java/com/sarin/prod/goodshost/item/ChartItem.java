package com.sarin.prod.goodshost.item;

public class ChartItem {
    private String c_date;
    private int max_value;
    private int min_value;

    public String getC_date() {
        return c_date;
    }

    public void setC_date(String c_date) {
        this.c_date = c_date;
    }

    public int getMax_value() {
        return max_value;
    }

    public void setMax_value(int max_value) {
        this.max_value = max_value;
    }

    public int getMin_value() {
        return min_value;
    }

    public void setMin_value(int min_value) {
        this.min_value = min_value;
    }

    @Override
    public String toString() {
        return "ChartItem{" +
                "c_date='" + c_date + '\'' +
                ", max_value=" + max_value +
                ", min_value=" + min_value +
                '}';
    }
}
