package com.sarin.prod.goodshost.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.item.ProductAlarmItem;
import com.sarin.prod.goodshost.util.StringUtil;
import com.sarin.prod.goodshost.item.EventItem;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>{

    private List<EventItem> items;
    public static String TAG = MainApplication.TAG;
    private static Context context;
    static StringUtil sUtil = StringUtil.getInstance();
    private EventRecyclerViewClickListener recyclerViewClickListener;
    private final RequestManager glide;

    public EventAdapter(RequestManager glide, List<EventItem> items, EventRecyclerViewClickListener listener)
    {
        this.glide = glide;
        this.items = items;
        this.recyclerViewClickListener = listener;
    }

    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_event_list , parent, false);
        context = parent.getContext();
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.ViewHolder holder, int position) {
        EventItem item = items.get(position);
        holder.setItem(item);

        // 전체 아이템 클릭 이벤트
        // activity 또는 fragment 화면단 코드에서 onItemClickListener 메소드 추가해줘야함.
        holder.full_layout.setOnClickListener(v -> recyclerViewClickListener.onItemClickListener(v, position));
//        holder.layout_favorite.setOnClickListener(v -> recyclerViewClickListener.onItemClickListener(v, position));

    }

    public void addItems(List<EventItem> items){
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public EventItem get(int possion) {
        return items.get(possion);
    }
    public void setItems(List<EventItem> list){
        items = list;
        notifyDataSetChanged();
    }

    public void set (int pos, EventItem item) {
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
        private TextView title;
        private ImageView image;
        private CardView full_layout;

        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            image = (ImageView) itemView.findViewById(R.id.image);
            full_layout = (CardView) itemView.findViewById(R.id.full_layout);

        }

        public void setItem(EventItem pitem){

            title.setText(pitem.getTitle());
            glide.load(pitem.getImage())
                    .into(image);

        }

    }


}
