package com.sarin.prod.goodshost.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.adapter.ExpandableListAdapter;
import com.sarin.prod.goodshost.adapter.ProductAdapter;
import com.sarin.prod.goodshost.databinding.ActivityCategoryMenuBinding;
import com.sarin.prod.goodshost.databinding.ActivityProductDetailBinding;
import com.sarin.prod.goodshost.item.CategoryItem;
import com.sarin.prod.goodshost.item.ProductItem;
import com.sarin.prod.goodshost.network.RetrofitClientInstance;
import com.sarin.prod.goodshost.network.RetrofitInterface;
import com.sarin.prod.goodshost.util.LoadingDialogManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryMenuActivity extends AppCompatActivity {

    private ActivityCategoryMenuBinding binding;
    private static String TAG = MainApplication.TAG;

    private static RecyclerView recyclerView;
    public static ExpandableListAdapter expandableListAdapter;
    private static ImageView exit;
    private LoadingDialogManager loadingDialogManager;

    private List<CategoryItem> categoryItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerView = binding.recyclerView;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        exit = binding.exit;
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadingDialogManager = new LoadingDialogManager();

        getCategoryList();




    }


    public void getCategoryList(){
//        Log.d(TAG, "page: " + CategoryProducts_page);

        retrofit2.Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);   // 레트로핏 인터페이스 객체 구현

        Call<List<CategoryItem>> call = service.getCategoryList("getCategoryList");

        call.enqueue(new Callback<List<CategoryItem>>() {
            @Override
            public void onResponse(Call<List<CategoryItem>> call, Response<List<CategoryItem>> response) {
                if(response.isSuccessful()){
                    categoryItem = response.body();
                    Log.d(TAG, "productItem: " + categoryItem.toString());
//                    piLIst.addAll(productItem);
//                    productAdapter.notifyDataSetChanged();

//                    List<ExpandableListAdapter.Item> data = new ArrayList<>();
//
//                    ExpandableListAdapter.Item places = new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, categoryItem.get(0).name);
//                    places.invisibleChildren = new ArrayList<>();
//                    places.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, categoryItem.get(1).name));
//                    data.add(places);
//
//                    places = new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, categoryItem.get(2).name);
//                    places.invisibleChildren = new ArrayList<>();
//                    places.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, categoryItem.get(3).name));
//                    data.add(places);
//
//                    recyclerView.setAdapter(new ExpandableListAdapter(data));

                    List<ExpandableListAdapter.Item> data = new ArrayList<>();
                    Map<String, ExpandableListAdapter.Item> headerMap = new HashMap<>();

                    for (CategoryItem category : categoryItem) {
                        if ("1".equals(category.getLevel())) {
                            // Level 1 카테고리는 헤더로 추가
                            ExpandableListAdapter.Item header = new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, category.getName());
                            header.invisibleChildren = new ArrayList<>();
                            data.add(header);
                            headerMap.put(category.getCode(), header);
                        } else if ("2".equals(category.getLevel())) {
                            // Level 2 카테고리는 상위 카테고리의 invisibleChildren으로 추가
                            ExpandableListAdapter.Item child = new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, category.getName());
                            ExpandableListAdapter.Item parentHeader = headerMap.get(category.getPcode());
                            if (parentHeader != null && parentHeader.invisibleChildren != null) {
                                parentHeader.invisibleChildren.add(child);
                            }
                        }
                    }

                    recyclerView.setAdapter(new ExpandableListAdapter(data));

                }
                else{
                    // 실패
                    Log.e(TAG, "실패 코드 확인 : " + response.code());
                    Log.e(TAG, "연결 주소 확인 : " + response.raw().request().url().url());
                }
            }

            @Override
            public void onFailure(Call<List<CategoryItem>> call, Throwable t) {
                // 통신 실패
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingDialogManager != null) {
            loadingDialogManager.hideLoading();
        }

    }
}