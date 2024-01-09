package com.sarin.prod.goodshost.network;

import com.sarin.prod.goodshost.item.CategoryItem;
import com.sarin.prod.goodshost.item.ProductItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitInterface {

    @GET("{path}")
    Call<List<ProductItem>> getCategoryProductList(
            @Path ("path") String path,
            @Query("category_id") String category_id,
            @Query("page") int page
    );


    @GET("{path}")
    Call<List<ProductItem>> getSearchProductList(
            @Path ("path") String path,
            @Query("searchName") String searchName,
            @Query("page") int page
    );

    @GET("{path}")
    Call<ProductItem> getProductDetail(
            @Path ("path") String path,
            @Query("vendor_item_id") String vendor_item_id
    );

    @GET("{path}")
    Call<List<CategoryItem>> getCategoryList(
            @Path ("path") String path
    );

    @GET("{path}")
    Call<List<ProductItem>> getBestSalesProducts(
            @Path ("path") String path,
            @Query("cnt") int cnt,
            @Query("page") int page
    );

    @GET("{path}")
    Call<List<ProductItem>> getTopProducts(
            @Path ("path") String path,
            @Query("cnt") int cnt,
            @Query("page") int page,
            @Query("code") String code
    );







}

