package com.example.weatherusapplication.data.network

import com.example.weatherusapplication.data.network.service.APIService
import com.example.weatherusapplication.domain.utilities.Const.Companion.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * In this theRetrofitInstance() function, we build the retrofit instance for generating API calling.
 * We have to pass the base url of the API.
 */
@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun theRetrofitInstance(): APIService {
        val API: APIService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(APIService::class.java)
        }
        return API
    }
}