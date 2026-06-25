package com.dmitry.test.animeapplication.di

import com.dmitry.test.animeapplication.data.repository.AnimeRepositoryImpl
import com.dmitry.test.animeapplication.data.repository.AuthRepositoryImpl
import com.dmitry.test.animeapplication.domain.repository.AnimeRepository
import com.dmitry.test.animeapplication.domain.repository.AuthRepository
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
    abstract fun bindAnimeRepository(
        impl: AnimeRepositoryImpl
    ): AnimeRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

}
