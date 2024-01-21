package com.sarin.prod.goodshost.item;

public class RecentSearcherItem {
    private String date;
    private String name;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "RecentSearcherItem{" +
                "date='" + date + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
