package com.sarin.prod.goodshost.fragment.event;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.activity.ProductDetailActivity;
import com.sarin.prod.goodshost.adapter.ProductAdapter;
import com.sarin.prod.goodshost.adapter.ProductAlarmAdapter;
import com.sarin.prod.goodshost.adapter.EventRecyclerViewClickListener;
import com.sarin.prod.goodshost.databinding.FragmentEventBinding;
import com.sarin.prod.goodshost.databinding.FragmentFavoriteBinding;
import com.sarin.prod.goodshost.item.EventItem;
import com.sarin.prod.goodshost.item.ProductAlarmItem;
import com.sarin.prod.goodshost.item.ProductItem;
import com.sarin.prod.goodshost.network.RetrofitClientInstance;
import com.sarin.prod.goodshost.network.RetrofitInterface;
import com.sarin.prod.goodshost.view.PopupDialogClickListener;
import com.sarin.prod.goodshost.view.PopupDialogUtil;
import com.sarin.prod.goodshost.adapter.EventAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventFragment extends Fragment implements EventRecyclerViewClickListener {

    private EventViewModel mViewModel;
    private FragmentEventBinding binding;
    public static String TAG = MainApplication.TAG;

    public static EventAdapter eventAdapter;
    private static RecyclerView recyclerView;
    private List<EventItem> eventItems = new ArrayList<>();
    private EventItem eventItem = new EventItem();


    public static EventFragment newInstance() {
        return new EventFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 가로 2개 나열 할때.
        eventAdapter = new EventAdapter(Glide.with(getActivity()),eventItems, this);
        recyclerView.setAdapter(eventAdapter);

        if(eventAdapter.size() <= 0){
            getEvent("");
        }


        return root;

    }

    @Override
    public void onItemClickListener(View v, int pos) {
        // 아이템 클릭 이벤트 처리
        eventItem = eventAdapter.get(pos);
//        pdItem_possion = pos;
        if (v.getId() == R.id.full_layout) {
            Toast.makeText(getContext(), "" + pos, Toast.LENGTH_SHORT).show();
        }
    }

    public void getEvent(String type){

        retrofit2.Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        RetrofitInterface service = retrofit.create(RetrofitInterface.class);   // 레트로핏 인터페이스 객체 구현

        Call<List<EventItem>> call = service.getEvent("getEvent", type);

        call.enqueue(new Callback<List<EventItem>>() {
            @Override
            public void onResponse(Call<List<EventItem>> call, Response<List<EventItem>> response) {
                if(response.isSuccessful()){
                    List<EventItem> eventItems = response.body();
                    Log.d(MainApplication.TAG, eventItems.toString());

                    if(eventItems.size() < 1){
                        // TODO 데이터가 없을 경우
                    } else {
                        eventAdapter.addItems(eventItems);
                    }

                }
                else{

                }

            }

            @Override
            public void onFailure(Call<List<EventItem>> call, Throwable t) {
//                loadingProgressManager.hideLoading();
                PopupDialogUtil.showCustomDialog(getActivity(), new PopupDialogClickListener() {
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

}