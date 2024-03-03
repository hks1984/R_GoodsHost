package com.sarin.prod.goodshost.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.item.ProductAlarmItem;
import com.sarin.prod.goodshost.item.ProductItem;
import com.sarin.prod.goodshost.util.StringUtil;

import java.util.List;


public class ProductAlarmAdapter extends RecyclerView.Adapter<ProductAlarmAdapter.ViewHolder>{

    private List<ProductAlarmItem> items;
    public static String TAG = MainApplication.TAG;
    private static Context context;
    static StringUtil sUtil = StringUtil.getInstance();
    private ProductAlarmAdapter.OnItemClickListener onItemClickListener = null;
    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }
    public void setOnItemClickListener(ProductAlarmAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public ProductAlarmAdapter(List<ProductAlarmItem> items)
    {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_product_alarm_list , parent, false);
        context = parent.getContext();
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductAlarmItem item = items.get(position);
        holder.setItem(item);

        // 전체 아이템 클릭 이벤트
        // activity 또는 fragment 화면단 코드에서 onItemClickListener 메소드 추가해줘야함.
//        holder.list_view_hori.setOnClickListener(v -> recyclerViewClickListener.onItemClickListener(v, position));
//        holder.layout_favorite.setOnClickListener(v -> recyclerViewClickListener.onItemClickListener(v, position));

    }

    public void addItems(List<ProductAlarmItem> items){
        this.items.addAll( items);
        notifyDataSetChanged();
    }

    public ProductAlarmItem get(int possion) {
        return items.get(possion);
    }
    public void setItems(List<ProductAlarmItem> list){
        items = list;
        notifyDataSetChanged();
    }

    public void set (int pos, ProductAlarmItem item) {
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
        private ImageView imagew;
        private ConstraintLayout full_layout;

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            body = (TextView) itemView.findViewById(R.id.body);
            c_date = (TextView) itemView.findViewById(R.id.c_date);
            imagew = (ImageView) itemView.findViewById(R.id.imagew);
            full_layout = (ConstraintLayout) itemView.findViewById(R.id.full_layout);

            full_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){ if(onItemClickListener != null){ onItemClickListener.onItemClick(v, pos); } }
                }
            });


        }

        public void setItem(ProductAlarmItem pitem){

            name.setText(pitem.getProduct_title());
            body.setText(pitem.getProduct_name());
            String url = pitem.getProduct_image();
            if(!sUtil.nullCheck(url)){
                if(!url.contains("https:")){
                    url = "https:" + url;
                }
            }
            Glide.with(itemView.getContext())
                    .load(url)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(20))) // 여기서 10은 코너의 반지름을 dp 단위로 지정
                    .into(imagew);

            String timeAgo = sUtil.getTimeAgo(Long.parseLong(pitem.getC_date()));
            c_date.setText(timeAgo);

        }


    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }


}


