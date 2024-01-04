package com.sarin.prod.goodshost.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.adapter.ProductAdapter;
import com.sarin.prod.goodshost.databinding.ActivityCategoryProductListBinding;
import com.sarin.prod.goodshost.item.ProductItem;
import com.sarin.prod.goodshost.network.RetrofitClientInstance;
import com.sarin.prod.goodshost.network.RetrofitInterface;
import com.sarin.prod.goodshost.util.LoadingDialogManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryProductListActivity extends AppCompatActivity {

    private ActivityCategoryProductListBinding binding;
    private static String TAG = MainApplication.TAG;

    private static RecyclerView recyclerView;
    public static ProductAdapter productAdapter;
    private LoadingDialogManager loadingDialogManager;
    private List<ProductItem> piLIst = new ArrayList<>();

    private ImageView exit;
    private TextView logo;

    private LinearLayout LinearLayout1;
    private boolean isScrollListenerAdded = false;
    private RecyclerView.OnScrollListener scrollListener;
    private String api_code;
    private String name;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryProductListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadingDialogManager = new LoadingDialogManager();

        recyclerView = binding.recyclerView;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        productAdapter = new ProductAdapter(piLIst);
        recyclerView.setAdapter(productAdapter);

        Intent intent = getIntent();
        api_code = intent.getStringExtra("api_code");
        name = intent.getStringExtra("name");

        exit = binding.exit;
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        logo = binding.logo;
        logo.setText(name);


        loadingDialogManager = new LoadingDialogManager();

        LinearLayout1 = binding.LinearLayout1;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean isScrolledDown = false;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_SETTLING && isScrolledDown) {
                    LinearLayout1.setVisibility(View.VISIBLE);

                }else if(newState == RecyclerView.SCROLL_STATE_SETTLING && !isScrolledDown){
                    if (!piLIst.isEmpty()) {
                        LinearLayout1.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                isScrolledDown = dy < 0;
            }
        });


        getCategoryProducts(api_code);
        initScrollListener();

    }

    private int CategoryProducts_page = 0;
    public void getCategoryProducts(String category_id){
//        Log.d(TAG, "page: " + CategoryProducts_page);

        loadingDialogManager.showLoading(getSupportFragmentManager());
        retrofit2.Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);   // 레트로핏 인터페이스 객체 구현

        Call<List<ProductItem>> call = service.getCategoryProductList("getCategoryProducts", category_id, CategoryProducts_page++);

        call.enqueue(new Callback<List<ProductItem>>() {
            @Override
            public void onResponse(Call<List<ProductItem>> call, Response<List<ProductItem>> response) {
                if(response.isSuccessful()){
                    List<ProductItem> productItem = response.body();
                    piLIst.addAll(productItem);
                    productAdapter.notifyDataSetChanged();

                }
                else{
                    // 실패
                    Log.e(TAG, "실패 코드 확인 : " + response.code());
                    Log.e(TAG, "연결 주소 확인 : " + response.raw().request().url().url());
                }
                loadingDialogManager.hideLoading();
            }

            @Override
            public void onFailure(Call<List<ProductItem>> call, Throwable t) {
                // 통신 실패
                Log.e(TAG, "onFailure: " + t.getMessage());
                loadingDialogManager.hideLoading();
            }
        });

    }

    boolean isLoading = false;

    private void initScrollListener() {
        if (!isScrollListenerAdded) {
            scrollListener = new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                    if (!isLoading) {
                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == piLIst.size() - 10) {
                            loadMore();
                            isLoading = true;
                        }
                    }
                }
            };

            recyclerView.addOnScrollListener(scrollListener);
            isScrollListenerAdded = true;
        }


    }

    private void loadMore() {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int position = linearLayoutManager.findFirstVisibleItemPosition();
        Log.d(TAG, "position: " + position);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getCategoryProducts(api_code);
                isLoading = false;
            }
        }, 1000);

    }






    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingDialogManager != null) {
            loadingDialogManager.hideLoading();
        }

        if (isScrollListenerAdded && recyclerView != null) {
            recyclerView.removeOnScrollListener(scrollListener);
            isScrollListenerAdded = false;
        }

    }
}