package com.mobile.searchmvvm.domain

import com.mobile.searchmvvm.base.BaseResponse
import com.mobile.searchmvvm.base.ResponseError
import com.mobile.searchmvvm.data.model.Jokes
import com.mobile.searchmvvm.data.repository.JokesRepository
import com.mobile.searchmvvm.network.NetworkResponse
import com.mobile.searchmvvm.network.UseCase

class GetSearchJokesUseCase(
    private val jokesRepository: JokesRepository
) : UseCase<BaseResponse<List<Jokes>>, ResponseError, String>() {

    override suspend fun build(params: String?): NetworkResponse<BaseResponse<List<Jokes>>, ResponseError> {
        requireNotNull(params) { "params must not be null" }
        return jokesRepository.searchJokes(params)
    }

}