package com.dmitry.test.animeapplication.di

import com.dmitry.test.animeapplication.data.authorization.TokenStorage
import com.dmitry.test.animeapplication.data.authorization.TokenStorageImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StorageModule {

    @Binds
    @Singleton
    abstract fun bindTokenStorage(
        impl: TokenStorageImpl
    ): TokenStorage
}