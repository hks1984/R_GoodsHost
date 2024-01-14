package com.sarin.prod.goodshost.network;

import android.util.Log;

import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.item.UserItem;
import com.sarin.prod.goodshost.item.ReturnMsgItem;
import com.sarin.prod.goodshost.util.StringUtil;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitApi {

    public static String TAG = MainApplication.TAG;

    private static RetrofitApi mInstnace = null;

    public static RetrofitApi getInstance() {
        if (mInstnace == null) {
            mInstnace = new RetrofitApi();
        }

        return mInstnace;
    }

    public void setUserRegister(UserItem userItem){

        retrofit2.Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);   // 레트로핏 인터페이스 객체 구현

        Call<ReturnMsgItem> call = service.setUserRegister("setUserRegister", userItem.getUser_id(), userItem.getFcm_token());
        call.enqueue(new Callback<ReturnMsgItem>() {
            @Override
            public void onResponse(Call<ReturnMsgItem> call, Response<ReturnMsgItem> response) {
                if(response.isSuccessful()){
                    ReturnMsgItem returnMsgItem = response.body();
                    Log.d(TAG, "setUserRegister : " + returnMsgItem.toString());

                }
                else{
                    Log.e(TAG, "실패 코드 확인 : " + response.code());
                    Log.e(TAG, "연결 주소 확인 : " + response.raw().request().url().url());
                }
            }
            @Override
            public void onFailure(Call<ReturnMsgItem> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });


    }
}
