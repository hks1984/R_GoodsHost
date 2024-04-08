package com.sarin.prod.goodshost.network;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import android.content.Context;
import android.util.Log;

import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.item.ReturnMsgItem;
import com.sarin.prod.goodshost.item.UserItem;
import com.sarin.prod.goodshost.util.PreferenceManager;
import com.sarin.prod.goodshost.util.StringUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TokenUploadWorker extends Worker {

    private static String TAG = MainApplication.TAG;
    static StringUtil su = StringUtil.getInstance();

    public TokenUploadWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {

        boolean success = true;
        // FCM 토큰을 서버로 전송하는 로직 구현
        int fcmFlag = PreferenceManager.getInt(getApplicationContext(), "fcmFlag");

        if(fcmFlag > 0){
            success = uploadTokenToServer();
        }

        return success ? Result.success() : Result.retry();
    }

    private boolean uploadTokenToServer() {
        String userId = PreferenceManager.getString(getApplicationContext(), "userId");
        String androidId = PreferenceManager.getString(getApplicationContext(), "androidId");
        String fcmToken = PreferenceManager.getString(getApplicationContext(), "fcmToken");

        if(su.nullCheck(userId) || su.nullCheck(fcmToken)) {
            return false;
        }

        UserItem userItem = new UserItem();
        userItem.setUser_id(userId);
        userItem.setAndroid_id(androidId);
        userItem.setFcm_token(fcmToken);
        setFcmTokenUpdate(userItem);

        return true; // 임시 코드
    }


    public void setFcmTokenUpdate(UserItem userItem){

        retrofit2.Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);   // 레트로핏 인터페이스 객체 구현

        Call<ReturnMsgItem> call = service.setFcmTokenUpdate("setFcmTokenUpdate", userItem);
        call.enqueue(new Callback<ReturnMsgItem>() {
            @Override
            public void onResponse(Call<ReturnMsgItem> call, Response<ReturnMsgItem> response) {
                if(response.isSuccessful()){
                    ReturnMsgItem returnMsgItem = response.body();

                    if(returnMsgItem.getCode() > 0){    // 성공
                        PreferenceManager.setInt(getApplicationContext(), "fcmFlag", 0);
                    } else {
                        PreferenceManager.setInt(getApplicationContext(), "fcmFlag", 1);
                    }

                }
                else{
                    PreferenceManager.setInt(getApplicationContext(), "fcmFlag", 1);
                }
            }
            @Override
            public void onFailure(Call<ReturnMsgItem> call, Throwable t) {
                PreferenceManager.setInt(getApplicationContext(), "fcmFlag", 1);
            }

        });

    }




}
