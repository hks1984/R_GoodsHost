package com.sarin.prod.goodshost.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.adapter.ProductAdapter;
import com.sarin.prod.goodshost.adapter.ProductAlarmAdapter;
import com.sarin.prod.goodshost.adapter.RecyclerViewClickListener;
import com.sarin.prod.goodshost.databinding.ActivityAlarmBinding;
import com.sarin.prod.goodshost.databinding.ActivityCategoryProductListBinding;
import com.sarin.prod.goodshost.db.AlarmDatabaseManager;
import com.sarin.prod.goodshost.item.ProductAlarmItem;
import com.sarin.prod.goodshost.item.ProductItem;

import java.util.ArrayList;
import java.util.List;

public class AlarmActivity extends AppCompatActivity implements RecyclerViewClickListener {

    private ActivityAlarmBinding binding;
    private static String TAG = MainApplication.TAG;
    private static RecyclerView recyclerView;
    public static ProductAlarmAdapter productAlarmAdapter;
    private List<ProductAlarmItem> aList = new ArrayList<>();
    private static AlarmDatabaseManager alarmDatabaseManager;

    private ImageView exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent tp_intent = getIntent();
        String url = tp_intent.getStringExtra("url");
        if(url != null && !url.isEmpty()){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }

        exit = binding.exit;

        recyclerView = binding.recyclerView;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        productAlarmAdapter = new ProductAlarmAdapter(aList, this);
        recyclerView.setAdapter(productAlarmAdapter);

        alarmDatabaseManager = AlarmDatabaseManager.getInstance(getApplicationContext());

        exit = binding.exit;
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getAlarmList();

    }

    private void getAlarmList() {
        List<ProductAlarmItem> aList = new ArrayList<>();
        aList = alarmDatabaseManager.selectAll();
        productAlarmAdapter.setItems(aList);

    }

    @Override
    public void onItemClickListener(View v, int pos) {
        // 아이템 클릭 이벤트 처리
        ProductAlarmItem pItem = productAlarmAdapter.get(pos);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(pItem.getProduct_image()));
        startActivity(intent);

    }

    @Override
    public void onItemClickListener_Hori(View v, int pos) {}

}