package com.sarin.prod.goodshost.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.sarin.prod.goodshost.adapter.ProductAdapterHori;
import com.sarin.prod.goodshost.adapter.RecyclerViewClickListener;
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

public class ProductDetailActivity extends AppCompatActivity implements RecyclerViewClickListener {

    private ActivityProductDetailBinding binding;
    private static String TAG = MainApplication.TAG;

    private LoadingProgressManager loadingProgressManager = LoadingProgressManager.getInstance();
    static StringUtil sUtil = StringUtil.getInstance();

    private ImageView image, exit, favorite, share;
    private TextView name, price_value, registration, link, save, rocket, difference_price, average_price, views, discount_1, discount_2, discount_3, rating_total_count;
    private RatingBar rating;
    private EditText hope_price;
    private CheckBox check;
    private LinearLayout price_layout, no_linechart;
    private String vendor_item_id;

    private LineChart lineChart;

    ProductItem _pi = new ProductItem();

    private SharedViewModel viewModel;

    private int current_price = 0;

    private static RecyclerView relatedRecyclerView;
    public static ProductAdapterHori productHoriAdapter;
    private List<ProductItem> piLIst = new ArrayList<>();
    private ProductItem pdItem = new ProductItem();
    private int pdItem_possion = 0;

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
//        registration = binding.registration;
        link = binding.link;
        rocket = binding.rocket;
        difference_price = binding.differencePrice;
        average_price = binding.averagePrice;
        price_layout = binding.priceLayout;
//        views = binding.views;
        no_linechart = binding.noLinechart;
        share = binding.share;
        favorite = binding.favorite;
        rating_total_count = binding.ratingTotalCount;
        rating = binding.rating;


        relatedRecyclerView = binding.recyclerView;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        relatedRecyclerView.setLayoutManager(layoutManager);
//        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1)); // 가로 2개 나열 할때.
        productHoriAdapter = new ProductAdapterHori(piLIst, this);
        relatedRecyclerView.setAdapter(productHoriAdapter);

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
        check = bottomSheetDialog.findViewById(R.id.check);
        discount_1 = bottomSheetDialog.findViewById(R.id.discount_1);
        discount_2 = bottomSheetDialog.findViewById(R.id.discount_2);
        discount_3 = bottomSheetDialog.findViewById(R.id.discount_3);


        favorite.setOnClickListener(new View.OnClickListener() {
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

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Sharing_intent = new Intent(Intent.ACTION_SEND);
                Sharing_intent.setType("text/plain");

                Sharing_intent.putExtra(Intent.EXTRA_TEXT, _pi.getCoupang_link());

                Intent Sharing = Intent.createChooser(Sharing_intent, "공유하기");
                startActivity(Sharing);
            }
        });
        출처: https://goatlab.tistory.com/entry/Android-Studio-공유하기-기능-Android-Sharesheet [GOATLAB:티스토리]


        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                String url = sUtil.nullCheck(_pi.getCoupang_link()) ? _pi.getLink():_pi.getCoupang_link();
                intent.setData(Uri.parse(url));
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
                favorite.setImageResource(R.drawable.baseline_favorite_24);
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

        hope_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EditText가 클릭되었을 때 실행될 코드
                Log.d(TAG, "hope_price.getText().length() 1: " + hope_price.getText().length());
                if (hope_price.getText().length() > 0) {
                    hope_price.setSelection(hope_price.getText().length()); // 커서를 텍스트의 가장 우측으로 이동
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

    public void onItemClickListener(View v, int pos) {

    }

    public void onItemClickListener_Hori(View v, int pos) {
        // 아이템 클릭 이벤트 처리
        pdItem = productHoriAdapter.get(pos);
        pdItem_possion = pos;
        if (v.getId() == R.id.layout_favorite) {
            if(pdItem.isIs_Favorite()){
                setDelUserItemMap(MainApplication.ANDROID_ID, pdItem.getVendor_item_id());
                pdItem.setIs_Favorite(false);
                productHoriAdapter.set(pos, pdItem);
            } else {
                setUserItemMap(MainApplication.ANDROID_ID, pdItem.getVendor_item_id(), "Y", 0, "N");
                pdItem.setIs_Favorite(true);
                productHoriAdapter.set(pos, pdItem);
            }

        } else if (v.getId() == R.id.list_view_hori) {
            Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
            intent.putExtra("vendor_item_id", pdItem.getVendor_item_id());
            v.getContext().startActivity(intent);	//intent 에 명시된 액티비티로 이동
        }
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
                PopupDialogUtil.showCustomDialog(ProductDetailActivity.this, new PopupDialogClickListener() {
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

    public void setDelUserItemMap(String user_id, String vendor_item_id){

        retrofit2.Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);   // 레트로핏 인터페이스 객체 구현

        Call<ReturnMsgItem> call = service.setDelUserItemMap("setDelUserItemMap", user_id, vendor_item_id);
        call.enqueue(new Callback<ReturnMsgItem>() {
            @Override
            public void onResponse(Call<ReturnMsgItem> call, Response<ReturnMsgItem> response) {
                if(response.isSuccessful()){
                    ReturnMsgItem returnMsgItem = response.body();
//                    productAdapter.remove(pdItem_possion);
                }
                else{
                }
            }
            @Override
            public void onFailure(Call<ReturnMsgItem> call, Throwable t) {
                PopupDialogUtil.showCustomDialog(ProductDetailActivity.this, new PopupDialogClickListener() {
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

                
            }
            entries.add(new Entry(xLabelMap.get(date), value));


        }
        
        
        
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

//                int intValue = (int) value;
//                for (Map.Entry<String, Integer> entry : xLabelMap.entrySet()) {
//                    if (entry.getValue().equals(intValue)) {

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

                    
                    if(chartItem != null && chartItem.size() > 0){
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
                        no_linechart.setVisibility(View.VISIBLE);
                    }
                }
                else{
                }

                loadingProgressManager.hideLoading();
            }

            @Override
            public void onFailure(Call<List<ChartItem>> call, Throwable t) {
                loadingProgressManager.hideLoading();
                PopupDialogUtil.showCustomDialog(ProductDetailActivity.this, new PopupDialogClickListener() {
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
//                    views.setText(sUtil.replaceStringPriceToInt(productItem.getViews()));
                    current_price = productItem.getPrice_value();
                    String url = productItem.getImage();
                    if(!url.contains("https:")){
                        url = "https:" + url;
                    }
                    Glide.with(getApplicationContext())
                            .load(url)
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(20))) // 여기서 10은 코너의 반지름을 dp 단위로 지정
                            .into(image);

                    rating.setRating(Float.parseFloat(productItem.getRating()));
                    String ratingTotalCount = sUtil.replaceStringPriceToInt(productItem.getRating_total_count());
                    if (ratingTotalCount != null && !ratingTotalCount.isEmpty()) {
                        rating_total_count.setText("(" + ratingTotalCount + ")");
                    } else {
                        rating_total_count.setText("");
                    }


                    if(productItem.getRocket_baesong() != null && !"".equals(productItem.getRocket_baesong())){
                        rocket.setVisibility(View.VISIBLE);
                        rocket.setText(productItem.getRocket_baesong());
                    } else {
                        rocket.setVisibility(View.GONE);
                    }

                    if(productItem.isIs_Favorite()){
                        favorite.setImageResource(R.drawable.baseline_favorite_24);
                    }else{
                        favorite.setImageResource(R.drawable.baseline_favorite_border_24);
                    }


                    getProductsRelated(productItem.getName());

                }
                else{
                }
                loadingProgressManager.hideLoading();

            }

            @Override
            public void onFailure(Call<ProductItem> call, Throwable t) {
                loadingProgressManager.hideLoading();
                PopupDialogUtil.showCustomDialog(ProductDetailActivity.this, new PopupDialogClickListener() {
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

    public void getProductsRelated(String product_name){

        loadingProgressManager.showLoading(this);
        retrofit2.Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);   // 레트로핏 인터페이스 객체 구현

        Call<List<ProductItem>> call = service.getProductsRelated("getProductsRelated", MainApplication.ANDROID_ID, product_name);

        call.enqueue(new Callback<List<ProductItem>>() {
            @Override
            public void onResponse(Call<List<ProductItem>> call, Response<List<ProductItem>> response) {
                if(response.isSuccessful()){
                    List<ProductItem> productItem = response.body();
                    productHoriAdapter.addItems(productItem);
                }
                else{
                }

                loadingProgressManager.hideLoading();
            }

            @Override
            public void onFailure(Call<List<ProductItem>> call, Throwable t) {
                loadingProgressManager.hideLoading();
                PopupDialogUtil.showCustomDialog(ProductDetailActivity.this, new PopupDialogClickListener() {
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