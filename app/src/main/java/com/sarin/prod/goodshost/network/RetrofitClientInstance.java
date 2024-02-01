package com.sarin.prod.goodshost.network;

import android.util.Log;

import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.fragment.home.HomeFragment;
import com.sarin.prod.goodshost.item.ProductItem;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {

    public static String TAG = MainApplication.TAG;
    private static Retrofit retrofit;

    // BaseUrl등록
    private static final String BASE_URL = "http://192.168.10.70:8080/dealdive/";
//    private static final String BASE_URL = "http://192.168.0.2:8080/dealdive/";
//    private static final String BASE_URL = "https://api.dealdive.co.kr/dealdive/";

    private static RetrofitClientInstance mInstnace = null;

    public static RetrofitClientInstance getInstance() {
        if (mInstnace == null) {
            mInstnace = new RetrofitClientInstance();
        }

        return mInstnace;
    }
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    // Json을 변환해줄 Gson변환기 등록
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }




}