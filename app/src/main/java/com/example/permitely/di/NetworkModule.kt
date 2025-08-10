package com.example.permitely.di

import android.content.Context
import com.example.permitely.data.network.AuthApiService
import com.example.permitely.data.network.DashboardApiService
import com.example.permitely.data.network.GuardApiService
import com.example.permitely.data.network.ProfileApiService
import com.example.permitely.data.network.VisitorApiService
import com.example.permitely.data.storage.TokenStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt module for providing network-related dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Provides HTTP logging interceptor for debugging network requests
     */
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    /**
     * Provides configured OkHttp client with timeouts and logging
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    /**
     * Provides Retrofit instance configured for the backend API
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://permitly-production.up.railway.app/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Provides AuthApiService for authentication endpoints
     */
    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    /**
     * Provides DashboardApiService for dashboard endpoints
     */
    @Provides
    @Singleton
    fun provideDashboardApiService(retrofit: Retrofit): DashboardApiService {
        return retrofit.create(DashboardApiService::class.java)
    }

    /**
     * Provides ProfileApiService for profile endpoints
     */
    @Provides
    @Singleton
    fun provideProfileApiService(retrofit: Retrofit): ProfileApiService {
        return retrofit.create(ProfileApiService::class.java)
    }

    /**
     * Provides VisitorApiService for visitor endpoints
     */
    @Provides
    @Singleton
    fun provideVisitorApiService(retrofit: Retrofit): VisitorApiService {
        return retrofit.create(VisitorApiService::class.java)
    }

    /**
     * Provides GuardApiService for guard endpoints
     */
    @Provides
    @Singleton
    fun provideGuardApiService(retrofit: Retrofit): GuardApiService {
        return retrofit.create(GuardApiService::class.java)
    }

    /**
     * Provides TokenStorage for secure token management
     */
    @Provides
    @Singleton
    fun provideTokenStorage(@ApplicationContext context: Context): TokenStorage {
        return TokenStorage(context)
    }
}
