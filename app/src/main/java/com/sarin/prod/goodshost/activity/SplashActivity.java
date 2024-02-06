package com.sarin.prod.goodshost.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.activity.PermissionActivity;
import com.sarin.prod.goodshost.MainActivity;
import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.util.PreferenceManager;
import com.sarin.prod.goodshost.util.WritingTextView;


public class SplashActivity extends AppCompatActivity {

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
                if("1".equals(isPermission)){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);	//intent 에 명시된 액티비티로 이동
                }else{
                    Intent intent = new Intent(getApplicationContext(), PermissionActivity.class);
                    startActivity(intent);	//intent 에 명시된 액티비티로 이동
                }

                finish();	//현재 액티비티 종료

            }
        }, 1000 * sec); // sec초 정도 딜레이를 준 후 시작
    }
}