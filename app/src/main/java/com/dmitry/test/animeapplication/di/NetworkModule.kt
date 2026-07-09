package com.dmitry.test.animeapplication.di

import com.dmitry.test.animeapplication.BuildConfig
import com.dmitry.test.animeapplication.data.AuthApi
import com.dmitry.test.animeapplication.data.RefreshApi
import com.dmitry.test.animeapplication.data.AnimeApi
import com.dmitry.test.animeapplication.data.PlayerApi
import com.dmitry.test.animeapplication.data.VerificationApi
import com.dmitry.test.animeapplication.data.authorization.AuthInterceptor
import com.dmitry.test.animeapplication.data.authorization.TokenAuthenticator
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Qualifier @Retention(AnnotationRetention.BINARY)
    annotation class AuthClient

    @Qualifier @Retention(AnnotationRetention.BINARY)
    annotation class MainClient

    private const val BASE_URL = "https://api.verkstad.space/"

    @Provides @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor{
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) Level.BODY else Level.NONE
        }
    }

    @Provides @Singleton @MainClient
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor,
        authenticator: TokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .authenticator(authenticator)
            .build()
    }

    @Provides @Singleton @AuthClient
    fun provideAuthOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .build()
    }

    @Provides @Singleton @MainClient
    fun provideRetrofit(
        @MainClient okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides @Singleton @AuthClient
    fun provideAuthRetrofit(
        @AuthClient okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideTestApi(
        @MainClient retrofit: Retrofit
    ): AnimeApi {
        return retrofit.create(AnimeApi::class.java)
    }

    @Provides
    @Singleton
    fun providePlayerApi(
        @MainClient retrofit: Retrofit
    ): PlayerApi {
        return retrofit.create(PlayerApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRefreshApi(
        @AuthClient retrofit: Retrofit
    ): RefreshApi =
        retrofit.create(RefreshApi::class.java)

    @Provides
    @Singleton
    fun provideAuthApi(
        @MainClient retrofit: Retrofit
    ): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideVerificationApi(
        @MainClient retrofit: Retrofit
    ): VerificationApi {
        return retrofit.create(VerificationApi::class.java)
    }
}