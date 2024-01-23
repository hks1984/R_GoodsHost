package com.sarin.prod.goodshost.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.databinding.ActivityProductDetailBinding;


import com.sarin.prod.goodshost.fragment.home.HomeFragment;
import com.sarin.prod.goodshost.item.ChartItem;
import com.sarin.prod.goodshost.item.ProductItem;
import com.sarin.prod.goodshost.item.ReturnMsgItem;
import com.sarin.prod.goodshost.item.SharedViewModel;
import com.sarin.prod.goodshost.network.RetrofitApi;
import com.sarin.prod.goodshost.network.RetrofitClientInstance;
import com.sarin.prod.goodshost.network.RetrofitInterface;
import com.sarin.prod.goodshost.util.LoadingProgressManager;
import com.sarin.prod.goodshost.util.MyMarkerView;
import com.sarin.prod.goodshost.util.StringUtil;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private ActivityProductDetailBinding binding;
    private static String TAG = MainApplication.TAG;

    private LoadingProgressManager loadingProgressManager = LoadingProgressManager.getInstance();
    static StringUtil sUtil = StringUtil.getInstance();

    private ImageView image, exit;
    private TextView name, price_value, registration, link;

    private TextView save;
    private EditText hope_price;
    private CheckBox check;
    private String vendor_item_id;

    private LineChart lineChart;

    ProductItem _pi = new ProductItem();

    private SharedViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent tp_intent = getIntent();
        vendor_item_id = tp_intent.getStringExtra("vendor_item_id");

        name = binding.name;
        price_value = binding.priceValue;
        image = binding.image;
        lineChart = binding.detailLinechart;
        registration = binding.registration;
        link = binding.link;

        getProductDetail(vendor_item_id);
        getProductChart(vendor_item_id);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.bottom_sheet_registration, null, false);
        // 상단 라운딩 적용. (themes.xml에 style 추가 필요)
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.RoundCornerBottomSheetDialogTheme);
        bottomSheetDialog.setContentView(view);


        viewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewModel.setFavorite(true);
                bottomSheetDialog.show();

            }
        });

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(_pi.getCoupang_link()));
                startActivity(intent);

            }
        });


        save = bottomSheetDialog.findViewById(R.id.save);
        hope_price = bottomSheetDialog.findViewById(R.id.hope_price);
        hope_price.setSelection(hope_price.getText().length());
        check = bottomSheetDialog.findViewById(R.id.check);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheckBoxChecked = check.isChecked();
                String hope_stock = isCheckBoxChecked ? "Y" : "N";
                String editTextValue = hope_price.getText().toString();
                setUserItemMap(MainApplication.ANDROID_ID, vendor_item_id, sUtil.convertStringToInt(editTextValue), hope_stock);

                HomeFragment hf = HomeFragment.getInstance();
                hf.productAdapter.setFavorite(vendor_item_id, true);
                hf.productHoriAdapter.setFavorite(vendor_item_id, true);

                CategoryProductListActivity cpla = CategoryProductListActivity.getInstance();
                cpla.productAdapter.setFavorite(vendor_item_id, true);

                bottomSheetDialog.dismiss();

            }
        });

        hope_price.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 변경 전에 필요한 작업이 있다면 여기서 수행
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 변경되는 텍스트에 대한 처리
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    hope_price.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[,]", "");
                    if (!cleanString.isEmpty()) {
                        try {
                            String formatted = NumberFormat.getNumberInstance(Locale.KOREA).format(Double.parseDouble(cleanString));
                            current = formatted;
                            hope_price.setText(formatted);
                            hope_price.setSelection(formatted.length());
                        } catch (NumberFormatException e) {
                            // 예외 처리
                        }
                    }

                    hope_price.addTextChangedListener(this);
                }
            }
        });



        exit = binding.exit;
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }

    public void setUserItemMap(String user_id, String vendor_item_id, int hope_price, String hope_stock){

        retrofit2.Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);   // 레트로핏 인터페이스 객체 구현

        Call<ReturnMsgItem> call = service.setUserItemMap("setUserItemMap", user_id, vendor_item_id, hope_price, hope_stock);
        call.enqueue(new Callback<ReturnMsgItem>() {
            @Override
            public void onResponse(Call<ReturnMsgItem> call, Response<ReturnMsgItem> response) {
                if(response.isSuccessful()){
                    ReturnMsgItem returnMsgItem = response.body();
                    Log.d(TAG, "setUserRegister : " + returnMsgItem.toString());

                }
                else{
                    Log.e(TAG, "실패 코드 확인 : " + response.code());
                    Log.e(TAG, "연결 주소 확인 : " + response.raw().request().url().url());
                }
            }
            @Override
            public void onFailure(Call<ReturnMsgItem> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });


    }


    private void LineChartGraph(List<ChartItem> chartItems) {
        List<Entry> entries = new ArrayList<>();
        final Map<String, Integer> xLabelMap = new HashMap<>();

        float minValue = Float.MAX_VALUE;
        float maxValue = Float.MIN_VALUE;

        int index = 0;
        for (ChartItem ci : chartItems) {
            float value = ci.getMin_value();
            minValue = Math.min(minValue, value);
            maxValue = Math.max(maxValue, value);

            String date = ci.getC_date();
            if (!xLabelMap.containsKey(date)) {
                xLabelMap.put(date, index++);

                Log.d(TAG, "date : " + date);
            }
            entries.add(new Entry(xLabelMap.get(date), value));


        }
        Log.d(TAG, "chartItems: " + chartItems.size());
        Log.d(TAG, "chartItems: " + chartItems.toString());
        Log.d(TAG, "" + minValue + "   " + maxValue);
        LineDataSet dataSet = new LineDataSet(entries, null);
        dataSet.setColor(Color.RED);
//        dataSet.setValueTextColor(Color.BLUE);

        // 선 색에 대한 설명 부분(색 이미지 설명)을 숨김
        Legend legend = lineChart.getLegend();
        legend.setEnabled(false);


        // 선 굵기 조절
        dataSet.setLineWidth(3f); // 굵기를 조절하고 싶은 값으로 설정

        // 선을 곡선으로 설정
//        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        // dot 표시 없앰.
        dataSet.setDrawCircles(false);

        // 최소값과 최대값에만 값을 표시
//        float finalMinValue = minValue;
//        float finalMaxValue = maxValue;
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPointLabel(Entry entry) {
//                if (entry.getY() == finalMinValue || entry.getY() == finalMaxValue) {
//                    return String.valueOf(entry.getY());
//                }
                return "";
            }
        });

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // X축 Formatter 설정
//        lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
//            @Override
//            public String getAxisLabel(float value, AxisBase axis) {
//                Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!");
//                int intValue = (int) value;
//                for (Map.Entry<String, Integer> entry : xLabelMap.entrySet()) {
//                    if (entry.getValue().equals(intValue)) {
//                        Log.d(TAG, "entry.getKey(): " + entry.getKey() + "   value: " + value);
//                        return entry.getKey();
//                    }
//                }
//                return "";
//            }
//        });

        lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                int intValue = (int) value;

                if(xLabelMap.containsValue(intValue)) {
                    for (Map.Entry<String, Integer> entry : xLabelMap.entrySet()) {
                        if (entry.getValue().equals(intValue)) {
                            return entry.getKey();
                        }
                    }
                }
                return "";
            }
        });


                        // Y축 범위에 여유 공간 추가
        float range = maxValue - minValue;
        if(range == 0){
            range = 10;
        }
        lineChart.getAxisLeft().setAxisMinimum(minValue - range * 1.0f);
        lineChart.getAxisLeft().setAxisMaximum(maxValue + range * 1.0f);

        // 최소값에 대한 LimitLine 추가
        LimitLine llMin = new LimitLine(minValue, "최소값: " + sUtil.replaceStringPriceToInt((int)minValue) + "원");
        llMin.setLineColor(Color.BLUE);
        llMin.setLineWidth(1f);
        llMin.enableDashedLine(10f, 10f, 0f);
        llMin.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llMin.setTextSize(10f);
        llMin.setTextColor(Color.BLUE);

        // 최대값에 대한 LimitLine 추가
        LimitLine llMax = new LimitLine(maxValue, "최대값: " + sUtil.replaceStringPriceToInt((int)maxValue) + "원");
        llMax.setLineColor(Color.RED);
        llMax.setLineWidth(1f);
        llMax.enableDashedLine(10f, 10f, 0f);
        llMax.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        llMax.setTextSize(10f);
        llMax.setTextColor(Color.RED);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(llMin);
        leftAxis.addLimitLine(llMax);
        leftAxis.setDrawLimitLinesBehindData(true);

        // 좌측 범위 내용 숨기기
        leftAxis.setDrawLabels(false);

        // 왼쪽 Y축 자체의 선을 숨깁니다.
        leftAxis.setDrawAxisLine(false);

        // 이 줄은 차트의 왼쪽 Y축에 해당하는 그리드 선을 숨깁니다. 그리드 선은 차트의 배경에 그려지는 수평 또는 수직선들로, 데이터의 위치를 파악하는 데 도움을 줍니다.
        // 이 설정을 사용하면 그리드 선이 표시되지 않아 차트의 배경이 더 깔끔해집니다.
        leftAxis.setDrawGridLines(false);


//        lineChart.getXAxis().setDrawLabels(false);
//        lineChart.getXAxis().setDrawAxisLine(false);
        lineChart.getXAxis().setDrawGridLines(false);

        lineChart.getXAxis().setGranularity(1f);







        lineChart.getAxisRight().setEnabled(false);
        Description description = new Description();
        description.setText("");
        lineChart.setDescription(description);

        // 툴팁 적용
        Map<Integer, String> xValuesMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : xLabelMap.entrySet()) {
            xValuesMap.put(entry.getValue(), entry.getKey());
        }
        MyMarkerView mv = new MyMarkerView(this, R.layout.my_marker_view, xValuesMap);
        mv.setChartView(lineChart); // For bounds control
        lineChart.setMarker(mv);

        // x축 아래에 기준값 표시
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);


        lineChart.invalidate(); // 차트 갱신
    }


    public void getProductChart(String vendor_item_id){

        loadingProgressManager.showLoading(MainApplication.activity);
        retrofit2.Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);   // 레트로핏 인터페이스 객체 구현

        Call<List<ChartItem>> call = service.getProductChart("getProductChart", vendor_item_id);

        call.enqueue(new Callback<List<ChartItem>>() {
            @Override
            public void onResponse(Call<List<ChartItem>> call, Response<List<ChartItem>> response) {
                if(response.isSuccessful()){
                    List<ChartItem> chartItem = response.body();

                    Log.d(TAG, vendor_item_id + " "+ chartItem.toString());
                    if(chartItem.size() > 0){
                        LineChartGraph(chartItem);
                        lineChart.setScaleEnabled(false);

//                        lineChart.setTouchEnabled(false);
//                        lineChart.getAxisRight().setAxisMaxValue(80);
//                        lineChart.getAxisLeft().setAxisMaxValue(80);
                    }
                }
                else{
                    // 실패
                    Log.e(TAG, "실패 코드 확인 : " + response.code());
                    Log.e(TAG, "연결 주소 확인 : " + response.raw().request().url().url());
                }

                loadingProgressManager.hideLoading();
            }

            @Override
            public void onFailure(Call<List<ChartItem>> call, Throwable t) {
                // 통신 실패
                Log.e(TAG, "onFailure: " + t.getMessage());

                loadingProgressManager.hideLoading();
            }
        });


    }


    public void getProductDetail(String vendor_item_id){
//        Log.d(TAG, "page: " + CategoryProducts_page);

        loadingProgressManager.showLoading(this);
        retrofit2.Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);   // 레트로핏 인터페이스 객체 구현

        Call<ProductItem> call = service.getProductDetail("getProductDetail", vendor_item_id);

        call.enqueue(new Callback<ProductItem>() {
            @Override
            public void onResponse(Call<ProductItem> call, Response<ProductItem> response) {
                if(response.isSuccessful()){
                    ProductItem productItem = response.body();
                    _pi = productItem;
                    name.setText(productItem.getName());
                    price_value.setText(sUtil.replaceStringPriceToInt(productItem.getPrice_value()) + getApplicationContext().getResources().getString(R.string.won));
                    String url = productItem.getImage();
                    if(!url.contains("https:")){
                        url = "https:" + url;
                    }
                    Glide.with(getApplicationContext()).load(url).into(image);
                }
                else{
                    // 실패
                    Log.e(TAG, "실패 코드 확인 : " + response.code());
                    Log.e(TAG, "연결 주소 확인 : " + response.raw().request().url().url());
                }
                loadingProgressManager.hideLoading();

            }

            @Override
            public void onFailure(Call<ProductItem> call, Throwable t) {
                // 통신 실패
                Log.e(TAG, "onFailure: " + t.getMessage());
                loadingProgressManager.hideLoading();

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingProgressManager != null) {
            loadingProgressManager.hideLoading();
        }

    }
}