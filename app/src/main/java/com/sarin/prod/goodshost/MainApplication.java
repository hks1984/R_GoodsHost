package com.sarin.prod.goodshost;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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
import com.sarin.prod.goodshost.view.PopupDialog;
import com.sarin.prod.goodshost.view.PopupDialogClickListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.sarin.prod.goodshost.view.PopupDialogUtil;
import com.sarin.prod.goodshost.view.PopupDialogClickListener;


public class MainApplication extends Application implements Application.ActivityLifecycleCallbacks, LifecycleObserver {

    public static final String TAG = "SARIN_LOG";

    public static String ANDROID_ID = "";
    private final static int NOTICATION_ID = 222;
    private Activity currentActivity;
    public static Context context;
    public static Activity activity;
    public static final String BASE_URL = "https://dealdive.co.kr/dealdive/";
//    public static final String BASE_URL = "http://192.168.10.70:8080/dealdive/";
//    public static final String BASE_URL = "http://192.168.0.2:8080/dealdive/";



    int count = 0;


    static StringUtil stringUtil = StringUtil.getInstance();

    public MainApplication() {
        context = this;

    }

    @Override
    public void onCreate(){

        super.onCreate();
        
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

        this.registerActivityLifecycleCallbacks(this);
        

    }





    /** ActivityLifecycleCallback methods. */
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {


    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

        this.activity = activity;
        if (++count == 1) {


        }

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {}

    @Override
    public void onActivityPaused(@NonNull Activity activity) {}

    @Override
    public void onActivityStopped(@NonNull Activity activity) {


        if (--count == 0) {


        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {}

}