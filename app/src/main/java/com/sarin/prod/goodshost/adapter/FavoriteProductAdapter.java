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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.item.ProductItem;
import com.sarin.prod.goodshost.util.StringUtil;

import java.util.List;


public class FavoriteProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ProductItem> items;
    public static String TAG = MainApplication.TAG;
    private static Context context;

    static StringUtil sUtil = StringUtil.getInstance();
    private RecyclerViewClickListener recyclerViewClickListener;

    public static int TYPE_HEADER = 0;
    public static int TYPE_ITEM = 1;
    public static int TYPE_FOOTER = 2;

    public FavoriteProductAdapter(List<ProductItem> items, RecyclerViewClickListener listener){

        this.items = items;
        this.recyclerViewClickListener = listener;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        RecyclerView.ViewHolder holder;
        View view;

        if (viewType == TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_favorite_header, parent, false);
            holder = new HeaderViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_favorite_footer, parent, false);
            holder = new FooterViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_favorite_list, parent, false);
            holder = new ViewHolder(view);
        }
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
        } else {
            // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
            ViewHolder itemViewHolder = (ViewHolder) holder;
            ProductItem item = items.get(position - 1);
            itemViewHolder.setItem(item);

            itemViewHolder.favorite_del.setOnClickListener(v -> recyclerViewClickListener.onItemClickListener(v, position));
            itemViewHolder.favorite_alarm_edit.setOnClickListener(v -> recyclerViewClickListener.onItemClickListener(v, position));
            itemViewHolder.list_view_favorite.setOnClickListener(v -> recyclerViewClickListener.onItemClickListener(v, position));
        }


    }

    public void addItems(List<ProductItem> items){
        this.items.addAll( items);
        notifyDataSetChanged();
    }

    public void addItem(ProductItem items){
        this.items.add( items);
        notifyDataSetChanged();
    }

    public void setItems(List<ProductItem> list){
        items = list;
        notifyDataSetChanged();
    }

    public void set(int position, ProductItem productItem){
        items.set(position - 1, productItem);
        notifyDataSetChanged();
    }

    public void clear(){
        items.clear();
//        notifyDataSetChanged();
    }

    public void remove(int position){
        items.remove(position - 1);
        notifyDataSetChanged();
    }

    public ProductItem get(int position){
        return items.get(position - 1);
    }


    public int size() {
        int i = items.size();
        return i;
    }

    @Override
    public int getItemCount() {

        return items.size() + 2;
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name, price_value, persent, favorite_hope_price, favorite_hope_stock, favorite_hope_low_price;
        private ImageView image;
//        private RatingBar rating;

        private LinearLayout favorite_del, favorite_alarm_edit;

        private ConstraintLayout list_view_favorite;

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            price_value = (TextView) itemView.findViewById(R.id.price_value);
            image = (ImageView) itemView.findViewById(R.id.imagew);
            favorite_hope_price = (TextView) itemView.findViewById(R.id.favorite_hope_price);
            favorite_hope_stock = (TextView) itemView.findViewById(R.id.favorite_hope_stock);
//            favorite_hope_low_price = (TextView) itemView.findViewById(R.id.favorite_hope_low_price);
            persent = (TextView) itemView.findViewById(R.id.persent);
            favorite_del = (LinearLayout) itemView.findViewById(R.id.favorite_del);
            favorite_alarm_edit = (LinearLayout) itemView.findViewById(R.id.favorite_alarm_edit);
            list_view_favorite = (ConstraintLayout) itemView.findViewById(R.id.list_view_favorite);


        }

        public void setItem(ProductItem pitem){
            name.setText(pitem.getName());
            price_value.setText(sUtil.replaceStringPriceToInt(pitem.getPrice_value()) + context.getResources().getString(R.string.won));
//            String url = "https:" + pitem.getImage();
            String url = pitem.getImage();
            if(!url.contains("https:")){
                url = "https:" + url;
            }

            Glide.with(itemView.getContext())
                    .load(url)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(20))) // 여기서 10은 코너의 반지름을 dp 단위로 지정
                    .into(image);


            double number = Double.parseDouble(pitem.getPersent());
            if(number < 0){
                String numString = getNumberConverter(number);
                persent.setVisibility(View.VISIBLE); // 레이아웃 표시
                persent.setText(numString + "%"); // 할인율 표시
            } else {
                persent.setVisibility(View.GONE); // 레이아웃 숨김
            }


//            if("Y".equals(pitem.getHope_low_price())){
//                favorite_hope_low_price.setVisibility(View.VISIBLE);
//            } else {
//                favorite_hope_low_price.setVisibility(View.GONE); // 레이아웃 숨김
//            }

            if(pitem.getHope_price() > 0){
                favorite_hope_price.setVisibility(View.VISIBLE);
                favorite_hope_price.setText(sUtil.replaceStringPriceToInt(pitem.getHope_price()) + context.getResources().getString(R.string.won));
            } else {
                favorite_hope_price.setVisibility(View.GONE); // 레이아웃 숨김
            }


            if("Y".equals(pitem.getHope_stock())){
                favorite_hope_stock.setVisibility(View.VISIBLE);
            } else {
                favorite_hope_stock.setVisibility(View.GONE); // 레이아웃 숨김
            }


        }

    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {


        HeaderViewHolder(View headerView) {
            super(headerView);


        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {

        FooterViewHolder(View footerView) {
            super(footerView);

        }
    }


    @Override
    public int getItemViewType(int position) {


        if (position == 0)
            return TYPE_HEADER;
        else if (position == items.size() +1)
            return TYPE_FOOTER;
        else
            return TYPE_ITEM;
    }

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


