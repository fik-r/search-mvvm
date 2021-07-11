package com.mobile.searchmvvm.network

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import okio.IOException
import retrofit2.*
import java.lang.reflect.Type

internal class DeferredNetworkResponseAdapter<T : Any, U : Any>(
    private val successBodyType: Type,
    private val errorConverter: Converter<ResponseBody, U>
) : CallAdapter<T, Deferred<NetworkResponse<T, U>>> {

    override fun responseType(): Type = successBodyType

    override fun adapt(call: Call<T>): Deferred<NetworkResponse<T, U>> {
        val deferred = CompletableDeferred<NetworkResponse<T, U>>()
        deferred.invokeOnCompletion {
            if (deferred.isCancelled) {
                call.cancel()
            }
        }
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val networkResponse =
                    ResponseHandler.handle(response, successBodyType, errorConverter)
                deferred.complete(networkResponse)
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                try {
                    val networkResponse = t.extractNetworkResponse<T, U>(errorConverter)
                    deferred.complete(networkResponse)
                } catch (t: Throwable) {
                    deferred.completeExceptionally(t)
                }
            }
        })
        return deferred
    }

}

const val UNKNOWN_ERROR_RESPONSE_CODE = 520

fun <E : Any> HttpException.extractFromHttpException(
    errorConverter: Converter<ResponseBody, E>
): NetworkResponse.ServerError<E> {
    val error = response()?.errorBody()
    val responseCode = response()?.code() ?: UNKNOWN_ERROR_RESPONSE_CODE
    val headers = response()?.headers()
    val errorBody = when {
        error == null -> null
        error.contentLength() == 0L -> null
        else -> try {
            errorConverter.convert(error)
        } catch (e: Exception) {
            return NetworkResponse.ServerError(null, responseCode, headers)
        }
    }
    return NetworkResponse.ServerError(errorBody, responseCode, headers)
}

fun <S : Any, E : Any> Throwable.extractNetworkResponse(
    errorConverter: Converter<ResponseBody, E>
): NetworkResponse<S, E> {
    return when (this) {
        is IOException -> NetworkResponse.NetworkError(this)
        is HttpException -> extractFromHttpException(errorConverter)
        else -> NetworkResponse.UnknownError(this)
    }
}