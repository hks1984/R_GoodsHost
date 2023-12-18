package com.sarin.prod.goodshost;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.LifecycleObserver;

public class MainApplication extends Application implements Application.ActivityLifecycleCallbacks, LifecycleObserver {

    public static final String TAG = "SARIN_LOG";
    private Activity currentActivity;
    Context context;

    int count = 0;


    public MainApplication() {
        context = this;

    }

    @Override
    public void onCreate(){

        super.onCreate();
        //Log.d(TAG, "MainApplication START");
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        context = this;

        this.registerActivityLifecycleCallbacks(this);
        //Log.d(TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion());


    }

    /** ActivityLifecycleCallback methods. */
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {}

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

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
        if (--count == 0) {
//            Log.d(TAG, "백그라운드." + activity.getLocalClassName());
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {}

}