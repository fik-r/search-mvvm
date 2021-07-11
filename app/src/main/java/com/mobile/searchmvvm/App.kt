package com.mobile.searchmvvm

import android.app.Application
import com.mobile.searchmvvm.di.appModule
import com.mobile.searchmvvm.di.domainModule
import com.mobile.searchmvvm.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(appModule + networkModule + domainModule)
        }
    }
}