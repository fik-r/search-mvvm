package com.mobile.searchmvvm.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mobile.searchmvvm.BuildConfig
import com.mobile.searchmvvm.network.NetworkResponseAdapterFactory
import com.mobile.searchmvvm.service.AppService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


val networkModule = module {
    single { createOkHttpClient(androidContext()) }
    single { createGson() }
    single { createApi<AppService>(get(), get()) }
}

fun createGson(): Gson {
    return GsonBuilder()
        .create()
}

inline fun <reified T> createApi(okHttpClient: OkHttpClient, gson: Gson): T {
    val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .addCallAdapterFactory(NetworkResponseAdapterFactory())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    return retrofit.create(T::class.java)
}

fun createOkHttpClient(context: Context): OkHttpClient {
    val httpLoggingInterceptor = HttpLoggingInterceptor()
        .apply { level = HttpLoggingInterceptor.Level.BODY }
    return OkHttpClient.Builder().apply {

        if (BuildConfig.DEBUG) {
            addInterceptor(httpLoggingInterceptor)
            addNetworkInterceptor(ChuckerInterceptor(context))
        }
    }.build()
}