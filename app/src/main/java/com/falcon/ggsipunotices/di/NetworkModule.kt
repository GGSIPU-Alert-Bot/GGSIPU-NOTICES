package com.falcon.ggsipunotices.di

import com.falcon.ggsipunotices.network.ApiService
import com.falcon.ggsipunotices.network.FcmApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val NOTICES_BASE_URL = "https://notice-scrap-server.azurewebsites.net"
    private const val FCM_BASE_URL = "https://fcm-server-1.onrender.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // Increase connection timeout
        .readTimeout(30, TimeUnit.SECONDS)    // Increase read timeout
        .writeTimeout(30, TimeUnit.SECONDS)   // Increase write timeout
        .build()

    private val fcmRetrofit =
        Retrofit.Builder()
            .baseUrl(FCM_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    private val noticesRetrofit =
        Retrofit.Builder()
            .baseUrl(NOTICES_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


    @Provides
    @Singleton
    fun provideApiService(): ApiService =
        noticesRetrofit.create(ApiService::class.java)


    @Provides
    @Singleton
    fun provideFcmApiService(): FcmApiService =
        fcmRetrofit.create(FcmApiService::class.java)
}
