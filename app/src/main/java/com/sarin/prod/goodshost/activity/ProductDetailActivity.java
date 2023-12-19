package com.sarin.prod.goodshost.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.databinding.ActivityMainBinding;
import com.sarin.prod.goodshost.databinding.ActivityProductDetailBinding;


import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.databinding.FragmentHomeBinding;
import com.sarin.prod.goodshost.item.ProductItem;
import com.sarin.prod.goodshost.network.RetrofitClientInstance;
import com.sarin.prod.goodshost.network.RetrofitInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private ActivityProductDetailBinding binding;
    private static String TAG = MainApplication.TAG;

    private ImageView image, exit;
    private TextView name, price_value;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent tp_intent = getIntent();
        String vendor_item_id = tp_intent.getStringExtra("vendor_item_id");

        name = binding.name;
        price_value = binding.priceValue;
        image = binding.image;

        getProductDetail(vendor_item_id);


        exit = binding.exit;
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void getProductDetail(String vendor_item_id){
//        Log.d(TAG, "page: " + CategoryProducts_page);

        retrofit2.Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);   // 레트로핏 인터페이스 객체 구현

        Call<ProductItem> call = service.getProductDetail("getProductDetail", vendor_item_id);

        call.enqueue(new Callback<ProductItem>() {
            @Override
            public void onResponse(Call<ProductItem> call, Response<ProductItem> response) {
                if(response.isSuccessful()){
                    ProductItem productItem = response.body();
                    name.setText(productItem.getName());
                    price_value.setText(productItem.getPrice_value());
                    String url = "https:" + productItem.getImage();
                    Glide.with(getApplicationContext()).load(url).into(image);
                }
                else{
                    // 실패
                    Log.e(TAG, "실패 코드 확인 : " + response.code());
                    Log.e(TAG, "연결 주소 확인 : " + response.raw().request().url().url());
                }
            }

            @Override
            public void onFailure(Call<ProductItem> call, Throwable t) {
                // 통신 실패
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });

    }


}