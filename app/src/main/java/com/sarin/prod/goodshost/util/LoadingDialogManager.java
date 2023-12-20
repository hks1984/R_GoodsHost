package com.sarin.prod.goodshost.util;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.sarin.prod.goodshost.view.LoadingDialogFragment;

public class LoadingDialogManager {
    private static final String LOADING_DIALOG_TAG = "LOADING_DIALOG";
    private DialogFragment loadingDialog;

    public LoadingDialogManager() {
        loadingDialog = new LoadingDialogFragment();
    }

    public void showLoading(FragmentManager fragmentManager) {
        if (!loadingDialog.isAdded() && fragmentManager != null) {
            loadingDialog.show(fragmentManager, LOADING_DIALOG_TAG);
        }
    }

    public void hideLoading() {
        if (loadingDialog != null && loadingDialog.isAdded()) {
            loadingDialog.dismiss();
        }
    }
}