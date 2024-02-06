package com.sarin.prod.goodshost;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.LifecycleObserver;

import com.github.mikephil.charting.data.Entry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sarin.prod.goodshost.item.ReturnMsgItem;
import com.sarin.prod.goodshost.item.UserItem;
import com.sarin.prod.goodshost.network.RetrofitApi;
import com.sarin.prod.goodshost.network.RetrofitClientInstance;
import com.sarin.prod.goodshost.network.RetrofitInterface;
import com.sarin.prod.goodshost.util.PreferenceManager;
import com.sarin.prod.goodshost.util.StringUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainApplication extends Application implements Application.ActivityLifecycleCallbacks, LifecycleObserver {

    public static final String TAG = "SARIN_LOG";

    public static String ANDROID_ID = "";
    private Activity currentActivity;
    public static Context context;
    public static Activity activity;

    int count = 0;


    static StringUtil stringUtil = StringUtil.getInstance();

    public MainApplication() {
        context = this;

    }

    @Override
    public void onCreate(){

        super.onCreate();
        //Log.d(TAG, "MainApplication START");
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        context = this;

        String userId = PreferenceManager.getString(getApplicationContext(), "userId");
        // userId가 null이면
        if(stringUtil.nullCheck(userId)){

            try{
                ANDROID_ID = Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);
            }catch(Exception e){

            }
            if(stringUtil.nullCheck(ANDROID_ID)){
                long currentTimeMillis = System.currentTimeMillis();
                String deviceModel = android.os.Build.MODEL;
                ANDROID_ID = deviceModel + Long.toString(currentTimeMillis);
                ANDROID_ID = ANDROID_ID.trim();
            }

        }
        // userId가 기존 있으면.
        else{
            ANDROID_ID = userId;
        }

        Log.d(TAG,"ANDROID_ID : " + ANDROID_ID);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
//                Log.d(TAG,"task.isSuccessful() : " + task.isSuccessful());
                if(task.isSuccessful() == false) {
//                    Log.d(TAG,"Fetching FCM registration token failed : " + task.getException());
                    return;
                }
                String token = task.getResult();
//                Log.d(TAG,"Fetching FCM token : " + token);
                if(!stringUtil.nullCheck(token)){
                    UserItem userItem = new UserItem();
                    userItem.setUser_id(ANDROID_ID);
                    userItem.setFcm_token(token);
                    setUserRegister(userItem);
                }
            }
        });

        this.registerActivityLifecycleCallbacks(this);
        //Log.d(TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion());

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

                    PreferenceManager.setString(getApplicationContext(), "userId", MainApplication.ANDROID_ID);

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


    /** ActivityLifecycleCallback methods. */
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

//        Log.d(TAG, "onActivityCreated: " + activity.getLocalClassName());
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
//        Log.d(TAG, "onActivityStarted: " + activity.getLocalClassName());
        this.activity = activity;
        if (++count == 1) {
//            Log.d(TAG, "포그라운드." + activity.getLocalClassName());

        }

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {}

    @Override
    public void onActivityPaused(@NonNull Activity activity) {}

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
//        Log.d(TAG, "onActivityStopped: " + activity.getLocalClassName());

        if (--count == 0) {
//            Log.d(TAG, "백그라운드." + activity.getLocalClassName());

        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {}

}