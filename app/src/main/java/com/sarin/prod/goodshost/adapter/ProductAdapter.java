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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sarin.prod.goodshost.MainApplication;
import com.sarin.prod.goodshost.R;
import com.sarin.prod.goodshost.item.ProductItem;
import com.sarin.prod.goodshost.util.StringUtil;

import java.util.List;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder>{

    private List<ProductItem> items;
    public static String TAG = MainApplication.TAG;
    private static Context context;
    static StringUtil sUtil = StringUtil.getInstance();
    private RecyclerViewClickListener recyclerViewClickListener;
    public ProductAdapter(List<ProductItem> items, RecyclerViewClickListener listener )
    {
        this.items = items;
        this.recyclerViewClickListener = listener;
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

        // 전체 아이템 클릭 이벤트
        // activity 또는 fragment 화면단 코드에서 onItemClickListener 메소드 추가해줘야함.
        holder.LinearLayout.setOnClickListener(v -> recyclerViewClickListener.onItemClickListener(v, position));
        holder.layout_favorite.setOnClickListener(v -> recyclerViewClickListener.onItemClickListener(v, position));

    }

    public void addItems(List<ProductItem> items){
        this.items.addAll( items);
        notifyDataSetChanged();
    }

    public ProductItem get(int possion) {
        return items.get(possion);
    }

    public void setItems(List<ProductItem> list){
        items = list;
        notifyDataSetChanged();
    }

    public void set (int pos, ProductItem item) {
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
        private TextView name, price_value, rating_total_count, persent;
        private ImageView image, favorite;
        private RatingBar rating;

        private LinearLayout layout_favorite, LinearLayout;



        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            price_value = (TextView) itemView.findViewById(R.id.price_value);
            image = (ImageView) itemView.findViewById(R.id.imagew);
            rating = (RatingBar) itemView.findViewById(R.id.rating);
            rating_total_count = (TextView) itemView.findViewById(R.id.rating_total_count);
            persent = (TextView) itemView.findViewById(R.id.persent);
            favorite = (ImageView) itemView.findViewById(R.id.favorite);
            layout_favorite = (LinearLayout) itemView.findViewById(R.id.layout_favorite);
            LinearLayout = (LinearLayout) itemView.findViewById(R.id.list_view);

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
            if(pitem.getRating() == null){
                pitem.setRating("0");
            }
            rating.setRating(Float.parseFloat(pitem.getRating()));
            String ratingTotalCount = sUtil.replaceStringPriceToInt(pitem.getRating_total_count());
            if (ratingTotalCount != null && !ratingTotalCount.isEmpty()) {
                rating_total_count.setText("(" + ratingTotalCount + ")");
            } else {
                rating_total_count.setText("");
            }

            double number = Double.parseDouble(pitem.getPersent());
            if(number < 0){
                String numString = getNumberConverter(number);
                persent.setVisibility(View.VISIBLE); // 레이아웃 표시
                persent.setText(numString + "% off"); // 할인율 표시
            } else {
                persent.setVisibility(View.GONE); // 레이아웃 숨김
            }

            if(pitem.isIs_Favorite()){
                favorite.setImageResource(R.drawable.baseline_favorite_24);
            }else{
                favorite.setImageResource(R.drawable.baseline_favorite_border_24);
            }


        }


    }


    @Override
    public int getItemViewType(int position) {
        return position;
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


