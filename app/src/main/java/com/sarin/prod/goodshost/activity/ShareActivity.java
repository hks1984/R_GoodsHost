package com.sarin.prod.goodshost.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.util.StringUtil;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ShareActivity extends AppCompatActivity {

    public static String TAG = MainApplication.TAG;

    static StringUtil sUtil = StringUtil.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                final String[] url = {handleSendText(intent)}; // 텍스트 데이터 처리
                Log.d(TAG, "ss URL: " + url[0]);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 네트워크 작업 수행
                        url[0] = expandShortURL(url[0]);
                        Log.d(TAG, "찐 url : " + url[0]);
                        // UI 스레드에서 결과 처리
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // UI 업데이트
                            }
                        });
                    }
                }).start();

            }
//            else if (type.startsWith("image/")) {
//                handleSendImage(intent); // 이미지 데이터 처리
//            }
            // 여기에 다른 데이터 유형에 대한 처리 코드 추가
        }
        else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent); // 여러 이미지 처리
            }
            // 여기에 다른 데이터 유형에 대한 처리 코드 추가
        }
    }

    public static String expandShortURL(String shortUrl) {
        String longUrl = "";
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(shortUrl).openConnection();
            connection.setInstanceFollowRedirects(false); // 리디렉션을 자동으로 따르지 않도록 설정
            connection.connect();
            int responseCode = connection.getResponseCode();

            // 301 또는 302 응답 코드는 리디렉션을 나타냅니다.
            Log.d(TAG, "responseCode: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_MOVED_PERM) {
                longUrl = connection.getHeaderField("Location");
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return longUrl;
    }

    // 데이터 처리 메서드 구현
    public String handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        String rtn = "";
        Log.d(TAG, "sharedText : " + sharedText);
        if (sharedText != null) {
            // 공유된 텍스트 사용
            List<String> urls = sUtil.extractUrls(sharedText);
            for (String url : urls) {
                Log.d(TAG, "URL: " + url);
                rtn = url;
                break;
            }
        }
        return rtn;
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // 공유된 이미지 사용
        }
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // 공유된 여러 이미지 사용
        }
    }

}

