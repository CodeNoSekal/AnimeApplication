package com.dmitry.yume.di

import com.dmitry.yume.data.repository.AnimeRepositoryImpl
import com.dmitry.yume.data.repository.AuthRepositoryImpl
import com.dmitry.yume.data.repository.MeRepositoryImpl
import com.dmitry.yume.data.repository.MetaRepositoryImpl
import com.dmitry.yume.data.repository.PlaybackRepositoryImpl
import com.dmitry.yume.domain.repository.AnimeRepository
import com.dmitry.yume.domain.repository.AuthRepository
import com.dmitry.yume.domain.repository.MeRepository
import com.dmitry.yume.domain.repository.MetaRepository
import com.dmitry.yume.domain.repository.PlaybackRepository
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

    @Binds
    @Singleton
    abstract fun bindPlaybackRepository(
        impl: PlaybackRepositoryImpl
    ): PlaybackRepository

    @Binds
    @Singleton
    abstract fun bindMeRepository(
        impl: MeRepositoryImpl
    ): MeRepository


    @Binds
    @Singleton
    abstract fun bindMetaRepository(
        impl: MetaRepositoryImpl
    ): MetaRepository
}
