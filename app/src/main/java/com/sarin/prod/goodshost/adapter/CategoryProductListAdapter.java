package com.sarin.prod.goodshost.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.activity.CategoryProductListActivity;
import com.sarin.prod.goodshost.fragment.home.HomeFragment;
import com.sarin.prod.goodshost.item.CategoryItem;
import com.sarin.prod.goodshost.network.RetrofitClientInstance;
import com.sarin.prod.goodshost.util.StringUtil;

import java.util.List;


public class CategoryProductListAdapter extends RecyclerView.Adapter<CategoryProductListAdapter.ViewHolder>{

    private List<CategoryItem> items;
    public static String TAG = MainApplication.TAG;
    private static Context context;
    private String isMode = "";

    static CategoryProductListActivity cpla = CategoryProductListActivity.getInstance();


    public CategoryProductListAdapter(List<CategoryItem> items){
        this.items = items;
    }

    public int selectedItem = -1;

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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list_view , parent, false);
        context = parent.getContext();
        return new ViewHolder(itemView);
    }

    public void setMode(String isMode) {
        this.isMode = isMode;
        notifyDataSetChanged();  // 데이터 변경을 알리기
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryItem item = items.get(position);
        holder.setItem(item);

        if(selectedItem == position) {
            holder.categoryName.setBackgroundResource(R.drawable.round_button_on);
            holder.categoryName.setTextColor(ContextCompat.getColor(context, R.color.white_500));

        } else {
            holder.categoryName.setBackgroundResource(R.drawable.round_button_off);
            holder.categoryName.setTextColor(ContextCompat.getColor(context, R.color.black_500));
        }

        holder.categoryItemClickListener = new CategoryItemClickListener() {
            @Override
            public void onItemClickListener(View v, int position) {
                Log.d(TAG, "category: " + items.get(position).api_code + "   " + items.get(position).name);
                selectedItem = position;
                notifyDataSetChanged();

                cpla.productAdapter.clear();
                cpla.productAdapter.notifyDataSetChanged();
                cpla.categoryCode = items.get(position).api_code;
                cpla.page = 0;
                if("best".equals(isMode)){
                    cpla.getBestSalesProducts(cpla.viewCount, items.get(position).api_code);
                }else{
                    cpla.getTopProducts(cpla.viewCount, items.get(position).api_code);
                }

            }
        };

    }

    public void addItems(List<CategoryItem> items){
        this.items.addAll(items);
        notifyDataSetChanged();
    }
    public void addItem(CategoryItem items){
        this.items.add(items);
        notifyDataSetChanged();
    }
    public void setItems(List<CategoryItem> items){
        items = items;
        notifyDataSetChanged();
    }

    public int size() {
        int i = items.size();
        return i;
    }

    public void clear(){
        items.clear();
//        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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


