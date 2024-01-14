package com.sarin.prod.goodshost.util;

import java.text.NumberFormat;
import java.util.Locale;

public class StringUtil {

    private static StringUtil mInstnace = null;

    public static StringUtil getInstance() {
        if (mInstnace == null) {
            mInstnace = new StringUtil();
        }

        return mInstnace;
    }

    public static String replaceStringPriceToInt(int value) {
        String formattedValue = "";
        try {
            NumberFormat formatter = NumberFormat.getNumberInstance(Locale.KOREA);
            formattedValue = formatter.format(value);
        } catch (Exception e) {

        }
        return formattedValue;
    }

    public static String replaceIntToPrice(int value) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.KOREA);
        return formatter.format(value);
    }

    public boolean nullCheck(String str) {
        // null 이면 true
        return str == null || str.isEmpty();

    }


}
