package com.sarin.prod.goodshost.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import com.sarin.prod.goodshost.databinding.ActivityMainBinding;
import com.sarin.prod.goodshost.databinding.ActivityProductDetailBinding;


import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.databinding.FragmentHomeBinding;

public class ProductDetailActivity extends AppCompatActivity {

    private ActivityProductDetailBinding binding;
    //private FragmentHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        View root = binding.getRoot();

        Intent tp_intent = getIntent();
        String serviceId = tp_intent.getStringExtra("vendor_item_id");

        TextView text_favorite = binding.favorite;

        text_favorite.setText(serviceId);


    }
}