package com.mobile.searchmvvm.network

import okhttp3.Headers
import okio.IOException

sealed class NetworkResponse<out T : Any, out U : Any> {

    data class Success<T : Any>(
        val body: T,
        val headers: Headers? = null,
        val code: Int
    ) : NetworkResponse<T, Nothing>()

    data class ServerError<U : Any>(
        val body: U?,
        val code: Int,
        val headers: Headers? = null
    ) : NetworkResponse<Nothing, U>()

    data class NetworkError(val error: IOException) : NetworkResponse<Nothing, Nothing>()

    data class UnknownError(
        val error: Throwable,
        val code: Int? = null,
        val headers: Headers? = null
    ) : NetworkResponse<Nothing, Nothing>()

}
