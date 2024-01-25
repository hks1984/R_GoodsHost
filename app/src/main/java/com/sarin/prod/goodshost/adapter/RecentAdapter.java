package com.sarin.prod.goodshost.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.item.RecentSearcherItem;
import com.sarin.prod.goodshost.util.StringUtil;

import java.util.List;


public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.ViewHolder>{

    private List<String> items;
    public static String TAG = MainApplication.TAG;
    private static Context context;

    static StringUtil sUtil = StringUtil.getInstance();

    public RecentAdapter(List<String> items){
        this.items = items;
    }

    private OnItemClickListener onItemClickListener = null;
    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_list_view, parent, false);
        context = parent.getContext();
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = items.get(position);
        holder.setItem(item);

    }

    public void addItems(List<String> items){
        this.items.addAll(items);
        notifyDataSetChanged();
    }
    public void addItem(String items){
        this.items.remove(items);
        this.items.add(0, items);
        notifyDataSetChanged();
    }
    public void setItems(List<String> items){
        items = items;
        notifyDataSetChanged();
    }
    public void remove (int i){
        this.items.remove(i);
        notifyDataSetChanged();
    }


    public String get(int possion) {
        return items.get(possion);
    }

    public int size() {
        int i = items.size();
        return i;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView recentName;
        private ImageView remove;
        private LinearLayout recentLayout;

        CategoryItemClickListener categoryItemClickListener;

        public ViewHolder(View itemView) {
            super(itemView);

            recentLayout = (LinearLayout) itemView.findViewById(R.id.recentLayout);
            recentName = (TextView) itemView.findViewById(R.id.recentName);
            remove = (ImageView) itemView.findViewById(R.id.remove);

            recentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){ if(onItemClickListener != null){ onItemClickListener.onItemClick(v, pos); } }
                }
            });
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){ if(onItemClickListener != null){ onItemClickListener.onItemClick(v, pos); } }
                }
            });

        }
        public void setItem(String pitem){
            recentName.setText(pitem);
        }

    }


}


