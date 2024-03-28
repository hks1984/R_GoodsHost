package com.sarin.prod.goodshost.network;

import com.sarin.prod.goodshost.item.CategoryItem;
import com.sarin.prod.goodshost.item.ChartItem;
import com.sarin.prod.goodshost.item.ProductItem;
import com.sarin.prod.goodshost.item.ReturnMsgItem;
import com.sarin.prod.goodshost.item.UserItem;
import com.sarin.prod.goodshost.item.VersionItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitInterface {


    @GET("{path}")
    Call<VersionItem> getVersion(
            @Path ("path") String path,
            @Query("user_id") String user_id,
            @Query("app_version") String app_version,
            @Query("os_type") String os_type

    );

    @GET("{path}")
    Call<List<ProductItem>> getSearchProducts(
            @Path ("path") String path,
            @Query("searchName") String searchName,
            @Query("user_id") String user_id,
            @Query("page") int page
    );

    @GET("{path}")
    Call<ProductItem> getProductDetail(
            @Path ("path") String path,
            @Query("user_id") String user_id,
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
            @Query("page") int page,
            @Query("user_id") String user_id,
            @Query("code") String code
    );

    @GET("{path}")
    Call<List<ProductItem>> getTopProducts(
            @Path ("path") String path,
            @Query("cnt") int cnt,
            @Query("page") int page,
            @Query("user_id") String user_id,
            @Query("code") String code
    );

    @GET("{path}")
    Call<List<ChartItem>> getProductChart(
            @Path ("path") String path,
            @Query("vendor_item_id") String vendor_item_id
    );

    @GET("{path}")
    Call<ReturnMsgItem> setUserRegister(
            @Path ("path") String path,
            @Query("user_id") String user_id,
            @Query("fcm_token") String fcm_token
    );

    @GET("{path}")
    Call<ReturnMsgItem> setUserItemMap(
            @Path ("path") String path,
            @Query("user_id") String user_id,
            @Query("vendor_item_id") String vendor_item_id,
            @Query("hope_low_price") String hope_low_price,
            @Query("hope_price") int hope_price,
            @Query("hope_stock") String hope_stock
    );

    @GET("{path}")
    Call<ReturnMsgItem> setDelUserItemMap(
            @Path ("path") String path,
            @Query("user_id") String user_id,
            @Query("vendor_item_id") String vendor_item_id
    );

    @GET("{path}")
    Call<ReturnMsgItem> setUserSelectProduct(
            @Path ("path") String path,
            @Query("user_id") String user_id,
            @Query("deep_link") String deep_link
    );

    @GET("{path}")
    Call<List<ProductItem>> getFavoriteProducts(
            @Path ("path") String path,
            @Query("user_id") String user_id
    );

    @GET("{path}")
    Call<List<String>> getAutoCompleteText(
            @Path ("path") String path,
            @Query("search_text") String search_text
    );

    @GET("{path}")
    Call<List<ProductItem>> getProductsRelated(
            @Path ("path") String path,
            @Query("user_id") String user_id,
            @Query("product_name") String product_name
    );

    @GET("{path}")
    Call<List<String>> getPopularSearch(
            @Path ("path") String path
    );


























}

