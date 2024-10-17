package com.sarin.prod.goodshost.item;

public class EventItem {

    private String title;
    private String image;
    private String link;
    private String end_time;
    private int count;
    private int order;
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }
    public String getEnd_time() {
        return end_time;
    }
    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public int getOrder() {
        return order;
    }
    public void setOrder(int order) {
        this.order = order;
    }
    @Override
    public String toString() {
        return "EventItem [title=" + title + ", image=" + image + ", link=" + link + ", end_time=" + end_time
                + ", count=" + count + ", order=" + order + "]";
    }



}
