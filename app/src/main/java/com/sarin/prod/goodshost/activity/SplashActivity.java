package com.sarin.prod.goodshost.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.activity.PermissionActivity;
import com.sarin.prod.goodshost.MainActivity;
import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.util.PreferenceManager;
import com.sarin.prod.goodshost.util.StringUtil;
import com.sarin.prod.goodshost.util.WritingTextView;


public class SplashActivity extends AppCompatActivity {

    private static String TAG = MainApplication.TAG;
    static StringUtil stringUtil = StringUtil.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        WritingTextView writingTextView = findViewById(R.id.writingTextView);
        moveMain(2);
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