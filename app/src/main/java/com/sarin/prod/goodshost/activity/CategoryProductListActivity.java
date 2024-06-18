package com.sarin.prod.goodshost.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.adapter.CateRecyclerViewClickListener;
import com.sarin.prod.goodshost.adapter.CategoryProductListAdapter;
import com.sarin.prod.goodshost.adapter.RecyclerViewClickListener;

import com.sarin.prod.goodshost.adapter.ProductAdapter;
import com.sarin.prod.goodshost.databinding.ActivityCategoryProductListBinding;
import com.sarin.prod.goodshost.fragment.home.HomeFragment;
import com.sarin.prod.goodshost.item.CategoryItem;
import com.sarin.prod.goodshost.item.ProductItem;
import com.sarin.prod.goodshost.item.ReturnMsgItem;
import com.sarin.prod.goodshost.network.RetrofitClientInstance;
import com.sarin.prod.goodshost.network.RetrofitInterface;
import com.sarin.prod.goodshost.util.LoadingProgressManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.sarin.prod.goodshost.util.StringUtil;
import com.sarin.prod.goodshost.view.PopupDialogUtil;
import com.sarin.prod.goodshost.view.PopupDialogClickListener;


public class CategoryProductListActivity extends AppCompatActivity implements RecyclerViewClickListener, CateRecyclerViewClickListener {

    private ActivityCategoryProductListBinding binding;
    private static String TAG = MainApplication.TAG;

    private static RecyclerView recyclerView;
    public static ProductAdapter productAdapter;
    private LoadingProgressManager loadingProgressManager = LoadingProgressManager.getInstance();
    private List<ProductItem> piLIst = new ArrayList<>();

    private ImageView exit;
    private TextView logo, categoryName;

    private LinearLayout LinearLayout1;
    private boolean isScrollListenerAdded = false;
    private RecyclerView.OnScrollListener scrollListener;
    private String flag;
    public int viewCount = 50;
    public static int page = 0;
    public static String categoryCode;

    public static CategoryProductListAdapter categoryProductListAdapter;
    private static RecyclerView categoryRecyclerView;
    private List<CategoryItem> ctLIst = new ArrayList<>();

    private static CategoryProductListActivity mInstnace = null;

    private ProductItem pdItem = new ProductItem();
    private int pdItem_possion = 0;

    private int selectedItem = -1;


    public static CategoryProductListActivity getInstance() {
        if (mInstnace == null) {
            mInstnace = new CategoryProductListActivity();
        }

        return mInstnace;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryProductListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerView = binding.recyclerView;
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
//        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2)); // 가로 2개 나열 할때.
        productAdapter = new ProductAdapter(Glide.with(this) ,piLIst, this);
        recyclerView.setAdapter(productAdapter);

        Intent intent = getIntent();
        flag = intent.getStringExtra("flag");

        exit = binding.exit;

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.none, R.anim.to_right_exit);

            }
        });

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
                // 처음 리스트가 생성되었을때 스크롤을 위로 올릴때는 뷰가 숨겨지면 안된다.
                if(dy == 0){
                    isScrolledDown = true;
                }
            }
        });

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View otherLayout = inflater.inflate(R.layout.category_list_view, null);

        // 인플레이트한 레이아웃에서 뷰 찾기
        categoryName = otherLayout.findViewById(R.id.categoryName);



        categoryRecyclerView = binding.categoryRecyclerView;
        LinearLayoutManager catelayoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false);
        categoryRecyclerView.setLayoutManager(catelayoutManager);
        CategoryItem ci = new CategoryItem();
        categoryCode = "0";
        ci.setApi_code(categoryCode);
        ci.setName("전체");
        ctLIst.add(ci);
        categoryProductListAdapter = new CategoryProductListAdapter(ctLIst, this);
        categoryRecyclerView.setAdapter(categoryProductListAdapter);
        categoryProductListAdapter.notifyItemChanged(1);


        logo = binding.logo;


        if(flag.equals("top")){
            categoryProductListAdapter.setMode("top");
            getTopProducts(viewCount, categoryCode);
            logo.setText(getResources().getString(R.string.top_products));
        } else if(flag.equals("best")) {
            categoryProductListAdapter.setMode("best");
            getBestSalesProducts(viewCount, categoryCode);
            logo.setText(getResources().getString(R.string.best_sale));
        }

        getCategoryList();
        initScrollListener();

    }

    @Override
    public void onItemClickListener(View v, int pos) {
        // 아이템 클릭 이벤트 처리
        pdItem = productAdapter.get(pos);
        pdItem_possion = pos;
        if (v.getId() == R.id.layout_favorite) {
            if(pdItem.isIs_Favorite()){
                setDelUserItemMap(MainApplication.USER_ID, pdItem.getVendor_item_id());
                pdItem.setIs_Favorite(false);
                productAdapter.set(pos, pdItem);
            } else {
                setUserItemMap(MainApplication.USER_ID, pdItem.getVendor_item_id(), "Y", 0, "Y");
                pdItem.setIs_Favorite(true);
                productAdapter.set(pos, pdItem);
            }

        } else if (v.getId() == R.id.list_view_hori) {
            Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
            intent.putExtra("vendor_item_id", pdItem.getVendor_item_id());
            v.getContext().startActivity(intent);	//intent 에 명시된 액티비티로 이동
        }
    }

    @Override
    public void cateOnItemClickListener(View v, int pos) {

        categoryProductListAdapter.setSelectedItem(pos);
        productAdapter.clear();
        categoryCode = categoryProductListAdapter.get(pos).api_code;
        page = 0;
        if("best".equals(categoryProductListAdapter.getMode())){
            getBestSalesProducts(viewCount, categoryProductListAdapter.get(pos).api_code);
        }else{
            getTopProducts(viewCount, categoryProductListAdapter.get(pos).api_code);
        }

    }



    @Override
    public void onItemClickListener_Hori(View v, int pos) {}

    public void setUserItemMap(String user_id, String vendor_item_id, String hope_low_price, int hope_price, String hope_stock){

        retrofit2.Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);   // 레트로핏 인터페이스 객체 구현

        Call<ReturnMsgItem> call = service.setUserItemMap("setUserItemMap", user_id, vendor_item_id, hope_low_price, hope_price, hope_stock);
        call.enqueue(new Callback<ReturnMsgItem>() {
            @Override
            public void onResponse(Call<ReturnMsgItem> call, Response<ReturnMsgItem> response) {
                if(response.isSuccessful()){
                    ReturnMsgItem returnMsgItem = response.body();
                    


                }
                else{
                }
            }
            @Override
            public void onFailure(Call<ReturnMsgItem> call, Throwable t) {
                PopupDialogUtil.showCustomDialog(CategoryProductListActivity.this, new PopupDialogClickListener() {
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
                PopupDialogUtil.showCustomDialog(CategoryProductListActivity.this, new PopupDialogClickListener() {
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

    public void getCategoryList(){


        loadingProgressManager.showLoading(this);

        retrofit2.Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);   // 레트로핏 인터페이스 객체 구현

        Call<List<CategoryItem>> call = service.getCategoryList("getCategoryList");

        call.enqueue(new Callback<List<CategoryItem>>() {
            @Override
            public void onResponse(Call<List<CategoryItem>> call, Response<List<CategoryItem>> response) {
                if(response.isSuccessful()){
                    List<CategoryItem> categoryItem = response.body();
                    categoryProductListAdapter.addItems(categoryItem);

                }
                else{

                }
                loadingProgressManager.hideLoading();
            }

            @Override
            public void onFailure(Call<List<CategoryItem>> call, Throwable t) {
                loadingProgressManager.hideLoading();

                PopupDialogUtil.showCustomDialog(CategoryProductListActivity.this, new PopupDialogClickListener() {
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



    public void getTopProducts(int cnt, String code){

        loadingProgressManager.showLoading(this);
        retrofit2.Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);   // 레트로핏 인터페이스 객체 구현

        Call<List<ProductItem>> call = service.getTopProducts("getTopProducts", cnt, page++, MainApplication.USER_ID, code);

        call.enqueue(new Callback<List<ProductItem>>() {
            @Override
            public void onResponse(Call<List<ProductItem>> call, Response<List<ProductItem>> response) {
                if(response.isSuccessful()){
                    List<ProductItem> productItem = response.body();
                    productAdapter.addItems(productItem);

                }
                else{
                   
                }

                loadingProgressManager.hideLoading();
            }

            @Override
            public void onFailure(Call<List<ProductItem>> call, Throwable t) {
              
                loadingProgressManager.hideLoading();
                PopupDialogUtil.showCustomDialog(CategoryProductListActivity.this, new PopupDialogClickListener() {
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
    public void getBestSalesProducts(int cnt, String code){

        loadingProgressManager.showLoading(this);
        retrofit2.Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);   // 레트로핏 인터페이스 객체 구현

        Call<List<ProductItem>> call = service.getBestSalesProducts("getBestSalesProducts", cnt, page++, MainApplication.USER_ID, code);

        call.enqueue(new Callback<List<ProductItem>>() {
            @Override
            public void onResponse(Call<List<ProductItem>> call, Response<List<ProductItem>> response) {
                if(response.isSuccessful()){
                    List<ProductItem> productItem = response.body();
                    productAdapter.addItems(productItem);
                }
                else{
                   
                }
                loadingProgressManager.hideLoading();

            }

            @Override
            public void onFailure(Call<List<ProductItem>> call, Throwable t) {
                           
                loadingProgressManager.hideLoading();
                PopupDialogUtil.showCustomDialog(CategoryProductListActivity.this, new PopupDialogClickListener() {
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

                    if(productAdapter.size() > 4){
                        if (!isLoading) {
                            if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == productAdapter.size() - 1) {
                                loadMore();
                                isLoading = true;
                            }
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
        
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(flag.equals("top")){
                    getTopProducts(viewCount, categoryCode);
                } else if(flag.equals("best")) {
                    getBestSalesProducts(viewCount, categoryCode);
                }
                isLoading = false;
            }
        }, 1000);

    }






    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingProgressManager != null) {
            loadingProgressManager.hideLoading();
        }

        if (isScrollListenerAdded && recyclerView != null) {
            recyclerView.removeOnScrollListener(scrollListener);
            isScrollListenerAdded = false;
        }

        page = 0;
        categoryCode = "";

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isFinishing()) {
            // back 버튼으로 화면 종료가 야기되면 동작한다.
            overridePendingTransition(R.anim.none, R.anim.to_right_exit);
        }
    }

}