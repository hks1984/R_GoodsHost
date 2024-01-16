package com.sarin.prod.goodshost.network;

import android.content.Context;
import android.util.Log;

import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.item.UserItem;
import com.sarin.prod.goodshost.item.ReturnMsgItem;
import com.sarin.prod.goodshost.util.PreferenceManager;
import com.sarin.prod.goodshost.util.StringUtil;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitApi {

    public static String TAG = MainApplication.TAG;

    private static RetrofitApi mInstnace = null;

    static Context context;

    private RetrofitApi(Context context) {
        this.context = context;
    }

    public static RetrofitApi getInstance(Context context) {
        if (mInstnace == null) {
            mInstnace = new RetrofitApi(context);
        }
        return mInstnace;
    }






}
