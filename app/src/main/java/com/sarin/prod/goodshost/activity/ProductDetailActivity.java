package com.sarin.prod.goodshost.activity;

import static com.sarin.prod.goodshost.util.StringUtil.replaceIntToPrice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.databinding.ActivityProductDetailBinding;


import com.sarin.prod.goodshost.item.ChartItem;
import com.sarin.prod.goodshost.item.ProductItem;
import com.sarin.prod.goodshost.network.RetrofitClientInstance;
import com.sarin.prod.goodshost.network.RetrofitInterface;
import com.sarin.prod.goodshost.util.LoadingProgressManager;
import com.sarin.prod.goodshost.util.MyMarkerView;
import com.sarin.prod.goodshost.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private TextView name, price_value;

    private LineChart lineChart;


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
        lineChart = binding.detailLinechart;

        getProductDetail(vendor_item_id);
        getProductChart(vendor_item_id);

        exit = binding.exit;
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }

//    private void LineChartGraph(List<ChartItem> chartItems) {
//        List<Entry> entries = new ArrayList<>();
//        final Map<String, Integer> xLabelMap = new HashMap<>();
//
//        Log.d(TAG,chartItems.toString()  );
//        int index = 0;
//        for (ChartItem ci : chartItems) {
//            String date = ci.getC_date();
//            if (!xLabelMap.containsKey(date)) {
//                xLabelMap.put(date, index++);
//            }
//            entries.add(new Entry(xLabelMap.get(date), ci.getMin_value()));
//        }
//
//        LineDataSet dataSet = new LineDataSet(entries, "Label");
//        dataSet.setColor(Color.RED);
//        dataSet.setValueTextColor(Color.BLUE);
//
//        LineData lineData = new LineData(dataSet);
//        lineChart.setData(lineData);
//
//        XAxis xAxis = lineChart.getXAxis();
//        xAxis.setDrawAxisLine(false);
//        xAxis.setGranularity(1f);
//        xAxis.setTextSize(10f);  //x축 텍스트 크기
//        xAxis.setGridColor(ContextCompat.getColor(getApplicationContext(), R.color.white));    //그래프 그리드 컬러
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // X 축 데이터 표시 위치
//        xAxis.setGranularityEnabled(true);  //x축 5개 이하일때 값 중복 제거
//
//
//        lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
//            @Override
//            public String getAxisLabel(float value, AxisBase axis) {
//                int intValue = (int) value;
//                for (Map.Entry<String, Integer> entry : xLabelMap.entrySet()) {
//                    if (entry.getValue().equals(intValue)) {
//                        return entry.getKey();
//                    }
//                }
//                return "";
//            }
//        });
//
//        YAxis axisLeft = lineChart.getAxisLeft();
////        axisLeft.setAxisMinimum(0f); // 최솟값
////        axisLeft.setAxisMaximum(50f); // 최댓값
//        axisLeft.setGranularity(1f); // 값만큼 라인선 설정
//        axisLeft.setDrawGridLines(false);
//        axisLeft.setDrawAxisLine(false);
//        axisLeft.setDrawLabels(false); // label 삭제
//
//        // YAxis(Right) (수평 막대 기준 위쪽) - 사이즈, 선 유무
//        YAxis axisRight = lineChart.getAxisRight();
//        axisRight.setTextSize(15f);
//        axisRight.setDrawLabels(false); // label 삭제
//        axisRight.setDrawGridLines(false);
//        axisRight.setDrawAxisLine(false);
//
//        Description description = new Description();
//        description.setText("My Line Chart");
//        lineChart.setDescription(description);
//
//        lineChart.invalidate(); // 차트 갱신
//    }

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
            }
            entries.add(new Entry(xLabelMap.get(date), value));
        }

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
        lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                int intValue = (int) value;
                for (Map.Entry<String, Integer> entry : xLabelMap.entrySet()) {
                    if (entry.getValue().equals(intValue)) {
                        return entry.getKey();
                    }
                }
                return "";
            }
        });

        // Y축 범위에 여유 공간 추가
        float range = maxValue - minValue;
        lineChart.getAxisLeft().setAxisMinimum(minValue - range * 2.0f);
        lineChart.getAxisLeft().setAxisMaximum(maxValue + range * 2.0f);

        // 최소값에 대한 LimitLine 추가
        LimitLine llMin = new LimitLine(minValue, "최소값: " + replaceIntToPrice((int)minValue) + "원");
        llMin.setLineColor(Color.BLUE);
        llMin.setLineWidth(1f);
        llMin.enableDashedLine(10f, 10f, 0f);
        llMin.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llMin.setTextSize(10f);
        llMin.setTextColor(Color.BLUE);

        // 최대값에 대한 LimitLine 추가
        LimitLine llMax = new LimitLine(maxValue, "최대값: " + replaceIntToPrice((int)maxValue) + "원");
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