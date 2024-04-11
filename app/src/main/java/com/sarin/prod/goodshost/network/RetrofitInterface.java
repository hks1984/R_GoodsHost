package com.sarin.prod.goodshost.network;

import com.sarin.prod.goodshost.item.CategoryItem;
import com.sarin.prod.goodshost.item.ChartItem;
import com.sarin.prod.goodshost.item.ProductItem;
import com.sarin.prod.goodshost.item.ReturnMsgItem;
import com.sarin.prod.goodshost.item.ReturnObjMsgItem;
import com.sarin.prod.goodshost.item.UserItem;
import com.sarin.prod.goodshost.item.VersionItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitInterface {


    @POST("{path}")
    Call<ReturnObjMsgItem> setLogin(
            @Path ("path") String path,
            @Body UserItem userItem
    );


    @POST("{path}")
    Call<ReturnMsgItem> setAccountDelete(
            @Path ("path") String path,
            @Body UserItem userItem
    );

    @POST("{path}")
    Call<ReturnMsgItem> setFcmTokenUpdate(
            @Path ("path") String path,
            @Body UserItem userItem
    );


    @POST("{path}")
    Call<VersionItem> getVersion(
            @Path ("path") String path,
            @Query("user_id") String user_id,
            @Query("app_version") String app_version,
            @Query("os_type") String os_type

    );

    @POST("{path}")
    Call<VersionItem> getVersion(
            @Path ("path") String path,
            @Query("os_type") String os_type

    );

    @POST("{path}")
    Call<ReturnObjMsgItem> getVersionJson(
            @Path ("path") String path,
            @Query("user_id") String user_id,
            @Body VersionItem versionItem

    );



    @POST("{path}")
    Call<List<ProductItem>> getSearchProducts(
            @Path ("path") String path,
            @Query("searchName") String searchName,
            @Query("user_id") String user_id,
            @Query("page") int page
    );

    @POST("{path}")
    Call<ProductItem> getProductDetail(
            @Path ("path") String path,
            @Query("user_id") String user_id,
            @Query("vendor_item_id") String vendor_item_id
    );

    @POST("{path}")
    Call<List<CategoryItem>> getCategoryList(
            @Path ("path") String path
    );

    @POST("{path}")
    Call<List<ProductItem>> getBestSalesProducts(
            @Path ("path") String path,
            @Query("cnt") int cnt,
            @Query("page") int page,
            @Query("user_id") String user_id,
            @Query("code") String code
    );

    @POST("{path}")
    Call<List<ProductItem>> getTopProducts(
            @Path ("path") String path,
            @Query("cnt") int cnt,
            @Query("page") int page,
            @Query("user_id") String user_id,
            @Query("code") String code
    );

    @POST("{path}")
    Call<List<ChartItem>> getProductChart(
            @Path ("path") String path,
            @Query("vendor_item_id") String vendor_item_id
    );


    @POST("{path}")
    Call<ReturnMsgItem> setUserItemMap(
            @Path ("path") String path,
            @Query("user_id") String user_id,
            @Query("vendor_item_id") String vendor_item_id,
            @Query("hope_low_price") String hope_low_price,
            @Query("hope_price") int hope_price,
            @Query("hope_stock") String hope_stock
    );

    @POST("{path}")
    Call<ReturnMsgItem> setDelUserItemMap(
            @Path ("path") String path,
            @Query("user_id") String user_id,
            @Query("vendor_item_id") String vendor_item_id
    );

    @POST("{path}")
    Call<ReturnMsgItem> setUserSelectProduct(
            @Path ("path") String path,
            @Query("user_id") String user_id,
            @Query("deep_link") String deep_link
    );

    @POST("{path}")
    Call<List<ProductItem>> getFavoriteProducts(
            @Path ("path") String path,
            @Query("user_id") String user_id
    );

    @POST("{path}")
    Call<List<String>> getAutoCompleteText(
            @Path ("path") String path,
            @Query("search_text") String search_text
    );

    @POST("{path}")
    Call<List<ProductItem>> getProductsRelated(
            @Path ("path") String path,
            @Query("user_id") String user_id,
            @Query("product_name") String product_name
    );

    @POST("{path}")
    Call<List<String>> getPopularSearch(
            @Path ("path") String path
    );


























}

