package com.sarin.prod.goodshost.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.item.ProductItem;

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
//                Intent intent = new Intent(v.getContext(), ServiceDetailActivity.class);
//                intent.putExtra("serviceId", items.get(position).getServiceId());
//                v.getContext().startActivity(intent);	//intent 에 명시된 액티비티로 이동

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
        private TextView name, price_value;
        private ImageView image;
        ProductItemClickListener productItemClickListener;

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            price_value = (TextView) itemView.findViewById(R.id.price_value);
            image = (ImageView) itemView.findViewById(R.id.imagew);

            itemView.setOnClickListener(this);
        }

        public void setItem(ProductItem pitem){
            name.setText(pitem.getName());
            price_value.setText(pitem.getPrice_value());
            String url = "https:" + pitem.getImage();
            Glide.with(context).load(url).into(image);
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


