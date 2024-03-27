package com.sarin.prod.goodshost.util;

import android.util.Log;

import com.sarin.prod.goodshost.MainApplication;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    private static String TAG = MainApplication.TAG;
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

    public List<String> extractUrls(String text) {
        List<String> foundUrls = new ArrayList<String>();
        String regex = "\\b((?:https?|ftp)://[^\\s\"<]+[^\\s\"<]*)";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find()) {
            foundUrls.add(text.substring(urlMatcher.start(0), urlMatcher.end(0)));
        }
        return foundUrls;
    }

    /**
     * 입력된 가격에서 10%를 차감한 가격을 계산하여 반환합니다.
     * 입력 값이 음수인 경우, 0을 반환합니다.
     *
     * @param originalPrice 원래 가격
     * @return 10% 차감된 가격, 단, 결과가 음수일 경우 0을 반환
     */
    public int calculateDiscountedPrice(int originalPrice, int value) {
        
        // 입력 값 검증
        if (originalPrice < 0) {
            return 0; // 음수 입력은 유효하지 않으므로 0 반환
        }

        // 10% 차감 계산
        double discountAmount = originalPrice * ((double) value / 100);
        
        int discountedPrice = (int) (originalPrice - Math.floor(discountAmount)); // 소수점 버림 처리
        
        return discountedPrice;
    }

    public String getTimeAgo(long pastTimeMillis) {
        long currentTimeMillis = System.currentTimeMillis();
        long timeDiff = currentTimeMillis - pastTimeMillis;

        // 시간 차이를 분, 시간, 일 단위로 계산
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeDiff);
        long hours = TimeUnit.MILLISECONDS.toHours(timeDiff);
        long days = TimeUnit.MILLISECONDS.toDays(timeDiff);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");

//        if (minutes < 1) {
//            return "방금 전";
//        } else if (minutes < 60) {
//            return minutes + "분 전";
//        } else if (hours < 24) {
//            return hours + "시간 전";
//        } else if (days < 30) {
//            return days + "일 전";
//        } else if (days < 365) {
//            return days / 30 + "달 전";
//        } else {
//            return days / 365 + "년 전";
//        }

        if (minutes < 1) {
            return "방금 전";
        } else if (minutes < 60) {
            return minutes + "분 전";
        } else if (hours < 24) {
            return hours + "시간 전";
        } else {
            return dateFormat.format(pastTimeMillis);
        }
    }


    public int compareVersion(String v1, String v2) {
        // 버전을 '.' 기준으로 분할
        String[] v1Parts = v1.split("\\.");
        String[] v2Parts = v2.split("\\.");

        int length = Math.max(v1Parts.length, v2Parts.length);

        for (int i = 0; i < length; i++) {
            // 각 부분을 정수로 변환하여 비교
            int v1Part = i < v1Parts.length ? Integer.parseInt(v1Parts[i]) : 0;
            int v2Part = i < v2Parts.length ? Integer.parseInt(v2Parts[i]) : 0;

            if (v1Part < v2Part) {
                return 1;
            } else if (v1Part > v2Part) {
                return -1;
            }
        }

        // 모든 부분이 같다면 0 반환
        return 0;
    }



}
