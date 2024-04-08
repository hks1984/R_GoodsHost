package com.sarin.prod.goodshost.activity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.activity.PermissionActivity;
import com.sarin.prod.goodshost.MainActivity;
import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.item.VersionItem;
import com.sarin.prod.goodshost.network.RetrofitClientInstance;
import com.sarin.prod.goodshost.network.RetrofitInterface;
import com.sarin.prod.goodshost.util.PreferenceManager;
import com.sarin.prod.goodshost.util.StringUtil;
import com.sarin.prod.goodshost.util.WritingTextView;
import com.sarin.prod.goodshost.view.PopupDialogClickListener;
import com.sarin.prod.goodshost.view.PopupDialogUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SplashActivity extends AppCompatActivity {

    private static String TAG = MainApplication.TAG;
    static StringUtil stringUtil = StringUtil.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getVersion("A");

    }

    public void getVersion(String os_type){

        retrofit2.Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);   // 레트로핏 인터페이스 객체 구현

        Call<VersionItem> call = service.getVersion("getVersion", os_type);
        call.enqueue(new Callback<VersionItem>() {
            @Override
            public void onResponse(Call<VersionItem> call, Response<VersionItem> response) {
                if(response.isSuccessful()){
                    VersionItem ver = response.body();
                    String newVersion = ver.getApp_version();
                    int flag = ver.getReq_update();
                    int rtn = stringUtil.compareVersion(MainApplication.VERSION, newVersion);

                    if(rtn > 0){
                        if(flag > 0) {
                            PopupDialogUtil.showCustomDialog(SplashActivity.this, new PopupDialogClickListener() {
                                @Override
                                public void onPositiveClick() {
                                    final String appPackageName = getApplicationContext().getPackageName();
                                    try {
                                        // Google Play Store 앱에서 앱의 페이지를 열기 위한 인텐트를 생성합니다.
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
                                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                                        getApplicationContext().startActivity(intent);
                                    } catch (ActivityNotFoundException e) {
                                        // Google Play Store 앱이 없는 경우 웹 브라우저를 사용해 앱 페이지를 엽니다.
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
                                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                                        getApplicationContext().startActivity(intent);

                                    }
                                    finish();

                                }
                                @Override
                                public void onNegativeClick() {

                                }
                            }, "ONE", getResources().getString(R.string.req_update));
                        }
                        else {
                            PopupDialogUtil.showCustomDialog(SplashActivity.this, new PopupDialogClickListener() {
                                @Override
                                public void onPositiveClick() {
                                    final String appPackageName = getApplicationContext().getPackageName();
                                    try {
                                        // Google Play Store 앱에서 앱의 페이지를 열기 위한 인텐트를 생성합니다.
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
                                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                                        getApplicationContext().startActivity(intent);
                                    } catch (ActivityNotFoundException e) {
                                        // Google Play Store 앱이 없는 경우 웹 브라우저를 사용해 앱 페이지를 엽니다.
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
                                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                                        getApplicationContext().startActivity(intent);

                                    }
                                }
                                @Override
                                public void onNegativeClick() {
                                    moveMain(1);
                                }
                            }, "TWO", getResources().getString(R.string.req_update_message));
                        }


                    } else {
                        moveMain(2);
                    }



                }
                else {
                    PopupDialogUtil.showCustomDialog(SplashActivity.this, new PopupDialogClickListener() {
                        @Override
                        public void onPositiveClick() {
                            finish();
                        }
                        @Override
                        public void onNegativeClick() {
                        }
                    }, "ONE", getResources().getString(R.string.server_not_connecting));
                }
            }
            @Override
            public void onFailure(Call<VersionItem> call, Throwable t) {
                PopupDialogUtil.showCustomDialog(SplashActivity.this, new PopupDialogClickListener() {
                    @Override
                    public void onPositiveClick() {
                        finish();
                    }
                    @Override
                    public void onNegativeClick() {
                    }
                }, "ONE", getResources().getString(R.string.server_not_connecting));
            }
        });


    }

    private void moveMain(int sec) {
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {

                String isPermission = PreferenceManager.getString(getApplicationContext(), "isPermission");
                String isLogin = PreferenceManager.getString(getApplicationContext(), "isLogin");

                if(stringUtil.nullCheck(isLogin)){
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }else if(stringUtil.nullCheck(isPermission)){
                    Intent intent = new Intent(getApplicationContext(), PermissionActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }

                finish();	//현재 액티비티 종료

            }
        }, 1000 * sec); // sec초 정도 딜레이를 준 후 시작
    }
}