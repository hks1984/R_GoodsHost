package com.sarin.prod.goodshost.fragment.favorite;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.adapter.FavoriteProductAdapter;
import com.sarin.prod.goodshost.databinding.FragmentFavoriteBinding;
import com.sarin.prod.goodshost.item.ProductItem;
import com.sarin.prod.goodshost.item.ReturnMsgItem;
import com.sarin.prod.goodshost.network.RetrofitClientInstance;
import com.sarin.prod.goodshost.network.RetrofitInterface;
import com.sarin.prod.goodshost.util.LoadingProgressManager;
import com.sarin.prod.goodshost.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteFragment extends Fragment {

    private FragmentFavoriteBinding binding;
    public static String TAG = MainApplication.TAG;
    private static RecyclerView recyclerView;
    private boolean isScrollListenerAdded = false;
    public static FavoriteProductAdapter favoriteProductAdapter;
    private List<ProductItem> piLIst = new ArrayList<>();
    private ProductItem pdItem = new ProductItem();
    private int pdItem_possion = 0;
    private LoadingProgressManager loadingProgressManager = LoadingProgressManager.getInstance();
    private StringUtil sUtil = StringUtil.getInstance();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FavoriteViewModel favoriteViewModel =
                new ViewModelProvider(this).get(FavoriteViewModel.class);

        binding = FragmentFavoriteBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.recyclerView;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        favoriteProductAdapter = new FavoriteProductAdapter(piLIst);
        recyclerView.setAdapter(favoriteProductAdapter);

        getFavoriteProducts(MainApplication.ANDROID_ID);


        LayoutInflater inflater_bottom_sheet = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater_bottom_sheet.inflate(R.layout.bottom_sheet_registration, null, false);
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.RoundCornerBottomSheetDialogTheme);
        bottomSheetDialog.setContentView(view);

        TextView hope_price = bottomSheetDialog.findViewById(R.id.hope_price);
        TextView save = bottomSheetDialog.findViewById(R.id.save);
        CheckBox check = bottomSheetDialog.findViewById(R.id.check);

        favoriteProductAdapter.setOnItemClickListener(new FavoriteProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                //save = bottomSheetDialog.findViewById(R.id.save);
                pdItem = favoriteProductAdapter.get(pos);
                pdItem_possion = pos;
                if (v.getId() == R.id.favorite_del) {
                    favoriteProductAdapter.remove(pos);
                } else if (v.getId() == R.id.favorite_alarm_edit) {
                    bottomSheetDialog.show();
                    hope_price.setText("" + favoriteProductAdapter.get(pos).getHope_price());
                    if("Y".equals(favoriteProductAdapter.get(pos).getHope_stock())){
                        check.setChecked(true);
                    } else {
                        check.setChecked(false);
                    }
                }

            }
        });

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

                bottomSheetDialog.dismiss();
            }
        });


        return root;
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

                    // 재입고 변경 시 화면 갱신 안됌.
                     favoriteProductAdapter.set(pdItem_possion, pdItem);
//                    favoriteProductAdapter.notifyItemChanged(pdItem_possion);

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
                    piLIst.addAll(productItem);
                    favoriteProductAdapter.notifyDataSetChanged();
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}