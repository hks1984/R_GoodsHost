package com.sarin.prod.goodshost.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.adapter.DefaultAlarmAdapter;
import com.sarin.prod.goodshost.adapter.ProductAlarmAdapter;
import com.sarin.prod.goodshost.adapter.RecentAdapter;
import com.sarin.prod.goodshost.adapter.RecyclerViewClickListener;
import com.sarin.prod.goodshost.databinding.ActivityAlarmBinding;
import com.sarin.prod.goodshost.db.DefaultAlarmDatabaseManager;
import com.sarin.prod.goodshost.db.ProductAlarmDatabaseManager;
import com.sarin.prod.goodshost.item.DefaultAlarmItem;
import com.sarin.prod.goodshost.item.ProductAlarmItem;
import com.sarin.prod.goodshost.util.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class AlarmActivity extends AppCompatActivity {

    private ActivityAlarmBinding binding;
    private static String TAG = MainApplication.TAG;
    private static RecyclerView recyclerView;
    public static ProductAlarmAdapter productAlarmAdapter;
    public static DefaultAlarmAdapter defaultAlarmAdapter;
    private List<ProductAlarmItem> aList = new ArrayList<>();
    private List<DefaultAlarmItem> dList = new ArrayList<>();
    private static ProductAlarmDatabaseManager productAlarmDatabaseManager;
    private static DefaultAlarmDatabaseManager defaultAlarmDatabaseManager;

    private ImageView exit;
    private TextView product_alarm, default_alarm, alarm_nodata;

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
        product_alarm = binding.productAlarm;
        default_alarm = binding.defaultAlarm;
        alarm_nodata = binding.alarmNodata;

        recyclerView = binding.recyclerView;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        productAlarmAdapter = new ProductAlarmAdapter(aList);
        defaultAlarmAdapter = new DefaultAlarmAdapter(dList);

        recyclerView.setAdapter(productAlarmAdapter);
        product_alarm.setBackgroundResource(R.drawable.round_button_on);
        product_alarm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white_500));

        productAlarmDatabaseManager = ProductAlarmDatabaseManager.getInstance(getApplicationContext());
        defaultAlarmDatabaseManager = DefaultAlarmDatabaseManager.getInstance(getApplicationContext());

        getProductAlarmList();
        if(productAlarmAdapter == null || productAlarmAdapter.size() < 1){
            alarm_nodata.setVisibility(View.VISIBLE);
        } else {
            alarm_nodata.setVisibility(View.GONE);
        }

        getDefaultAlarmList();


        exit = binding.exit;
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                overridePendingTransition(R.anim.none, R.anim.to_up_exit);
            }
        });

        product_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product_alarm.setBackgroundResource(R.drawable.round_button_on);
                product_alarm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white_500));

                default_alarm.setBackgroundResource(R.drawable.round_button_off);
                default_alarm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black_500));

                // 데이터가 없을 경우
                recyclerView.setAdapter(productAlarmAdapter);
                if(productAlarmAdapter == null || productAlarmAdapter.size() < 1){
                    alarm_nodata.setVisibility(View.VISIBLE);
                } else {
                    alarm_nodata.setVisibility(View.GONE);
                }


            }
        });
        default_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                default_alarm.setBackgroundResource(R.drawable.round_button_on);
                default_alarm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white_500));

                product_alarm.setBackgroundResource(R.drawable.round_button_off);
                product_alarm.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black_500));

                // 데이터가 없을 경우
                recyclerView.setAdapter(defaultAlarmAdapter);
                if(defaultAlarmAdapter == null || defaultAlarmAdapter.size() < 1){
                    alarm_nodata.setVisibility(View.VISIBLE);
                } else {
                    alarm_nodata.setVisibility(View.GONE);
                }

            }
        });

        productAlarmAdapter.setOnItemClickListener(new ProductAlarmAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                //save = bottomSheetDialog.findViewById(R.id.save);
                if (v.getId() == R.id.full_layout) {

                    ProductAlarmItem pItem = productAlarmAdapter.get(pos);

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(pItem.getLink()));
                    startActivity(intent);

                }
            }
        });



    }

    private void getProductAlarmList() {
        List<ProductAlarmItem> aList = new ArrayList<>();
        aList = productAlarmDatabaseManager.selectAll();
        productAlarmAdapter.setItems(aList);

    }

    private void getDefaultAlarmList() {
        List<DefaultAlarmItem> dList = new ArrayList<>();
        dList = defaultAlarmDatabaseManager.selectAll();
        defaultAlarmAdapter.setItems(dList);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isFinishing()) {
            // back 버튼으로 화면 종료가 야기되면 동작한다.
            overridePendingTransition(R.anim.none, R.anim.to_up_exit);
        }
    }

}