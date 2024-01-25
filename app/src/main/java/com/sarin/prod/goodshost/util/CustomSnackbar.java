package com.sarin.prod.goodshost.util;

import com.sarin.prod.goodshost.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class CustomSnackbar {

    public static void showSnackbar(Context ct, View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();

        // Snackbar 텍스트뷰 설정
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(ct, R.color.white_500));
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        // 텍스트뷰에 패딩 적용
        int padding = 10; // 원하는 패딩 값 설정
        textView.setPadding(padding, padding, padding, padding);

        // Snackbar 백그라운드 색상 및 모양 설정
        float radius = 50f; // 원하는 모서리 둥글기 정도 설정
        ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(new float[] {radius, radius, radius, radius, radius, radius, radius, radius}, null, null));
        shapeDrawable.getPaint().setColor(ContextCompat.getColor(ct, R.color.black_200)); // ShapeDrawable에 배경색 설정
        ViewCompat.setBackground(snackbarView, shapeDrawable);

        // Snackbar 위치 및 크기 설정
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
        params.width = FrameLayout.LayoutParams.WRAP_CONTENT; // 가로 길이를 내용에 맞춤
        params.gravity = Gravity.CENTER; // 중앙에 위치
        snackbarView.setLayoutParams(params);

        // 사용자 정의 시간으로 설정 (예: 500 밀리초 = 0.5초)
        snackbar.setDuration(500);
        snackbar.show();
    }

}

