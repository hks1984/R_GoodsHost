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

    public int convertStringToInt(String numberStr) {
        try {
            // 쉼표 제거
            String numberWithoutComma = numberStr.replace(",", "");

            // 정수로 변환
            return Integer.parseInt(numberWithoutComma);
        } catch (NumberFormatException e) {
            // 변환 중 오류 발생 시 적절한 처리 (여기서는 -1 반환)
            return 0;
        }
    }


    public String replaceHttp(String url) {
        String modifiedUrl = url.replace("https:", "coupang:").replace("http:", "coupang:");
        modifiedUrl = modifiedUrl.replace("www.coupang.com", "deeplink").replace("link.coupang.com", "deeplink");

        return modifiedUrl;
    }


    public boolean nullCheck(String str) {
        // null 이면 true
        return str == null || str.isEmpty();

    }


}
