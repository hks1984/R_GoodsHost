package com.sarin.prod.goodshost.adapter;

import android.view.View;

public interface SearchItemClickListener {
    void onItemClickListener(View v, int position);
    void onItemClickListener_recent(View v, int position);
    void onItemClickListener_favorite(View v, int position);

}
