package com.sarin.prod.goodshost.util;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 데이터 저장 및 로드 클래스
 */

public class PreferenceManager {

    public static final String PREFERENCES_NAME = "rebuild_preference";

    private static final String DEFAULT_VALUE_STRING = "";

    private static final boolean DEFAULT_VALUE_BOOLEAN = false;

    private static final int DEFAULT_VALUE_INT = -1;

    private static final long DEFAULT_VALUE_LONG = -1L;

    private static final float DEFAULT_VALUE_FLOAT = -1F;

    private static Gson gson = new Gson();;


    private static SharedPreferences getPreferences(Context context) {

        return context.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);

    }


    /**
     * String 값 저장
     *
     * @param context
     * @param key
     * @param value
     */

    public static void setString(Context context, String key, String value) {

        SharedPreferences prefs = getPreferences(context);

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(key, value);

        editor.commit();

    }


    /**
     * boolean 값 저장
     *
     * @param context
     * @param key
     * @param value
     */

    public static void setBoolean(Context context, String key, boolean value) {

        SharedPreferences prefs = getPreferences(context);

        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean(key, value);

        editor.commit();

    }


    /**
     * int 값 저장
     *
     * @param context
     * @param key
     * @param value
     */

    public static void setInt(Context context, String key, int value) {

        SharedPreferences prefs = getPreferences(context);

        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(key, value);

        editor.commit();

    }


    /**
     * long 값 저장
     *
     * @param context
     * @param key
     * @param value
     */

    public static void setLong(Context context, String key, long value) {

        SharedPreferences prefs = getPreferences(context);

        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong(key, value);

        editor.commit();

    }


    /**
     * float 값 저장
     *
     * @param context
     * @param key
     * @param value
     */

    public static void setFloat(Context context, String key, float value) {

        SharedPreferences prefs = getPreferences(context);

        SharedPreferences.Editor editor = prefs.edit();

        editor.putFloat(key, value);

        editor.commit();

    }

    public static void setStringList(Context context, String key, String newString) {
        SharedPreferences prefs = getPreferences(context);

        if(newString == null || "".equals(newString)){
            return;
        }
        List<String> stringList = getStringList(context, key);
        if (stringList == null) {
            stringList = new ArrayList<>();
        }

        stringList.remove(newString);

        // 새 항목을 리스트의 맨 앞에 추가
        stringList.add(0, newString);

        // 리스트 크기가 최대치를 초과하는 경우, 가장 오래된 항목(마지막 항목) 삭제
        if (stringList.size() > 10) {
            stringList.remove(10);
        }

        String json = gson.toJson(stringList);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, json);
        editor.apply();
    }

    public static void delStringList(Context context, String key, String newString) {
        SharedPreferences prefs = getPreferences(context);

        List<String> stringList = getStringList(context, key);
        if (stringList == null) {
            stringList = new ArrayList<>();
        }
        stringList.remove(newString);

        String json = gson.toJson(stringList);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, json);
        editor.apply();
    }

    public static List<String> getStringList(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        String json = prefs.getString(key, null);
        Type type = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(json, type);
    }



    private void saveStringList(List<String> stringList) {

    }




    /**
     * String 값 로드
     *
     * @param context
     * @param key
     * @return
     */

    public static String getString(Context context, String key) {

        SharedPreferences prefs = getPreferences(context);

        String value = prefs.getString(key, DEFAULT_VALUE_STRING);

        return value;

    }


    /**
     * boolean 값 로드
     *
     * @param context
     * @param key
     * @return
     */

    public static boolean getBoolean(Context context, String key) {

        SharedPreferences prefs = getPreferences(context);

        boolean value = prefs.getBoolean(key, DEFAULT_VALUE_BOOLEAN);

        return value;

    }


    /**
     * int 값 로드
     *
     * @param context
     * @param key
     * @return
     */

    public static int getInt(Context context, String key) {

        SharedPreferences prefs = getPreferences(context);

        int value = prefs.getInt(key, DEFAULT_VALUE_INT);

        return value;

    }


    /**
     * long 값 로드
     *
     * @param context
     * @param key
     * @return
     */

    public static long getLong(Context context, String key) {

        SharedPreferences prefs = getPreferences(context);

        long value = prefs.getLong(key, DEFAULT_VALUE_LONG);

        return value;

    }


    /**
     * float 값 로드
     *
     * @param context
     * @param key
     * @return
     */

    public static float getFloat(Context context, String key) {

        SharedPreferences prefs = getPreferences(context);

        float value = prefs.getFloat(key, DEFAULT_VALUE_FLOAT);

        return value;

    }


    /**
     * 키 값 삭제
     *
     * @param context
     * @param key
     */

    public static void removeKey(Context context, String key) {

        SharedPreferences prefs = getPreferences(context);

        SharedPreferences.Editor edit = prefs.edit();

        edit.remove(key);

        edit.commit();

    }


    /**
     * 모든 저장 데이터 삭제
     *
     * @param context
     */

    public static void clear(Context context) {

        SharedPreferences prefs = getPreferences(context);

        SharedPreferences.Editor edit = prefs.edit();

        edit.clear();

        edit.commit();

    }

}
