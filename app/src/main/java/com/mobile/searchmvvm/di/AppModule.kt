package com.mobile.searchmvvm.di

import com.mobile.searchmvvm.MainViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {
    viewModel {
        MainViewModel(
            getSearchJokesUseCase = get()
        )
    }


}

val appModule = listOf(viewModelModule)