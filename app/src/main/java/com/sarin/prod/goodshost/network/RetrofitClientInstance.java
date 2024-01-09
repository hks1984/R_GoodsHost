package com.sarin.prod.goodshost.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {

    private static Retrofit retrofit;
    // BaseUrl등록
    private static final String BASE_URL = "http://192.168.10.70:9090/test/";
//    private static final String BASE_URL = "http://192.168.0.2:9090/test/";
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