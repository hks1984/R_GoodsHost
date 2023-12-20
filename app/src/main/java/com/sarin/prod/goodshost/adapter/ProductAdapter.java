package com.sarin.prod.goodshost.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.item.ProductItem;
import com.sarin.prod.goodshost.activity.ProductDetailActivity;

import java.util.List;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder>{

    private List<ProductItem> items;
    public static String TAG = MainApplication.TAG;
    private static Context context;

    public ProductAdapter(List<ProductItem> items){
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_view , parent, false);
        context = parent.getContext();
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductItem item = items.get(position);
        holder.setItem(item);

        holder.productItemClickListener = new ProductItemClickListener() {
            @Override
            public void onItemClickListener(View v, int position) {

                Log.d(TAG, "position: " + position);
                Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
                intent.putExtra("vendor_item_id", items.get(position).getVendor_item_id());
                v.getContext().startActivity(intent);	//intent 에 명시된 액티비티로 이동

            }
        };

    }

    public void addItems(List<ProductItem> items){
        this.items.addAll( items);
//        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView name, price_value, rating_total_count, persent;
        private ImageView image;
        private RatingBar rating;

        ProductItemClickListener productItemClickListener;

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            price_value = (TextView) itemView.findViewById(R.id.price_value);
            image = (ImageView) itemView.findViewById(R.id.imagew);
            rating = (RatingBar) itemView.findViewById(R.id.rating);
            rating_total_count = (TextView) itemView.findViewById(R.id.rating_total_count);
            persent = (TextView) itemView.findViewById(R.id.persent);

            itemView.setOnClickListener(this);
        }

        public void setItem(ProductItem pitem){
            name.setText(pitem.getName());
            price_value.setText(pitem.getPrice_value());
            String url = "https:" + pitem.getImage();
            Glide.with(context).load(url).into(image);
            rating.setRating(Float.parseFloat(pitem.getRating()));
            String ratingTotalCount = pitem.getRating_total_count();
            if (ratingTotalCount != null && !ratingTotalCount.isEmpty()) {
                rating_total_count.setText("(" + ratingTotalCount + ")");
            } else {
                rating_total_count.setText("");
            }
            Log.d(TAG, "pitem.getPersent(): " + pitem.getPersent());

            int numberInt = 0;
            try {
                // 먼저 문자열을 float 또는 double로 변환합니다.
                float numberFloat = Float.parseFloat(pitem.getPersent());

                // 변환된 float 또는 double을 int로 변환합니다.
                numberInt = (int) numberFloat;

                // 결과 출력
//                System.out.println("변환된 정수: " + numberInt);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                // 예외 처리 로직
            }

            if (numberInt < 0) {
                persent.setVisibility(View.VISIBLE); // 레이아웃 표시
                persent.setText(Math.abs(numberInt)) + "%"); // 할인율 표시
            } else {
                persent.setVisibility(View.GONE); // 레이아웃 숨김
            }

        }

        @Override
        public void onClick(View v){
            this.productItemClickListener.onItemClickListener(v,getLayoutPosition());
        }

    }

    public void setItems(List<ProductItem> list){
        items = list;
//        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

//    @Override
//    public int getItemViewType(int position) {
//        if (position == 0 || mList.get(position) instanceof Country) {
//            return ITEM_TYPE_SERVICE;
//        } else {
//            return (position % MainActivity.ITEMS_PER_AD == 0) ? ITEM_TYPE_BANNER_AD : ITEM_TYPE_COUNTRY;
//        }
//    }



}


