package com.sarin.prod.goodshost.item;

public class ProductItem {


    public String product_id;
    public String item_id;
    public String vendor_item_id;
    public String category_id;
    public String link;
    public String name;
    public String image;
    public String out_of_stock;
    public int price_value;
    public String discount_percentage;
    public int base_price;
    public String rocket_baesong;
    public String rating;
    public int rating_total_count;
    public String page_link;
    public String coupang_link_use;
    public String coupang_link;
    public String updateSeparator;
    public String persent;
    public String rank;


    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getVendor_item_id() {
        return vendor_item_id;
    }

    public void setVendor_item_id(String vendor_item_id) {
        this.vendor_item_id = vendor_item_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOut_of_stock() {
        return out_of_stock;
    }

    public void setOut_of_stock(String out_of_stock) {
        this.out_of_stock = out_of_stock;
    }

    public int getPrice_value() {
        return price_value;
    }

    public void setPrice_value(int price_value) {
        this.price_value = price_value;
    }

    public String getDiscount_percentage() {
        return discount_percentage;
    }

    public void setDiscount_percentage(String discount_percentage) {
        this.discount_percentage = discount_percentage;
    }

    public int getBase_price() {
        return base_price;
    }

    public void setBase_price(int base_price) {
        this.base_price = base_price;
    }

    public String getRocket_baesong() {
        return rocket_baesong;
    }

    public void setRocket_baesong(String rocket_baesong) {
        this.rocket_baesong = rocket_baesong;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public int getRating_total_count() {
        return rating_total_count;
    }

    public void setRating_total_count(int rating_total_count) {
        this.rating_total_count = rating_total_count;
    }

    public String getPage_link() {
        return page_link;
    }

    public void setPage_link(String page_link) {
        this.page_link = page_link;
    }

    public String getCoupang_link_use() {
        return coupang_link_use;
    }

    public void setCoupang_link_use(String coupang_link_use) {
        this.coupang_link_use = coupang_link_use;
    }

    public String getCoupang_link() {
        return coupang_link;
    }

    public void setCoupang_link(String coupang_link) {
        this.coupang_link = coupang_link;
    }

    public String getUpdateSeparator() {
        return updateSeparator;
    }

    public void setUpdateSeparator(String updateSeparator) {
        this.updateSeparator = updateSeparator;
    }

    public String getPersent() {
        return persent;
    }

    public void setPersent(String persent) {
        this.persent = persent;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "ProductItem{" +
                "product_id='" + product_id + '\'' +
                ", item_id='" + item_id + '\'' +
                ", vendor_item_id='" + vendor_item_id + '\'' +
                ", category_id='" + category_id + '\'' +
                ", link='" + link + '\'' +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", out_of_stock='" + out_of_stock + '\'' +
                ", price_value='" + price_value + '\'' +
                ", discount_percentage='" + discount_percentage + '\'' +
                ", base_price='" + base_price + '\'' +
                ", rocket_baesong='" + rocket_baesong + '\'' +
                ", rating='" + rating + '\'' +
                ", rating_total_count='" + rating_total_count + '\'' +
                ", page_link='" + page_link + '\'' +
                ", coupang_link_use='" + coupang_link_use + '\'' +
                ", coupang_link='" + coupang_link + '\'' +
                ", updateSeparator='" + updateSeparator + '\'' +
                ", persent='" + persent + '\'' +
                ", rank='" + rank + '\'' +
                '}';
    }
}
