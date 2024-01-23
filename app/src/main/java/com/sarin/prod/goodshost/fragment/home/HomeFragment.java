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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.activity.CategoryProductListActivity;
import com.sarin.prod.goodshost.activity.ProductDetailActivity;
import com.sarin.prod.goodshost.adapter.CategoryAdapter;
import com.sarin.prod.goodshost.adapter.CategoryProductListAdapter;
import com.sarin.prod.goodshost.adapter.FavoriteProductAdapter;
import com.sarin.prod.goodshost.adapter.ProductAdapter;
import com.sarin.prod.goodshost.adapter.ProductAdapterHori;
import com.sarin.prod.goodshost.adapter.RecyclerViewClickListener;

import com.sarin.prod.goodshost.databinding.FragmentHomeBinding;
import com.sarin.prod.goodshost.item.CategoryItem;
import com.sarin.prod.goodshost.item.ProductItem;
import com.sarin.prod.goodshost.item.ReturnMsgItem;
import com.sarin.prod.goodshost.item.SharedViewModel;
import com.sarin.prod.goodshost.network.RetrofitClientInstance;
import com.sarin.prod.goodshost.network.RetrofitInterface;
import com.sarin.prod.goodshost.util.LoadingProgressManager;
import com.sarin.prod.goodshost.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements RecyclerViewClickListener{

    private FragmentHomeBinding binding;

    public static String TAG = MainApplication.TAG;

    private static RecyclerView recyclerView;
    private static RecyclerView recyclerViewTop;
    private static RecyclerView categoryRecyclerView;

    public static ProductAdapter productAdapter;
    public static ProductAdapterHori productHoriAdapter;
    public static CategoryAdapter categoryAdapter;
    private static ImageView favorite;
    private static TextView gmall_best_totalView, gmall_top_totalView;

    private List<ProductItem> piLIst = new ArrayList<>();
    public List<ProductItem> TopProductList = new ArrayList<>();
    private String currentCategoryCode = "1001";

    private List<CategoryItem> ctLIst = new ArrayList<>();

    private static HomeFragment mInstnace = null;

    private ProductItem pdItem = new ProductItem();
    private int pdItem_possion = 0;

    private LoadingProgressManager loadingProgressManager = LoadingProgressManager.getInstance();

    private StringUtil sUtil = StringUtil.getInstance();

    private SharedViewModel viewModel;

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

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getFavoriteStatus().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isFavorite) {
                Log.d(TAG, "1111111111111111111111111111111111111111111111");
                // 즐겨찾기 아이콘 업데이트
                updateFavoriteIcon(isFavorite);
            }
        });

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
         * 인기 상품
         */
        recyclerViewTop = binding.recyclerViewTop;
        LinearLayoutManager toplayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
//        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 가로 2개 나열 할때.
        recyclerViewTop.setLayoutManager(toplayoutManager);
        productAdapter = new ProductAdapter(TopProductList, this);
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


        productHoriAdapter.setOnItemClickListener(new ProductAdapterHori.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                //save = bottomSheetDialog.findViewById(R.id.save);
                pdItem = productHoriAdapter.get(pos);
                pdItem_possion = pos;

                if (v.getId() == R.id.layout_favorite) {
                    if(pdItem.isIs_Favorite()){
                        setDelUserItemMap(MainApplication.ANDROID_ID, pdItem.getVendor_item_id());
                        pdItem.setIs_Favorite(false);
                        productHoriAdapter.set(pos, pdItem);
                    } else {
                        setUserItemMap(MainApplication.ANDROID_ID, pdItem.getVendor_item_id(), 0, "Y");
                        pdItem.setIs_Favorite(true);
                        productHoriAdapter.set(pos, pdItem);
                    }

                }

            }
        });

        return root;
    }

    @Override
    public void onItemClickListener(View v, int pos) {
        // 아이템 클릭 이벤트 처리
        pdItem = productAdapter.get(pos);
        pdItem_possion = pos;
        Log.d(TAG, "v id: " + v.getId());
        if (v.getId() == R.id.layout_favorite) {
            if(pdItem.isIs_Favorite()){
                setDelUserItemMap(MainApplication.ANDROID_ID, pdItem.getVendor_item_id());
                pdItem.setIs_Favorite(false);
                productAdapter.set(pos, pdItem);
            } else {
                setUserItemMap(MainApplication.ANDROID_ID, pdItem.getVendor_item_id(), 0, "Y");
                pdItem.setIs_Favorite(true);
                productAdapter.set(pos, pdItem);
            }

        } else {
            Log.d(TAG, "position: " + pos);
            Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
            intent.putExtra("vendor_item_id", pdItem.getVendor_item_id());
            v.getContext().startActivity(intent);	//intent 에 명시된 액티비티로 이동
        }
    }

    private void updateFavoriteIcon(boolean isFavorite) {
        // 즐겨찾기 아이콘을 업데이트하는 로직
        Log.d(TAG, "updateFavoriteIcon: " + isFavorite);
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


    public void getTopProducts(int cnt, int page, String code){

        loadingProgressManager.showLoading(MainApplication.activity);
        retrofit2.Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);   // 레트로핏 인터페이스 객체 구현

        Call<List<ProductItem>> call = service.getTopProducts("getTopProducts", cnt, page, MainApplication.ANDROID_ID, code);

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

        Call<List<ProductItem>> call = service.getBestSalesProducts("getBestSalesProducts", cnt, page, MainApplication.ANDROID_ID, "0");

        call.enqueue(new Callback<List<ProductItem>>() {
            @Override
            public void onResponse(Call<List<ProductItem>> call, Response<List<ProductItem>> response) {
                if(response.isSuccessful()){
                    List<ProductItem> productItem = response.body();
                    Log.d(TAG, "1111111111111111111" + productItem.toString());
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
    public void onResume() {
        super.onResume();
//        productAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}