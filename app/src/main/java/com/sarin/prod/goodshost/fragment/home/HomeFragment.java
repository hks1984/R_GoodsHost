package com.sarin.prod.goodshost.fragment.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.adapter.CategoryAdapter;
import com.sarin.prod.goodshost.adapter.ProductAdapter;
import com.sarin.prod.goodshost.databinding.FragmentHomeBinding;
import com.sarin.prod.goodshost.item.CategoryItem;
import com.sarin.prod.goodshost.item.ProductItem;
import com.sarin.prod.goodshost.network.RetrofitClientInstance;
import com.sarin.prod.goodshost.network.RetrofitInterface;
import com.sarin.prod.goodshost.util.LoadingDialogManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public static String TAG = MainApplication.TAG;

    private static RecyclerView recyclerView;
    private static RecyclerView categoryRecyclerView;

    public static ProductAdapter productAdapter;
    public static CategoryAdapter categoryAdapter;
    private static ImageView menu;
    private LoadingDialogManager loadingDialogManager;
    private List<ProductItem> piLIst = new ArrayList<>();
    private List<CategoryItem> ctLIst = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        loadingDialogManager = new LoadingDialogManager();

        recyclerView = binding.recyclerView;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        productAdapter = new ProductAdapter(piLIst);
        recyclerView.setAdapter(productAdapter);

        categoryRecyclerView = binding.categoryRecyclerView;
        LinearLayoutManager catelayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        categoryRecyclerView.setLayoutManager(catelayoutManager);
        categoryAdapter = new CategoryAdapter(ctLIst);
        categoryRecyclerView.setAdapter(categoryAdapter);

        getCategoryList();
        getBestSalesProducts(5, 0);

        initScrollListener();

        return root;
    }


    private int BestSalesProducts_page = 0;
    public void getBestSalesProducts(int cnt, int page){
//        Log.d(TAG, "page: " + CategoryProducts_page);
        loadingDialogManager.showLoading(requireActivity().getSupportFragmentManager());

        retrofit2.Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);   // 레트로핏 인터페이스 객체 구현

        Call<List<ProductItem>> call = service.getBestSalesProducts("getBestSalesProducts", cnt, page);

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

    public void getCategoryList(){
//        Log.d(TAG, "page: " + CategoryProducts_page);

        retrofit2.Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);   // 레트로핏 인터페이스 객체 구현

        Call<List<CategoryItem>> call = service.getCategoryList("getCategoryList");

        call.enqueue(new Callback<List<CategoryItem>>() {
            @Override
            public void onResponse(Call<List<CategoryItem>> call, Response<List<CategoryItem>> response) {
                if(response.isSuccessful()){
                    List<CategoryItem> categoryItem = response.body();
                    ctLIst.addAll(categoryItem);
                    categoryAdapter.notifyDataSetChanged();
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

    boolean isLoading = false;

    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                Log.d(TAG, "current size: [" + linearLayoutManager.findLastCompletelyVisibleItemPosition() + "], piLIst.size():[" + piLIst.size() + "]");

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == piLIst.size() - 10) {
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });


    }

    private void loadMore() {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int position = linearLayoutManager.findFirstVisibleItemPosition();
        Log.d(TAG, "position: " + position);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                getCategoryProducts("510222");
                isLoading = false;
            }
        }, 1000);

    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}