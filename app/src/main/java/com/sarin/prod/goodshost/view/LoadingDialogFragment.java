package com.sarin.prod.goodshost.view;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import com.sarin.prod.goodshost.databinding.ViewCircleProgressBinding;

public class CircleProgressDialog extends DialogFragment {

    private ViewCircleProgressBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 뷰 바인딩을 사용하여 레이아웃 inflate
        binding = ViewCircleProgressBinding.inflate(inflater, container, false);

        // 대화상자의 배경을 투명하게 설정
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // 뷰 바인딩 참조 정리
    }
}
