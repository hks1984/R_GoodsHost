package com.sarin.prod.goodshost.fragment.favorite;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sarin.prod.goodshost.view.PopupDialogUtil;
import com.sarin.prod.goodshost.view.PopupDialogClickListener;


import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.activity.ProductDetailActivity;
import com.sarin.prod.goodshost.adapter.FavoriteProductAdapter;
import com.sarin.prod.goodshost.adapter.RecyclerViewClickListener;
import com.sarin.prod.goodshost.databinding.FragmentFavoriteBinding;
import com.sarin.prod.goodshost.fragment.home.HomeFragment;
import com.sarin.prod.goodshost.item.ProductItem;
import com.sarin.prod.goodshost.item.ReturnMsgItem;
import com.sarin.prod.goodshost.network.RetrofitClientInstance;
import com.sarin.prod.goodshost.network.RetrofitInterface;
import com.sarin.prod.goodshost.util.CustomSnackbar;
import com.sarin.prod.goodshost.util.LoadingProgressManager;
import com.sarin.prod.goodshost.util.StringUtil;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteFragment extends Fragment implements RecyclerViewClickListener {

    private FragmentFavoriteBinding binding;
    public static String TAG = MainApplication.TAG;
    private static RecyclerView recyclerView;
    private boolean isScrollListenerAdded = false;
    private RecyclerView.OnScrollListener scrollListener;
    public static FavoriteProductAdapter favoriteProductAdapter;
    private List<ProductItem> piLIst = new ArrayList<>();
    private ProductItem pdItem = new ProductItem();
    private int pdItem_possion = 0;
    private LoadingProgressManager loadingProgressManager = LoadingProgressManager.getInstance();
    private StringUtil sUtil = StringUtil.getInstance();

    private LinearLayout favorite_LinearLayout1;
    private NestedScrollView nestedScrollView;

    private BottomSheetDialog bottomSheetDialog;
    private EditText hope_price;
    private TextView save, discount_1, discount_2, discount_3, favorite_all_product_nodata;
    private CheckBox check;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FavoriteViewModel favoriteViewModel =
                new ViewModelProvider(this).get(FavoriteViewModel.class);

        binding = FragmentFavoriteBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        favorite_LinearLayout1 = binding.favoriteLinearLayout1;
        nestedScrollView = binding.nestedScrollView;
        recyclerView = binding.recyclerView;
        favorite_all_product_nodata = binding.favoriteAllProductNodata;


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        favoriteProductAdapter = new FavoriteProductAdapter(piLIst, this);
        recyclerView.setAdapter(favoriteProductAdapter);

        getFavoriteProducts(MainApplication.ANDROID_ID);

        LayoutInflater inflater_bottom_sheet = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater_bottom_sheet.inflate(R.layout.bottom_sheet_registration, null, false);
        bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.RoundCornerBottomSheetDialogTheme);
        bottomSheetDialog.setContentView(view);

        hope_price = bottomSheetDialog.findViewById(R.id.hope_price);
        save = bottomSheetDialog.findViewById(R.id.save);
        check = bottomSheetDialog.findViewById(R.id.check);
        discount_1 = bottomSheetDialog.findViewById(R.id.discount_1);
        discount_2 = bottomSheetDialog.findViewById(R.id.discount_2);
        discount_3 = bottomSheetDialog.findViewById(R.id.discount_3);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheckBoxChecked = check.isChecked();
                String hope_stock = isCheckBoxChecked ? "Y" : "N";

                String editTextValue = hope_price.getText().toString();

                Log.d(TAG, "" + MainApplication.ANDROID_ID + "   " + pdItem.getVendor_item_id() + "   " + sUtil.convertStringToInt(editTextValue) + "   " + hope_stock);

                pdItem.setHope_price(sUtil.convertStringToInt(editTextValue));
                pdItem.setHope_stock(hope_stock);
                setUserItemMap(MainApplication.ANDROID_ID, pdItem.getVendor_item_id(), sUtil.convertStringToInt(editTextValue), hope_stock);
                CustomSnackbar.showSnackbar(getContext(), binding.getRoot(), getContext().getResources().getString(R.string.favorite_save));

                bottomSheetDialog.dismiss();
            }
        });

        discount_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ObjectAnimator animator = ObjectAnimator.ofFloat(v, "alpha", 1f, 0f, 1f);
                animator.setDuration(300); // 500ms 동안 실행
                animator.start();

                int current_price = pdItem.getPrice_value();
                hope_price.setText("" + sUtil.calculateDiscountedPrice(current_price, 10));



            }
        });
        discount_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(v, "alpha", 1f, 0f, 1f);
                animator.setDuration(300); // 500ms 동안 실행
                animator.start();

                int current_price = pdItem.getPrice_value();
                hope_price.setText("" + sUtil.calculateDiscountedPrice(current_price, 15));
            }
        });
        discount_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(v, "alpha", 1f, 0f, 1f);
                animator.setDuration(300); // 500ms 동안 실행
                animator.start();

                int current_price = pdItem.getPrice_value();
                hope_price.setText("" + sUtil.calculateDiscountedPrice(current_price, 20));
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


        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener()
        {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY)
            {
                if (v.getChildAt(v.getChildCount() - 1) != null)
                {
                    if (scrollY > oldScrollY && Math.abs(oldScrollY - scrollY) < 10)
                    {
                        // 스크롤 아래로
                        favorite_LinearLayout1.setVisibility(View.GONE);
                        if (scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight()))
                        {
                            // 스크롤 마지막 도착.
                        }
                    } else if (oldScrollY > scrollY && Math.abs(oldScrollY - scrollY) < 10){
                        // 스크롤 위로
                        favorite_LinearLayout1.setVisibility(View.VISIBLE);
                    }
                }
            }
        });


        return root;
    }

    public void onItemClickListener(View v, int pos) {
        // 아이템 클릭 이벤트 처리
        pdItem = favoriteProductAdapter.get(pos);
        pdItem_possion = pos;

        if (v.getId() == R.id.favorite_del) {
            setDelUserItemMap(MainApplication.ANDROID_ID, pdItem.getVendor_item_id());
            HomeFragment hf = HomeFragment.getInstance();
            hf.productAdapter.setFavorite(pdItem.getVendor_item_id(), false);
            hf.productHoriAdapter.setFavorite(pdItem.getVendor_item_id(), false);

        } else if (v.getId() == R.id.favorite_alarm_edit) {
            bottomSheetDialog.show();
            hope_price.setText("" + favoriteProductAdapter.get(pos).getHope_price());
            hope_price.setSelection(hope_price.getText().length());
            if("Y".equals(favoriteProductAdapter.get(pos).getHope_stock())){
                check.setChecked(true);
            } else {
                check.setChecked(false);
            }
        } else if (v.getId() == R.id.list_view_favorite) {
            Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
            intent.putExtra("vendor_item_id", pdItem.getVendor_item_id());
            v.getContext().startActivity(intent);	//intent 에 명시된 액티비티로 이동
        }
    }

    @Override
    public void onItemClickListener_Hori(View v, int pos) {}


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
                     favoriteProductAdapter.set(pdItem_possion, pdItem);

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
                    favoriteProductAdapter.remove(pdItem_possion);
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

    public void getFavoriteProducts(String user_id){
//        Log.d(TAG, "page: " + CategoryProducts_page);

        loadingProgressManager.showLoading(getContext());

        retrofit2.Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);   // 레트로핏 인터페이스 객체 구현

        Call<List<ProductItem>> call = service.getFavoriteProducts("getFavoriteProducts", user_id);

        call.enqueue(new Callback<List<ProductItem>>() {
            @Override
            public void onResponse(Call<List<ProductItem>> call, Response<List<ProductItem>> response) {
                if(response.isSuccessful()){
                    List<ProductItem> productItem = response.body();
                    favoriteProductAdapter.addItems(productItem);
                    favoriteProductAdapter.notifyDataSetChanged();

                    Log.d(TAG, "favoriteProductAdapter.size() : " + favoriteProductAdapter.size());
                    if(favoriteProductAdapter.size() < 1){
                        favorite_all_product_nodata.setVisibility(View.VISIBLE);
                    } else {
                        favorite_all_product_nodata.setVisibility(View.GONE);
                    }

                }
                else{
                }
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





    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}