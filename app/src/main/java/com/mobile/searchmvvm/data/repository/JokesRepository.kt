package com.mobile.searchmvvm.data.repository

import com.mobile.searchmvvm.base.BaseResponse
import com.mobile.searchmvvm.base.ResponseError
import com.mobile.searchmvvm.data.model.Jokes
import com.mobile.searchmvvm.network.NetworkResponse
import com.mobile.searchmvvm.service.AppService

interface JokesRepository {
    suspend fun searchJokes(query: String): NetworkResponse<BaseResponse<List<Jokes>>, ResponseError>
}

open class JokesRepositoryImpl(private val appService: AppService) : JokesRepository {
    override suspend fun searchJokes(query: String): NetworkResponse<BaseResponse<List<Jokes>>, ResponseError> {
        return appService.searchJokes(query)
    }
}