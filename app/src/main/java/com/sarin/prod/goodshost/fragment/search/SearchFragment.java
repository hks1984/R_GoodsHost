package com.sarin.prod.goodshost.fragment.search;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.adapter.ProductAdapter;
import com.sarin.prod.goodshost.adapter.FavoriteSearcherAdapter;
import com.sarin.prod.goodshost.adapter.RecentAdapter;
import com.sarin.prod.goodshost.databinding.FragmentSearchBinding;
import com.sarin.prod.goodshost.item.ProductItem;
import com.sarin.prod.goodshost.item.RecentSearcherItem;
import com.sarin.prod.goodshost.network.RetrofitClientInstance;
import com.sarin.prod.goodshost.network.RetrofitInterface;
import com.sarin.prod.goodshost.util.LoadingProgressManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;

    public static String TAG = MainApplication.TAG;

    private static RecyclerView recyclerView;
    private static RecyclerView favoriteSearcherRecyclerView;
    private static RecyclerView recentRecyclerView;
    private RecyclerView.OnScrollListener scrollListener;
    private boolean isScrollListenerAdded = false;
    public static ProductAdapter productAdapter;
    public static FavoriteSearcherAdapter favoriteSearcherAdapter;
    public static RecentAdapter recentAdapter;

    private List<ProductItem> piLIst = new ArrayList<>();
    private AutoCompleteTextView autoCompleteTextView;
    private String searchName = "";
    private int SearchProducts_page = 0;
    private List<String> favoriteSearcherList = new ArrayList<>();
    private List<RecentSearcherItem> recentList = new ArrayList<>();

    private LoadingProgressManager loadingProgressManager = LoadingProgressManager.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SearchViewModel searchViewModel =
                new ViewModelProvider(this).get(SearchViewModel.class);

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.recyclerView;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        productAdapter = new ProductAdapter(piLIst);
        recyclerView.setAdapter(productAdapter);

        List<String> androids = new ArrayList<>();
        androids.add("좋아하는 색은 빨간색");
        androids.add("좋아하는 색은 파란색");
        androids.add("좋아하는 색은 노란색");

        autoCompleteTextView = binding.editTextSearchBox;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.my_auto_complete_box, androids);
        autoCompleteTextView.setAdapter(adapter);

        //엔터키 이벤트 처리
        autoCompleteTextView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //키패드 내리기
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);

                    searchName = String.valueOf(autoCompleteTextView.getText());
                    getSearchProducts(searchName);
                    initScrollListener();
                    return true;
                }
                return false;
            }
        });

        autoCompleteTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (autoCompleteTextView.getRight() - autoCompleteTextView.getCompoundDrawables()[2].getBounds().width() - autoCompleteTextView.getPaddingRight())) {
                        // 드로어블이 클릭되었을 때의 동작을 여기에 작성합니다.
                        // 예: 검색 기능 실행, 입력 내용 지우기 등
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);

                        searchName = String.valueOf(autoCompleteTextView.getText());
                        getSearchProducts(searchName);
                        initScrollListener();
                        return true;
                    }
                }
                return false;
            }
        });


        favoriteSearcherRecyclerView = binding.favoriteSearcherList;
        LinearLayoutManager favoriteSearcheroutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        favoriteSearcherRecyclerView.setLayoutManager(favoriteSearcheroutManager);
        favoriteSearcherAdapter = new FavoriteSearcherAdapter(favoriteSearcherList);
        favoriteSearcherRecyclerView.setAdapter(favoriteSearcherAdapter);

        favoriteSearcherList.add("asdf");
        favoriteSearcherList.add("asdsdf");
        favoriteSearcherList.add("asdsdgsdfg");
        favoriteSearcherList.add("asffg");
        favoriteSearcherList.add("asfsdgwefg");
        favoriteSearcherList.add("asgregrffg");
        favoriteSearcherList.add("asffgdfgdfgfg");
        favoriteSearcherList.add("asdfgffg");
        favoriteSearcherList.add("asdfeeeegffg");
        favoriteSearcherList.add("asaaaddfgffg");


        recentRecyclerView = binding.recentRecyclerView;
        LinearLayoutManager recentoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recentRecyclerView.setLayoutManager(recentoutManager);
        recentAdapter = new RecentAdapter(recentList);
        recentRecyclerView.setAdapter(recentAdapter);

        List<RecentSearcherItem> recentSearcherItem = new ArrayList<>();

        for(int a = 0; a < 10; a++){
            RecentSearcherItem ei = new RecentSearcherItem();
            ei.setDate("" + a);
            ei.setName("" + a);
            recentSearcherItem.add(ei);
            Log.d(TAG, "" + a);
        }
        Log.d(TAG, "size: " + recentSearcherItem.size());
        recentAdapter.addItems(recentSearcherItem);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean isScrolledDown = false;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_SETTLING && isScrolledDown) {
                    Log.d(TAG, "1");
                    autoCompleteTextView.setVisibility(View.VISIBLE);

                }else if(newState == RecyclerView.SCROLL_STATE_SETTLING && !isScrolledDown){
                    Log.d(TAG, "2");
                    if (!piLIst.isEmpty()) {
                        autoCompleteTextView.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d(TAG, "dy: " + dy);
                isScrolledDown = dy < 0;
            }
        });



        return root;
    }


    public void getSearchProducts(String searchName){
//        Log.d(TAG, "page: " + CategoryProducts_page);

        loadingProgressManager.showLoading(getContext());

        retrofit2.Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);   // 레트로핏 인터페이스 객체 구현

        Call<List<ProductItem>> call = service.getSearchProductList("getSearchProducts", searchName, SearchProducts_page++);

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
        Log.d(TAG, "position: " + position);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getSearchProducts(searchName);
                isLoading = false;
            }
        }, 1000);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if (loadingProgressManager != null) {
            loadingProgressManager.hideLoading();
        }

        if (isScrollListenerAdded && recyclerView != null) {
            recyclerView.removeOnScrollListener(scrollListener);
            isScrollListenerAdded = false;
        }
    }
}