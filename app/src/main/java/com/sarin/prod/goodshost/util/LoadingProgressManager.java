package com.sarin.prod.goodshost.util;


import com.sarin.prod.goodshost.R;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;


public class LoadingProgressManager {
        private static LoadingProgressManager instance;
        private AlertDialog loadingDialog;
        private int requestCount = 0;

        private LoadingProgressManager() {}

        public static synchronized LoadingProgressManager getInstance() {
            if (instance == null) {
                instance = new LoadingProgressManager();
            }
            return instance;
        }

        public void showLoading(Context context) {
            synchronized (this) {
                requestCount++;
                if (loadingDialog == null || !loadingDialog.isShowing()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    LayoutInflater inflater = LayoutInflater.from(context);
                    builder.setView(inflater.inflate(R.layout.view_circle_progress, null));
                    builder.setCancelable(false);

                    loadingDialog = builder.create();
                    // AlertDialog 윈도우 설정 변경
                    if (loadingDialog.getWindow() != null) {
                        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    }
                    loadingDialog.show();
                }
            }
        }

        public void hideLoading() {
            synchronized (this) {
                requestCount--;
                if (requestCount <= 0 && loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                    loadingDialog = null;
                    requestCount = 0;
                }
            }
        }
    }