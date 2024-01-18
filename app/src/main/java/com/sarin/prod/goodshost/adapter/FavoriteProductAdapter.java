package com.sarin.prod.goodshost.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.activity.ProductDetailActivity;
import com.sarin.prod.goodshost.item.ProductItem;
import com.sarin.prod.goodshost.util.StringUtil;

import java.util.List;


public class FavoriteProductAdapter extends RecyclerView.Adapter<FavoriteProductAdapter.ViewHolder>{

    private List<ProductItem> items;
    public static String TAG = MainApplication.TAG;
    private static Context context;

    static StringUtil sUtil = StringUtil.getInstance();

    public FavoriteProductAdapter(List<ProductItem> items){
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_favorite_list_view , parent, false);
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
        notifyDataSetChanged();
    }

    public void setItems(List<ProductItem> list){
        items = list;
        notifyDataSetChanged();
    }

    public void set(int position, ProductItem productItem){
        items.set(position, productItem);
        notifyDataSetChanged();
    }

    public void clear(){
        items.clear();
//        notifyDataSetChanged();
    }

    public void remove(int position){
        items.remove(position);
        notifyDataSetChanged();
    }

    public ProductItem get(int position){
        return items.get(position);
    }


    public int size() {
        int i = items.size();
        return i;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView name, price_value, persent, favorite_hope_price, favorite_hope_stock;
        private ImageView image;
//        private RatingBar rating;

        private LinearLayout favorite_del, favorite_alarm_edit;

        ProductItemClickListener productItemClickListener;

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            price_value = (TextView) itemView.findViewById(R.id.price_value);
            image = (ImageView) itemView.findViewById(R.id.imagew);
            favorite_hope_price = (TextView) itemView.findViewById(R.id.favorite_hope_price);
            favorite_hope_stock = (TextView) itemView.findViewById(R.id.favorite_hope_stock);
            persent = (TextView) itemView.findViewById(R.id.persent);
            favorite_del = (LinearLayout) itemView.findViewById(R.id.favorite_del);
            favorite_alarm_edit = (LinearLayout) itemView.findViewById(R.id.favorite_alarm_edit);

            favorite_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 클릭 이벤트 처리
//                    onFavoriteClick(getLayoutPosition());
                    //존재하는 포지션인지 확인
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        //동작 호출 (onItemClick 함수 호출)
                        if(onItemClickListener != null){
                            onItemClickListener.onItemClick(v, pos);
                        }
                    }

                }
            });

            favorite_alarm_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 클릭 이벤트 처리
//                    onFavoriteClick(getLayoutPosition());
                    //존재하는 포지션인지 확인
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        //동작 호출 (onItemClick 함수 호출)
                        if(onItemClickListener != null){
                            onItemClickListener.onItemClick(v, pos);
                        }
                    }

                }
            });


//            layout_favorite.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // 클릭 이벤트 처리
//                    onFavoriteClick(getLayoutPosition());
//                }
//            });

            itemView.setOnClickListener(this);
        }

        private void onFavoriteClick(int position) {
            Log.d(TAG, "Favorite clicked at position: " + position);

            // 여기에서 필요한 작업 수행 (예: 즐겨찾기 추가/제거)
        }

        public void setItem(ProductItem pitem){
            name.setText(pitem.getName());
            price_value.setText(sUtil.replaceStringPriceToInt(pitem.getPrice_value()) + context.getResources().getString(R.string.won));
//            String url = "https:" + pitem.getImage();
            String url = pitem.getImage();
            if(!url.contains("https:")){
                url = "https:" + url;
            }
            Glide.with(itemView.getContext()).load(url).into(image);

            double number = Double.parseDouble(pitem.getPersent());
            if(number < 0){
                String numString = getNumberConverter(number);
                persent.setVisibility(View.VISIBLE); // 레이아웃 표시
                persent.setText(numString + "% off"); // 할인율 표시
            } else {
                persent.setVisibility(View.GONE); // 레이아웃 숨김
            }

            if(pitem.getHope_price() > 0){
                favorite_hope_price.setText(sUtil.replaceStringPriceToInt(pitem.getHope_price()) + context.getResources().getString(R.string.won));
            } else {
                favorite_hope_price.setVisibility(View.GONE); // 레이아웃 숨김
            }


            if("Y".equals(pitem.getHope_stock())){
                favorite_hope_stock.setText(context.getResources().getString(R.string.favorite_stock_alarm));
            } else {
                favorite_hope_stock.setVisibility(View.GONE); // 레이아웃 숨김
            }


        }

        @Override
        public void onClick(View v){
            this.productItemClickListener.onItemClickListener(v,getLayoutPosition());
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


    public static String getNumberConverter (double number){

        String convertedString = "";
        // 문자열을 Double로 변환
        try{
            // 반올림
            number = Math.round(number);
            // 마이너스 제거 (절대값 사용)
            number = Math.abs(number);

            // 숫자를 문자열로 변환
            convertedString = String.valueOf((int)number);
            // 결과 출력
//            System.out.println("변환된 문자열: " + convertedString);
        }catch(Exception e){
            e.printStackTrace();
        }

        return convertedString;
    }

}


