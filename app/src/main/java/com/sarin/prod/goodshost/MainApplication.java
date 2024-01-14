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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sarin.prod.goodshost.item.UserItem;
import com.sarin.prod.goodshost.network.RetrofitApi;
import com.sarin.prod.goodshost.util.PreferenceManager;
import com.sarin.prod.goodshost.util.StringUtil;

public class MainApplication extends Application implements Application.ActivityLifecycleCallbacks, LifecycleObserver {

    public static final String TAG = "SARIN_LOG";

    public static String ANDROID_ID = "";
    private Activity currentActivity;
    public static Context context;
    public static Activity activity;

    int count = 0;


    static StringUtil stringUtil = StringUtil.getInstance();
    static RetrofitApi retrofitApi = RetrofitApi.getInstance();
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
        if(stringUtil.nullCheck(userId)){
            try{
                ANDROID_ID = Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);
            }catch(Exception e){

            }
            if(stringUtil.nullCheck(ANDROID_ID)){
                long currentTimeMillis = System.currentTimeMillis();
                ANDROID_ID = Long.toString(currentTimeMillis);
            }
            PreferenceManager.setString(getApplicationContext(), "userId", ANDROID_ID);
        }else{
            ANDROID_ID = userId;
        }

        Log.d(TAG, "ANDROID_ID: " + ANDROID_ID);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                Log.d(TAG,"task.isSuccessful() : " + task.isSuccessful());
                if(task.isSuccessful() == false) {
                    Log.d(TAG,"Fetching FCM registration token failed : " + task.getException());
                    return;
                }
                String token = task.getResult();
                Log.d(TAG,"Fetching FCM token : " + token);
                if(!stringUtil.nullCheck(token)){
                    UserItem userItem = new UserItem();
                    userItem.setUser_id(ANDROID_ID);
                    userItem.setFcm_token(token);
                    retrofitApi.setUserRegister(userItem);
                }
            }
        });

        this.registerActivityLifecycleCallbacks(this);
        //Log.d(TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion());

    }

    /** ActivityLifecycleCallback methods. */
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "onActivityCreated: " + activity.getLocalClassName());
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        Log.d(TAG, "onActivityStarted: " + activity.getLocalClassName());
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
        Log.d(TAG, "onActivityStopped: " + activity.getLocalClassName());

        if (--count == 0) {
//            Log.d(TAG, "백그라운드." + activity.getLocalClassName());

        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {}

}