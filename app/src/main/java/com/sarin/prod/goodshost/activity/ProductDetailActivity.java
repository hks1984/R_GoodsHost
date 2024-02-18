package com.sarin.prod.goodshost.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.sarin.prod.goodshost.view.PopupDialogUtil;
import com.sarin.prod.goodshost.view.PopupDialogClickListener;


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
import com.google.android.material.snackbar.Snackbar;
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
import com.sarin.prod.goodshost.util.CustomSnackbar;


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
    private TextView name, price_value, registration, link, save, rocket, difference_price, average_price, views, discount_1, discount_2, discount_3;
    private EditText hope_price;
    private CheckBox check;
    private LinearLayout price_layout;
    private String vendor_item_id;

    private LineChart lineChart;

    ProductItem _pi = new ProductItem();

    private SharedViewModel viewModel;

    private int current_price = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent tp_intent = getIntent();
        vendor_item_id = tp_intent.getStringExtra("vendor_item_id");

        String url = tp_intent.getStringExtra("url");
        if(url != null && !url.isEmpty()){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }

        name = binding.name;
        price_value = binding.priceValue;
        image = binding.image;
        lineChart = binding.detailLinechart;
        registration = binding.registration;
        link = binding.link;
        rocket = binding.rocket;
        difference_price = binding.differencePrice;
        average_price = binding.averagePrice;
        price_layout = binding.priceLayout;
        views = binding.views;

        getProductDetail(vendor_item_id);
        getProductChart(vendor_item_id);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.bottom_sheet_registration, null, false);
        // 상단 라운딩 적용. (themes.xml에 style 추가 필요)
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.RoundCornerBottomSheetDialogTheme);
        bottomSheetDialog.setContentView(view);


        viewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        save = bottomSheetDialog.findViewById(R.id.save);
        hope_price = bottomSheetDialog.findViewById(R.id.hope_price);
        hope_price.setSelection(hope_price.getText().length());
        check = bottomSheetDialog.findViewById(R.id.check);
        discount_1 = bottomSheetDialog.findViewById(R.id.discount_1);
        discount_2 = bottomSheetDialog.findViewById(R.id.discount_2);
        discount_3 = bottomSheetDialog.findViewById(R.id.discount_3);


        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewModel.setFavorite(true);
                bottomSheetDialog.show();

                hope_price.setText("" + _pi.getHope_price());

                if("Y".equals(_pi.getHope_stock())){
                    check.setChecked(true);
                } else {
                    check.setChecked(false);
                }


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


        discount_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(v, "alpha", 1f, 0f, 1f);
                animator.setDuration(300); // 500ms 동안 실행
                animator.start();

                int current_price = _pi.getPrice_value();
                hope_price.setText("" + sUtil.calculateDiscountedPrice(current_price, 10));
            }
        });
        discount_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(v, "alpha", 1f, 0f, 1f);
                animator.setDuration(300); // 500ms 동안 실행
                animator.start();

                int current_price = _pi.getPrice_value();
                hope_price.setText("" + sUtil.calculateDiscountedPrice(current_price, 15));
            }
        });
        discount_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(v, "alpha", 1f, 0f, 1f);
                animator.setDuration(300); // 500ms 동안 실행
                animator.start();

                int current_price = _pi.getPrice_value();
                hope_price.setText("" + sUtil.calculateDiscountedPrice(current_price, 20));
            }
        });



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheckBoxChecked = check.isChecked();
                String hope_stock = isCheckBoxChecked ? "Y" : "N";
                String editTextValue = hope_price.getText().toString();
                setUserItemMap(MainApplication.ANDROID_ID, vendor_item_id, "Y", sUtil.convertStringToInt(editTextValue), hope_stock);

                HomeFragment hf = HomeFragment.getInstance();
                hf.productAdapter.setFavorite(vendor_item_id, true);
                hf.productHoriAdapter.setFavorite(vendor_item_id, true);

                CategoryProductListActivity cpla = CategoryProductListActivity.getInstance();
                if(cpla.productAdapter != null){
                    cpla.productAdapter.setFavorite(vendor_item_id, true);
                }

                CustomSnackbar.showSnackbar(getApplicationContext(), binding.getRoot(), getApplicationContext().getResources().getString(R.string.favorite_save));
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

    public void setUserItemMap(String user_id, String vendor_item_id, String hope_low_price, int hope_price, String hope_stock){

        retrofit2.Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);   // 레트로핏 인터페이스 객체 구현

        Call<ReturnMsgItem> call = service.setUserItemMap("setUserItemMap", user_id, vendor_item_id, hope_low_price, hope_price, hope_stock);
        call.enqueue(new Callback<ReturnMsgItem>() {
            @Override
            public void onResponse(Call<ReturnMsgItem> call, Response<ReturnMsgItem> response) {
                if(response.isSuccessful()){
                    ReturnMsgItem returnMsgItem = response.body();
                    Log.d(TAG, "setUserRegister : " + returnMsgItem.toString());

                    // 처음 관심상품 등록 후 그 상태에서 바로 관심상품 눌렀을때 변경한 값으로 출력하도록. (서버 통신을 안하니 값을 새로안들고와서 객체에 추가)
                    if(returnMsgItem.getCode() > 0){
                        _pi.setHope_price(hope_price);
                        _pi.setHope_stock(hope_stock);
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
        dataSet.setColor(ContextCompat.getColor(getApplicationContext(), R.color.personal));
//        dataSet.setValueTextColor(Color.BLUE);

        // 선 색에 대한 설명 부분(색 이미지 설명)을 숨김
        Legend legend = lineChart.getLegend();
        legend.setEnabled(false);


        // 선 굵기 조절
        dataSet.setLineWidth(4f); // 굵기를 조절하고 싶은 값으로 설정

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
        LimitLine llMin = new LimitLine(minValue, "최저가: " + sUtil.replaceStringPriceToInt((int)minValue) + "원");
        llMin.setLineColor(ContextCompat.getColor(getApplicationContext(), R.color.blue_200));
        llMin.setLineWidth(1f);
        llMin.enableDashedLine(10f, 10f, 0f);
        llMin.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llMin.setTextSize(12f);
        llMin.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue_200));

        // 최대값에 대한 LimitLine 추가
        LimitLine llMax = new LimitLine(maxValue, "최고가: " + sUtil.replaceStringPriceToInt((int)maxValue) + "원");
        llMax.setLineColor(ContextCompat.getColor(getApplicationContext(), R.color.red_200));
        llMax.setLineWidth(1f);
        llMax.enableDashedLine(10f, 10f, 0f);
        llMax.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        llMax.setTextSize(12f);
        llMax.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red_200));

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

        loadingProgressManager.showLoading(this);
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

                        int total_price = 0;
                        for (ChartItem ci : chartItem) {
                            total_price += ci.getMin_value();  // 각 ChartItem의 min_value를 total_price에 더함
                        }
                        // 평균가 계산 (소수점 제거를 위해 int형으로 결과를 얻음)
                        int averagePrice = chartItem.size() > 0 ? total_price / chartItem.size() : 0;  // chartItem.size()가 0이 아닐 때만 평균 계산

                        // TextView에 평균가 설정 (정수로 변환된 평균가를 문자열로 변환하여 설정)
                        average_price.setText(sUtil.replaceStringPriceToInt(averagePrice) + getApplicationContext().getResources().getString(R.string.won));

//                        int diff_price = averagePrice > current_price ?
//                                averagePrice - current_price :
//                                current_price - averagePrice ;

                        int diff_price = current_price - averagePrice;

                        String resultText = diff_price > 0 ? "+": "";

                        difference_price.setText(resultText + sUtil.replaceStringPriceToInt(diff_price) + getApplicationContext().getResources().getString(R.string.won));


                    } else {
                        binding.priceLayout.setVisibility(View.GONE);
                    }
                }
                else{
                }

                loadingProgressManager.hideLoading();
            }

            @Override
            public void onFailure(Call<List<ChartItem>> call, Throwable t) {
                loadingProgressManager.hideLoading();
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


    public void getProductDetail(String vendor_item_id){
//        Log.d(TAG, "page: " + CategoryProducts_page);

        loadingProgressManager.showLoading(this);
        retrofit2.Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);   // 레트로핏 인터페이스 객체 구현

        Call<ProductItem> call = service.getProductDetail("getProductDetail", MainApplication.ANDROID_ID, vendor_item_id);

        call.enqueue(new Callback<ProductItem>() {
            @Override
            public void onResponse(Call<ProductItem> call, Response<ProductItem> response) {
                if(response.isSuccessful()){
                    ProductItem productItem = response.body();
                    _pi = productItem;
                    name.setText(productItem.getName());
                    price_value.setText(sUtil.replaceStringPriceToInt(productItem.getPrice_value()) + getApplicationContext().getResources().getString(R.string.won));
                    views.setText(sUtil.replaceStringPriceToInt(productItem.getViews()));
                    current_price = productItem.getPrice_value();
                    String url = productItem.getImage();
                    if(!url.contains("https:")){
                        url = "https:" + url;
                    }
                    Glide.with(getApplicationContext())
                            .load(url)
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(20))) // 여기서 10은 코너의 반지름을 dp 단위로 지정
                            .into(image);

                    if(productItem.getRocket_baesong() != null && !"".equals(productItem.getRocket_baesong())){
                        rocket.setVisibility(View.VISIBLE);
                        rocket.setText(productItem.getRocket_baesong());
                    } else {
                        rocket.setVisibility(View.GONE);
                    }

                }
                else{
                }
                loadingProgressManager.hideLoading();

            }

            @Override
            public void onFailure(Call<ProductItem> call, Throwable t) {
                loadingProgressManager.hideLoading();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingProgressManager != null) {
            loadingProgressManager.hideLoading();
        }

    }
}