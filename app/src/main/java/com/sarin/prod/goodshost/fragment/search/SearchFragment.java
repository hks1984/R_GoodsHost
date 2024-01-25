package com.sarin.prod.goodshost.fragment.search;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;

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
import com.sarin.prod.goodshost.adapter.RecyclerViewClickListener;
import com.sarin.prod.goodshost.adapter.SearchItemClickListener;
import com.sarin.prod.goodshost.databinding.FragmentSearchBinding;
import com.sarin.prod.goodshost.item.ProductItem;
import com.sarin.prod.goodshost.item.RecentSearcherItem;
import com.sarin.prod.goodshost.item.ReturnMsgItem;
import com.sarin.prod.goodshost.network.RetrofitClientInstance;
import com.sarin.prod.goodshost.network.RetrofitInterface;
import com.sarin.prod.goodshost.util.LoadingProgressManager;
import com.sarin.prod.goodshost.activity.ProductDetailActivity;

import com.sarin.prod.goodshost.adapter.RecyclerViewClickListener;
import com.sarin.prod.goodshost.util.PreferenceManager;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment implements RecyclerViewClickListener {

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
    private List<String> recentList = new ArrayList<>();

    private LoadingProgressManager loadingProgressManager = LoadingProgressManager.getInstance();
    private ProductItem pdItem = new ProductItem();
    private int pdItem_possion = 0;
    private ArrayAdapter<String> adapter;

    private LinearLayout recent_layout, favorite_layout, searchNoItemMsg;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SearchViewModel searchViewModel =
                new ViewModelProvider(this).get(SearchViewModel.class);

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        favorite_layout = binding.favoriteLayout;
        recent_layout = binding.recentLayout;
        searchNoItemMsg = binding.searchNoItemMsg;


        recyclerView = binding.recyclerView;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        productAdapter = new ProductAdapter(piLIst, this);
        recyclerView.setAdapter(productAdapter);

        List<String> androids = new ArrayList<>();

        autoCompleteTextView = binding.editTextSearchBox;

        adapter = new ArrayAdapter<String>(getContext(), R.layout.my_auto_complete_box, androids);

        autoCompleteTextView.setAdapter(adapter);


        autoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AutoCompleteTextView가 클릭되었을 때의 처리
                favorite_layout.setVisibility(View.VISIBLE);
                recent_layout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                searchNoItemMsg.setVisibility(View.GONE);
            }
        });

        autoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // AutoCompleteTextView가 포커스를 얻었을 때의 처리
                    favorite_layout.setVisibility(View.VISIBLE);
                    recent_layout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    searchNoItemMsg.setVisibility(View.GONE);
                } else {
                    // AutoCompleteTextView가 포커스를 잃었을 때의 처리
                }
            }
        });

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                searchName = s.toString();
                Log.d(TAG, "searchName: " + searchName);

//                if (s.length() >= 3) { // 예를 들어, 2글자 이상 입력된 경우에만 요청
//                    searchName = s.toString();
////                    getAutoCompleteText(s.toString());
//                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

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
                    productAdapter.clear();
                    SearchProducts_page = 0;
                    getSearchProducts(searchName);
                    PreferenceManager.setStringList(getContext(), "searchList", searchName);
                    recentAdapter.addItem(searchName);
                    recentAdapter.notifyDataSetChanged();
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
                        productAdapter.clear();
                        SearchProducts_page = 0;
                        getSearchProducts(searchName);
                        PreferenceManager.setStringList(getContext(), "searchList", searchName);
                        recentAdapter.addItem(searchName);
                        recentAdapter.notifyDataSetChanged();
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



        recentRecyclerView = binding.recentRecyclerView;
        LinearLayoutManager recentoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recentRecyclerView.setLayoutManager(recentoutManager);
        recentAdapter = new RecentAdapter(recentList);
        recentRecyclerView.setAdapter(recentAdapter);

        List<String> searchList = PreferenceManager.getStringList(getContext(), "searchList");
        if(searchList != null && searchList.size() > 0){
            recentAdapter.addItems(searchList);
        }

        recentAdapter.setOnItemClickListener(new RecentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                //save = bottomSheetDialog.findViewById(R.id.save);
                String name = recentAdapter.get(pos);
                pdItem_possion = pos;
                if (v.getId() == R.id.recentLayout) {
                    Log.d(TAG, "recentName: " + name);
                    autoCompleteTextView.setText(name);

                    productAdapter.clear();
                    SearchProducts_page = 0;
                    getSearchProducts(searchName);
                    PreferenceManager.setStringList(getContext(), "searchList", searchName);
                    recentAdapter.addItem(searchName);
                    recentAdapter.notifyDataSetChanged();

                    // 키패드 없애기.
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);


                } else if (v.getId() == R.id.remove) {
                    Log.d(TAG, "remove: " + pos);
                    PreferenceManager.delStringList(getContext(), "searchList", name);
                    recentAdapter.remove(pos);

                }

            }
        });



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
                // 처음 리스트가 생성되었을때 스크롤을 위로 올릴때는 뷰가 숨겨지면 안된다.
                if(dy == 0){
                    isScrolledDown = true;
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
//            if(pdItem.isIs_Favorite()){
//                setDelUserItemMap(MainApplication.ANDROID_ID, pdItem.getVendor_item_id());
//                pdItem.setIs_Favorite(false);
//                productAdapter.set(pos, pdItem);
//            } else {
//                setUserItemMap(MainApplication.ANDROID_ID, pdItem.getVendor_item_id(), 0, "Y");
//                pdItem.setIs_Favorite(true);
//                productAdapter.set(pos, pdItem);
//            }

        } else {

            Log.d(TAG, "position: " + pos);
            Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
            intent.putExtra("vendor_item_id", pdItem.getVendor_item_id());
            v.getContext().startActivity(intent);	//intent 에 명시된 액티비티로 이동


        }
    }

    @Override
    public void onItemClickListener_Hori(View v, int pos) {}


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
                    if(productItem.size() <= 0){
                        searchNoItemMsg.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }else{
                        productAdapter.addItems(productItem);
                        productAdapter.notifyDataSetChanged();
                    }

                }
                else{
                    // 실패
                    Log.e(TAG, "실패 코드 확인 : " + response.code());
                    Log.e(TAG, "연결 주소 확인 : " + response.raw().request().url().url());
                }
                favorite_layout.setVisibility(View.GONE);
                recent_layout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

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



    public void getAutoCompleteText(String search_text){

        retrofit2.Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);   // 레트로핏 인터페이스 객체 구현

        Call<List<String>> call = service.getAutoCompleteText("getAutoCompleteText", search_text);
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if(response.isSuccessful()){
                    List<String> searchName = response.body();
                    searchName = cleanList(searchName);
                    Log.d(TAG, "searchName : " + searchName.toString());
                    adapter.clear();
                    adapter.addAll(searchName);
                    adapter.notifyDataSetChanged();
                }
                else{
                    Log.e(TAG, "실패 코드 확인 : " + response.code());
                    Log.e(TAG, "연결 주소 확인 : " + response.raw().request().url().url());
                }
            }
            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });


    }

    public static List<String> cleanList(List<String> originalList) {
        Set<String> set = new LinkedHashSet<>(); // 중복 제거 및 순서 유지

        return originalList.stream()
                .filter(str -> str != null && !str.isEmpty()) // null과 빈 문자열 제거
                .filter(set::add) // 중복 제거
                .collect(Collectors.toList()); // 결과를 List로 반환
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