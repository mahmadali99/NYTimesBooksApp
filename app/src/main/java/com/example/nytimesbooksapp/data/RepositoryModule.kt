package com.example.nytimesbooksapp.data

import com.example.nytimesbooksapp.data.local.repository.BookRepositoryImpl
import com.example.nytimesbooksapp.domain.repository.BookRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)


abstract class RepositoryModule {


    @Binds
    @Singleton
    abstract fun bindBooksRepository(
        impl : BookRepositoryImpl
    ) : BookRepository

}