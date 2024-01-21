package com.sarin.prod.goodshost.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.util.StringUtil;

import java.util.List;


public class FavoriteSearcherAdapter extends RecyclerView.Adapter<FavoriteSearcherAdapter.ViewHolder>{

    private List<String> items;
    public static String TAG = MainApplication.TAG;
    private static Context context;

    static StringUtil sUtil = StringUtil.getInstance();

    public FavoriteSearcherAdapter(List<String> items){
        this.items = items;
    }

    private FavoriteSearcherAdapter.OnItemClickListener onItemClickListener = null;
    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }
    public void setOnItemClickListener(FavoriteSearcherAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_searcher_list_view, parent, false);
        context = parent.getContext();
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = items.get(position);
        holder.setItem(item);

        holder.categoryItemClickListener = new CategoryItemClickListener() {
            @Override
            public void onItemClickListener(View v, int position) {
                Log.d(TAG, "FavoriteSearcherAdapter: " + items.get(position));

            }
        };

    }

    public void addItems(List<String> items){
        this.items.addAll(items);
        notifyDataSetChanged();
    }
    public void addItem(String items){
        this.items.add(items);
        notifyDataSetChanged();
    }
    public void setItems(List<String> items){
        items = items;
        notifyDataSetChanged();
    }

    public int size() {
        int i = items.size();
        return i;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView recentName;

        CategoryItemClickListener categoryItemClickListener;

        public ViewHolder(View itemView) {
            super(itemView);

            recentName = (TextView) itemView.findViewById(R.id.recentName);

            itemView.setOnClickListener(this);
        }

        public void setItem(String pitem){
            recentName.setText(pitem);
        }

        @Override
        public void onClick(View v){
            this.categoryItemClickListener.onItemClickListener(v,getLayoutPosition());
        }

    }


}


