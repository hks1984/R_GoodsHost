package com.sarin.prod.goodshost.item;

public class CategoryItem {
    public String code;
    public String name;
    public String pcode;
    public String SEQ_CHAR;
    public String level;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPcode() {
        return pcode;
    }

    public void setPcode(String pcode) {
        this.pcode = pcode;
    }

    public String getSEQ_CHAR() {
        return SEQ_CHAR;
    }

    public void setSEQ_CHAR(String SEQ_CHAR) {
        this.SEQ_CHAR = SEQ_CHAR;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "CategoryItem{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", pcode='" + pcode + '\'' +
                ", SEQ_CHAR='" + SEQ_CHAR + '\'' +
                ", level='" + level + '\'' +
                '}';
    }
}
