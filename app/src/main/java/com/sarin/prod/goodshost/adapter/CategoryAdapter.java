package com.sarin.prod.goodshost.adapter;

import static com.sarin.prod.goodshost.fragment.home.HomeFragment.productAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.activity.CategoryProductListActivity;
import com.sarin.prod.goodshost.activity.ProductDetailActivity;
import com.sarin.prod.goodshost.fragment.home.HomeFragment;
import com.sarin.prod.goodshost.item.CategoryItem;
import com.sarin.prod.goodshost.item.ProductItem;
import com.sarin.prod.goodshost.util.StringUtil;

import java.util.List;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{

    private List<CategoryItem> items;
    public static String TAG = MainApplication.TAG;
    private static Context context;

    static StringUtil sUtil = StringUtil.getInstance();
    static HomeFragment hf = HomeFragment.getInstance();

    public CategoryAdapter(List<CategoryItem> items){
        this.items = items;
    }

    public int selectedItem = -1;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list_view , parent, false);
        context = parent.getContext();
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryItem item = items.get(position);
        holder.setItem(item);


        if(selectedItem == position) {
            Log.d(TAG, "black");
            holder.categoryName.setBackgroundResource(R.drawable.round_button_on);
            holder.categoryName.setTextColor(ContextCompat.getColor(context, R.color.white_500));

        } else {
            Log.d(TAG, "white");
            holder.categoryName.setBackgroundResource(R.drawable.round_button_off);
            holder.categoryName.setTextColor(ContextCompat.getColor(context, R.color.black_500));
        }

        holder.categoryItemClickListener = new CategoryItemClickListener() {
            @Override
            public void onItemClickListener(View v, int position) {
                Log.d(TAG, "position : " + position);
                selectedItem = position;
                notifyDataSetChanged();

                hf.productAdapter.clear();
                hf.getTopProducts(10, 0, items.get(position).api_code);
//                Log.d(TAG, "category: " + items.get(position).api_code + "   " + items.get(position).name);
//
//                hf.productAdapter.notifyDataSetChanged();



//                Intent intent = new Intent(v.getContext(), CategoryProductListActivity.class);
//                intent.putExtra("api_code", items.get(position).api_code);
//                intent.putExtra("name", items.get(position).name);
//                v.getContext().startActivity(intent);	//intent 에 명시된 액티비티로 이동






            }
        };

    }

    public void addItems(List<CategoryItem> items){
        this.items.addAll(items);
//        notifyDataSetChanged();
    }
    public void addItem(CategoryItem items){
        this.items.add(items);
//        notifyDataSetChanged();
    }
    public void setItems(List<CategoryItem> list){
        items = list;
//        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView categoryName;

        CategoryItemClickListener categoryItemClickListener;

        public ViewHolder(View itemView) {
            super(itemView);

            categoryName = (TextView) itemView.findViewById(R.id.categoryName);

            itemView.setOnClickListener(this);
        }

        public void setItem(CategoryItem pitem){
            categoryName.setText(pitem.getName());

        }

        @Override
        public void onClick(View v){
            this.categoryItemClickListener.onItemClickListener(v,getLayoutPosition());




        }

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


