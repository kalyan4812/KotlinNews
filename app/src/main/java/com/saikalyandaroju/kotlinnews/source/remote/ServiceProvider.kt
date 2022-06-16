package com.saikalyandaroju.kotlinnews.source.remote

import com.saikalyandaroju.kotlinnews.utils.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ServiceProvider {

    companion object {

        // lazy means ,we only intialize once.
        private val retrofit by lazy {
            val httpLoggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            val okHttpClient = OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build()
            Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL)
                .client(okHttpClient).build()
        }

        val requestApi by lazy {
            retrofit.create(NewsApi::class.java)
        }
    }
}