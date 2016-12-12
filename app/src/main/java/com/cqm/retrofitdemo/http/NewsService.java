package com.cqm.retrofitdemo.http;

import com.cqm.retrofitdemo.bean.NewsResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by chenqm on 2016/12/12.
 */

public interface NewsService {


    @GET("toutiao/index")
    Call<NewsResult> getNewsData(@Query("key") String key, @Query("type") String type);





}
