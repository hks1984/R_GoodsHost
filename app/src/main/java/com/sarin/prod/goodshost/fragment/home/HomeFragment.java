package com.sarin.prod.goodshost.fragment.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.activity.CategoryProductListActivity;
import com.sarin.prod.goodshost.adapter.CategoryAdapter;
import com.sarin.prod.goodshost.adapter.CategoryProductListAdapter;
import com.sarin.prod.goodshost.adapter.ProductAdapter;
import com.sarin.prod.goodshost.adapter.ProductAdapterHori;
import com.sarin.prod.goodshost.databinding.FragmentHomeBinding;
import com.sarin.prod.goodshost.item.CategoryItem;
import com.sarin.prod.goodshost.item.ProductItem;
import com.sarin.prod.goodshost.network.RetrofitClientInstance;
import com.sarin.prod.goodshost.network.RetrofitInterface;
import com.sarin.prod.goodshost.util.LoadingProgressManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public static String TAG = MainApplication.TAG;

    private static RecyclerView recyclerView;
    private static RecyclerView recyclerViewTop;
    private static RecyclerView categoryRecyclerView;

    public static ProductAdapter productAdapter;
    public static ProductAdapterHori productHoriAdapter;
    public static CategoryAdapter categoryAdapter;
    private static ImageView menu;
    private static TextView gmall_best_totalView, gmall_top_totalView;

    private List<ProductItem> piLIst = new ArrayList<>();
    public List<ProductItem> TopProductList = new ArrayList<>();
    private String currentCategoryCode = "1001";

    private List<CategoryItem> ctLIst = new ArrayList<>();

    private static HomeFragment mInstnace = null;

    private LoadingProgressManager loadingProgressManager = LoadingProgressManager.getInstance();

    public static HomeFragment getInstance() {
        if (mInstnace == null) {
            mInstnace = new HomeFragment();
        }

        return mInstnace;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);


        /**
         * 최저가 순위
         */
        recyclerView = binding.recyclerView;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
//        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 가로 2개 나열 할때.
        recyclerView.setLayoutManager(layoutManager);
        productHoriAdapter = new ProductAdapterHori(piLIst);
        recyclerView.setAdapter(productHoriAdapter);

        /**
         * 베스트 상품
         */
        recyclerViewTop = binding.recyclerViewTop;
        LinearLayoutManager toplayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
//        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 가로 2개 나열 할때.
        recyclerViewTop.setLayoutManager(toplayoutManager);
        productAdapter = new ProductAdapter(TopProductList);
        recyclerViewTop.setAdapter(productAdapter);

        /**
         * 카테고리
         */
        categoryRecyclerView = binding.categoryRecyclerView;
        LinearLayoutManager catelayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        categoryRecyclerView.setLayoutManager(catelayoutManager);
        categoryAdapter = new CategoryAdapter(ctLIst);
        categoryRecyclerView.setAdapter(categoryAdapter);

        gmall_best_totalView = binding.gmallBestTotalView;
        gmall_best_totalView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CategoryProductListActivity.class);
                intent.putExtra("flag", "best");
                v.getContext().startActivity(intent);	//intent 에 명시된 액티비티로 이동

            }
        });

        gmall_top_totalView = binding.gmallTopTotalView;
        gmall_top_totalView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CategoryProductListActivity.class);
                intent.putExtra("flag", "top");
                v.getContext().startActivity(intent);	//intent 에 명시된 액티비티로 이동
            }
        });

        if(categoryAdapter.size() <= 0){
            Log.d(TAG, "categoryAdapter.size() : " + categoryAdapter.size());
            getCategoryList();
        }

        if(productHoriAdapter.size() <= 0){
            getBestSalesProducts(10, 0);
        }

        if(productAdapter.size() <= 0){
            getTopProducts(10, 0, currentCategoryCode);
        }

        return root;
    }

    public void getTopProducts(int cnt, int page, String code){

        loadingProgressManager.showLoading(MainApplication.activity);
        retrofit2.Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);   // 레트로핏 인터페이스 객체 구현

        Call<List<ProductItem>> call = service.getTopProducts("getTopProducts", cnt, page, code);

        call.enqueue(new Callback<List<ProductItem>>() {
            @Override
            public void onResponse(Call<List<ProductItem>> call, Response<List<ProductItem>> response) {
                if(response.isSuccessful()){
                    List<ProductItem> productItem = response.body();
                    productAdapter.addItems(productItem);

                }
                else{
                    // 실패
                    Log.e(TAG, "실패 코드 확인 : " + response.code());
                    Log.e(TAG, "연결 주소 확인 : " + response.raw().request().url().url());
                }

                loadingProgressManager.hideLoading();
            }

            @Override
            public void onFailure(Call<List<ProductItem>> call, Throwable t) {
                // 통신 실패
                Log.e(TAG, "onFailure: " + t.getMessage());

                loadingProgressManager.hideLoading();
            }
        });


    }


    public void getBestSalesProducts(int cnt, int page){
//        Log.d(TAG, "page: " + CategoryProducts_page);


        loadingProgressManager.showLoading(getContext());

        retrofit2.Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);   // 레트로핏 인터페이스 객체 구현

        Call<List<ProductItem>> call = service.getBestSalesProducts("getBestSalesProducts", cnt, page, "0");

        call.enqueue(new Callback<List<ProductItem>>() {
            @Override
            public void onResponse(Call<List<ProductItem>> call, Response<List<ProductItem>> response) {
                if(response.isSuccessful()){
                    List<ProductItem> productItem = response.body();
                    productHoriAdapter.addItems(productItem);

                }
                else{
                    // 실패
                    Log.e(TAG, "실패 코드 확인 : " + response.code());
                    Log.e(TAG, "연결 주소 확인 : " + response.raw().request().url().url());
                }

                loadingProgressManager.hideLoading();
            }

            @Override
            public void onFailure(Call<List<ProductItem>> call, Throwable t) {
                // 통신 실패
                Log.e(TAG, "onFailure: " + t.getMessage());

                loadingProgressManager.hideLoading();
            }
        });

    }



    public void getCategoryList(){
//        Log.d(TAG, "page: " + CategoryProducts_page);
//        loadingDialogManager.showLoading(requireActivity().getSupportFragmentManager());

        loadingProgressManager.showLoading(getContext());
        retrofit2.Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);   // 레트로핏 인터페이스 객체 구현

        Call<List<CategoryItem>> call = service.getCategoryList("getCategoryList");

        call.enqueue(new Callback<List<CategoryItem>>() {
            @Override
            public void onResponse(Call<List<CategoryItem>> call, Response<List<CategoryItem>> response) {
                if(response.isSuccessful()){
                    List<CategoryItem> categoryItem = response.body();

                    CategoryItem ci = new CategoryItem();
                    ci.setApi_code("0");
                    ci.setName("전체");
                    categoryAdapter.addItem(ci);
                    categoryAdapter.addItems(categoryItem);

                }
                else{
                    // 실패
                    Log.e(TAG, "실패 코드 확인 : " + response.code());
                    Log.e(TAG, "연결 주소 확인 : " + response.raw().request().url().url());
                }
                loadingProgressManager.hideLoading();
            }

            @Override
            public void onFailure(Call<List<CategoryItem>> call, Throwable t) {
                // 통신 실패
                Log.e(TAG, "onFailure: " + t.getMessage());
                loadingProgressManager.hideLoading();
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}