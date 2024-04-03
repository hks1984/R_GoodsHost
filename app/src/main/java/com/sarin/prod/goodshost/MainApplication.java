package com.sarin.prod.goodshost;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
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
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.github.mikephil.charting.data.Entry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kakao.sdk.common.KakaoSdk;
import com.sarin.prod.goodshost.item.ReturnMsgItem;
import com.sarin.prod.goodshost.item.UserItem;
import com.sarin.prod.goodshost.network.RetrofitApi;
import com.sarin.prod.goodshost.network.RetrofitClientInstance;
import com.sarin.prod.goodshost.network.RetrofitInterface;
import com.sarin.prod.goodshost.util.PreferenceManager;
import com.sarin.prod.goodshost.util.StringUtil;
import com.sarin.prod.goodshost.network.TokenUploadWorker;
import com.sarin.prod.goodshost.view.PopupDialog;
import com.sarin.prod.goodshost.view.PopupDialogClickListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.sarin.prod.goodshost.view.PopupDialogUtil;
import com.sarin.prod.goodshost.view.PopupDialogClickListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.concurrent.TimeUnit;


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

        KakaoSdk.init(this,"cd6542e2cf71c211083caf3d8d5f8695");



        // FCM 토큰을 서버로 업데이트하지 못했을 때 주기적으로 업데이트하는 작업
        setupWorkManager();

        this.registerActivityLifecycleCallbacks(this);

        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e(TAG , "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d(TAG, Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e(TAG, "Unable to get MessageDigest. signature=" + signature, e);
            }
        }



    }

    private void setupWorkManager() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest tokenUploadRequest = new PeriodicWorkRequest.Builder(TokenUploadWorker.class, 20, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();

//        WorkManager.getInstance(this).enqueue(tokenUploadRequest);
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "uploadTokenWork", // 작업의 고유 이름
                ExistingPeriodicWorkPolicy.KEEP, // 기존 작업이 있다면 새 작업을 무시
                tokenUploadRequest // PeriodicWorkRequest 객체
        );

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