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

    public static void showSnackbar(Context ct, View view, String message, int gravity) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);

        // Snackbar 배경색 설정



        View snackbarView = snackbar.getView();
//        snackbarView.setBackgroundColor(ContextCompat.getColor(ct, R.color.black_200));

        float radius = 20f; // 원하는 모서리 둥글기 정도 설정
        ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(new float[] { radius, radius, radius, radius, radius, radius, radius, radius }, null, null));
        ViewCompat.setBackground(snackbarView, shapeDrawable);

        int padding = 8; // 원하는 패딩 값 설정
        snackbarView.setPadding(padding, padding, padding, padding);

        // Snackbar 위치 설정
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
        params.gravity = gravity;
        snackbarView.setLayoutParams(params);

        FrameLayout.LayoutParams layout = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
        // 가로 길이 설정
        layout.width = 350; // 픽셀 단위로 가로 길이 설정
//        layout.height = 300;
        // 레이아웃 매개변수를 Snackbar View에 다시 설정
        snackbarView.setLayoutParams(layout);

        // Snackbar 텍스트 색상 설정
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(ct, R.color.white_500));
        // 텍스트뷰의 중앙 정렬 설정
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//        textView.setGravity(Gravity.CENTER_VERTICAL);  // 세로 방향으로 텍스트 중앙 정렬
//        textView.setGravity(Gravity.CENTER);

        // 사용자 정의 시간으로 설정 (예: 3000 밀리초 = 3초)
        snackbar.setDuration(1000);

        snackbar.show();
    }
}

