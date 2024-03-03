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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
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

import com.sarin.prod.goodshost.view.PopupDialogUtil;
import com.sarin.prod.goodshost.view.PopupDialogClickListener;


import com.sarin.prod.goodshost.adapter.RecyclerViewClickListener;
import com.sarin.prod.goodshost.util.PreferenceManager;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
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

    private LinearLayout recent_layout, favorite_layout, searchNoItemMsg, editBoxLinearLayout, fragment_search_linearlayout;

    private ImageView searchIcon;
    private TextView search_favorite_searches_no_data;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SearchViewModel searchViewModel =
                new ViewModelProvider(this).get(SearchViewModel.class);

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        favorite_layout = binding.favoriteLayout;
        recent_layout = binding.recentLayout;
        searchNoItemMsg = binding.searchNoItemMsg;
        searchIcon = binding.searchIcon;
        editBoxLinearLayout = binding.editBoxLinearLayout;
        fragment_search_linearlayout = binding.fragmentSearchLinearlayout;
        search_favorite_searches_no_data = binding.searchFavoriteSearchesNoData;


        recyclerView = binding.recyclerView;
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
//        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 가로 2개 나열 할때.
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
//                fragment_search_linearlayout.setBackgroundColor( ContextCompat.getColor(getContext(), R.color.white_500));

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
//                    fragment_search_linearlayout.setBackgroundColor( ContextCompat.getColor(getContext(), R.color.white_500));
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
                

                if (s.length() >= 2) { // 예를 들어, 2글자 이상 입력된 경우에만 요청
                    searchName = s.toString();
                    getAutoCompleteText(s.toString());
                }
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
                        // drawableEnd 클릭 시 수행할 동작
                        // 예: 검색 기능 실행, 입력 내용 지우기 등
                        autoCompleteTextView.setText("");
                        favorite_layout.setVisibility(View.VISIBLE);
                        recent_layout.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        searchNoItemMsg.setVisibility(View.GONE);
//                        fragment_search_linearlayout.setBackgroundColor( ContextCompat.getColor(getContext(), R.color.white_500));
                        return true;
                    }
                }
                return false;
            }
        });

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        } else {
            search_favorite_searches_no_data.setVisibility(View.VISIBLE);
            recentRecyclerView.setVisibility(View.GONE);
        }

        recentAdapter.setOnItemClickListener(new RecentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                //save = bottomSheetDialog.findViewById(R.id.save);
                String name = recentAdapter.get(pos);
                pdItem_possion = pos;
                if (v.getId() == R.id.recentLayout) {
                    
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
                    
//                    autoCompleteTextView.setVisibility(View.VISIBLE);
//                    editBoxLinearLayout.setVisibility(View.VISIBLE);
                    VisibleSlideDown(editBoxLinearLayout);

                }else if(newState == RecyclerView.SCROLL_STATE_SETTLING && !isScrolledDown){
                    
                    if (!piLIst.isEmpty()) {
//                        autoCompleteTextView.setVisibility(View.GONE);
//                        editBoxLinearLayout.setVisibility(View.GONE);
                        GoneSlideUp(editBoxLinearLayout);
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



        return root;
    }

    @Override
    public void onItemClickListener(View v, int pos) {
        // 아이템 클릭 이벤트 처리
        pdItem = productAdapter.get(pos);
        pdItem_possion = pos;
        
        if (v.getId() == R.id.layout_favorite) {

            if(pdItem.isIs_Favorite()){
                setDelUserItemMap(MainApplication.ANDROID_ID, pdItem.getVendor_item_id());
                pdItem.setIs_Favorite(false);
                productAdapter.set(pos, pdItem);
            } else {
                setUserItemMap(MainApplication.ANDROID_ID, pdItem.getVendor_item_id(), "Y", 0, "N");
                pdItem.setIs_Favorite(true);
                productAdapter.set(pos, pdItem);
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
                    


                }
                else{
                }
            }
            @Override
            public void onFailure(Call<ReturnMsgItem> call, Throwable t) {
                PopupDialogUtil.showCustomDialog(getContext(), new PopupDialogClickListener() {
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
                PopupDialogUtil.showCustomDialog(getContext(), new PopupDialogClickListener() {
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
    public void onItemClickListener_Hori(View v, int pos) {}


    public void getSearchProducts(String searchName){


        loadingProgressManager.showLoading(getContext());


        retrofit2.Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);   // 레트로핏 인터페이스 객체 구현

        Call<List<ProductItem>> call = service.getSearchProducts("getSearchProducts", searchName, MainApplication.ANDROID_ID, SearchProducts_page++);

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
                        fragment_search_linearlayout.setBackgroundColor( ContextCompat.getColor(getContext(), R.color.white_500));
                    }

                }
                else{
                }
                favorite_layout.setVisibility(View.GONE);
                recent_layout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                search_favorite_searches_no_data.setVisibility(View.GONE);
                recentRecyclerView.setVisibility(View.VISIBLE);

                loadingProgressManager.hideLoading();

            }

            @Override
            public void onFailure(Call<List<ProductItem>> call, Throwable t) {
                loadingProgressManager.hideLoading();
                PopupDialogUtil.showCustomDialog(getContext(), new PopupDialogClickListener() {
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



    public void getAutoCompleteText(String search_text){

        retrofit2.Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);   // 레트로핏 인터페이스 객체 구현

        Call<List<String>> call = service.getAutoCompleteText("getAutoCompleteText", search_text);
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if(response.isSuccessful()){
//                    List<String> searchName = response.body();
//                    searchName = cleanList(searchName);

//                    adapter.clear();
//                    adapter.addAll(searchName);
//                    adapter.notifyDataSetChanged();

                    List<String> searchName = response.body();
                    searchName = cleanList(searchName);

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                            R.layout.my_auto_complete_box, searchName);
                    autoCompleteTextView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();


                }
                else{
                }
            }
            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                PopupDialogUtil.showCustomDialog(getContext(), new PopupDialogClickListener() {
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

    public static List<String> cleanList(List<String> originalList) {
        // 문자열 정제: null, 빈 문자열 제거 및 특수 문자 제거
        List<String> cleanedList = originalList.stream()
                .filter(str -> str != null && !str.isEmpty())
                .map(str -> str.replaceAll("[^a-zA-Z0-9\\s가-힣]", ""))
                .collect(Collectors.toList());

        // 문자열 빈도수 계산
        Map<String, Long> frequencyMap = cleanedList.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        // 빈도수에 따라 정렬 (빈도수가 같을 경우 원본 리스트의 순서 유지)
        List<String> sortedList = cleanedList.stream()
                .distinct() // 중복 제거
                .sorted((a, b) -> {
                    long freqA = frequencyMap.get(a);
                    long freqB = frequencyMap.get(b);
                    if (freqB != freqA) {
                        return Long.compare(freqB, freqA);
                    } else {
                        return Integer.compare(cleanedList.indexOf(a), cleanedList.indexOf(b));
                    }
                })
                .collect(Collectors.toList());

        return sortedList;
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


    public void GoneSlideUp(View view) {
        view.setVisibility(View.GONE);
        view.setTranslationY(view.getHeight());
        view.animate()
                .translationY(0)
                .setDuration(300)
                .setListener(null);
    }
    public void VisibleSlideDown(View view) {
        if(view.getVisibility() != View.VISIBLE){
            view.setVisibility(View.VISIBLE); // 뷰를 보이게 합니다.
            view.setTranslationY(-view.getHeight()); // 뷰를 화면 위로 위치시킵니다.
            // 애니메이션으로 뷰를 원래 위치로 이동시킵니다.
            view.animate()
                    .translationY(0)
                    .setDuration(300)
                    .setListener(null);
        }

    }



}