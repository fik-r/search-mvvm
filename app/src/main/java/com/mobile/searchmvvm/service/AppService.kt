package com.mobile.searchmvvm.service

import com.mobile.searchmvvm.base.BaseResponse
import com.mobile.searchmvvm.base.ResponseError
import com.mobile.searchmvvm.data.model.Jokes
import com.mobile.searchmvvm.network.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AppService {
    @GET("jokes/search")
    suspend fun searchJokes(
        @Query("query") query: String = ""
    ): NetworkResponse<BaseResponse<List<Jokes>>, ResponseError>
}