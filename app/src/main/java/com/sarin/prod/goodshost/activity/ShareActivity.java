package com.sarin.prod.goodshost.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.databinding.ActivityShareBinding;
import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.item.ReturnMsgItem;
import com.sarin.prod.goodshost.network.RetrofitClientInstance;
import com.sarin.prod.goodshost.network.RetrofitInterface;
import com.sarin.prod.goodshost.util.StringUtil;

import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.sarin.prod.goodshost.view.PopupDialogUtil;
import com.sarin.prod.goodshost.view.PopupDialogClickListener;


public class ShareActivity extends AppCompatActivity {

    private ActivityShareBinding binding;

    public static String TAG = MainApplication.TAG;

    private TextView add, textViewCountdown;

    static StringUtil sUtil = StringUtil.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShareBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        add = binding.add;
        textViewCountdown = binding.textViewCountdown;

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
//                handleSendText(intent);
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                add.setText(getApplicationContext().getResources().getString(R.string.share_adding));
                

                setUserSelectProduct(MainApplication.ANDROID_ID, sharedText);

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

    public void setUserSelectProduct(String user_id, String deep_link){

        retrofit2.Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);   // 레트로핏 인터페이스 객체 구현

        Call<ReturnMsgItem> call = service.setUserSelectProduct("setUserSelectProduct", user_id, deep_link);
        call.enqueue(new Callback<ReturnMsgItem>() {
            @Override
            public void onResponse(Call<ReturnMsgItem> call, Response<ReturnMsgItem> response) {
                if(response.isSuccessful()){
                    ReturnMsgItem returnMsgItem = response.body();
                    

                    if(returnMsgItem.getCode() > 0){
                        add.setText(getApplicationContext().getResources().getString(R.string.share_add_product));
                        new CountDownTimer(3000, 1000) { // 3000 milliseconds in total, 1000 milliseconds interval
                            public void onTick(long millisUntilFinished) {
                                textViewCountdown.setText("" + millisUntilFinished / 1000);
                            }
                            public void onFinish() {
//                                add.setVisibility(View.GONE); // Hide TextView
                                finish();
                            }

                        }.start();
                    }
                    else {
                        add.setText(getApplicationContext().getResources().getString(R.string.share_add_product_fail));
                        new CountDownTimer(3000, 1000) { // 3000 milliseconds in total, 1000 milliseconds interval
                            public void onTick(long millisUntilFinished) {
                                textViewCountdown.setText("" + millisUntilFinished / 1000);
                            }
                            public void onFinish() {
//                                add.setVisibility(View.GONE); // Hide TextView
                                finish();
                            }

                        }.start();
                    }

                }
                else{
                }
            }
            @Override
            public void onFailure(Call<ReturnMsgItem> call, Throwable t) {
                PopupDialogUtil.showCustomDialog(getApplicationContext(), new PopupDialogClickListener() {
                    @Override
                    public void onPositiveClick() {
                    }
                    @Override
                    public void onNegativeClick() {
                    }
                }, "ONE", getResources().getString(R.string.server_not_connecting));
            }
        });


    }



    // 데이터 처리 메서드 구현
    public void handleSendText(Intent intent) {

        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // 공유된 텍스트 사용
        }
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

