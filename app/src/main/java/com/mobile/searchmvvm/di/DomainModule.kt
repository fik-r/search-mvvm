package com.mobile.searchmvvm.di

import com.mobile.searchmvvm.data.repository.JokesRepository
import com.mobile.searchmvvm.data.repository.JokesRepositoryImpl
import com.mobile.searchmvvm.domain.GetSearchJokesUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single { GetSearchJokesUseCase(jokesRepository = get()) }
}

val repositoryModule = module {
    single<JokesRepository> { JokesRepositoryImpl(appService = get()) }
}

val domainModule = listOf(repositoryModule, useCaseModule)