package com.sarin.prod.goodshost.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.item.DefaultAlarmItem;
import com.sarin.prod.goodshost.item.ProductAlarmItem;
import com.sarin.prod.goodshost.util.StringUtil;

import java.util.List;


public class DefaultAlarmAdapter extends RecyclerView.Adapter<DefaultAlarmAdapter.ViewHolder>{

    private List<DefaultAlarmItem> items;
    public static String TAG = MainApplication.TAG;
    private static Context context;
    static StringUtil sUtil = StringUtil.getInstance();
    private RecyclerViewClickListener recyclerViewClickListener;
    public DefaultAlarmAdapter(List<DefaultAlarmItem> items)
    {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_default_alarm_list , parent, false);
        context = parent.getContext();
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DefaultAlarmItem item = items.get(position);
        holder.setItem(item);

    }

    public void addItems(List<DefaultAlarmItem> items){
        this.items.addAll( items);
        notifyDataSetChanged();
    }

    public DefaultAlarmItem get(int possion) {
        return items.get(possion);
    }
    public void setItems(List<DefaultAlarmItem> list){
        items = list;
        notifyDataSetChanged();
    }

    public void set (int pos, DefaultAlarmItem item) {
        items.set(pos, item);
        notifyDataSetChanged();
    }

    public void clear(){
        items.clear();
//        notifyDataSetChanged();
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
        private TextView name, body, c_date;
        private ConstraintLayout full_layout;

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            body = (TextView) itemView.findViewById(R.id.body);
            c_date = (TextView) itemView.findViewById(R.id.c_date);
            full_layout = (ConstraintLayout) itemView.findViewById(R.id.full_layout);

        }

        public void setItem(DefaultAlarmItem pitem){

            name.setText(pitem.getDefault_title());
            body.setText(pitem.getDefault_body());
            String timeAgo = sUtil.getTimeAgo(Long.parseLong(pitem.getC_date()));
            c_date.setText(timeAgo);

        }


    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }



}


