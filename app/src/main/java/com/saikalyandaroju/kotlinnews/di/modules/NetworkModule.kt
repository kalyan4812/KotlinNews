package com.saikalyandaroju.kotlinnews.di.modules

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.saikalyandaroju.kotlinnews.di.qualifiers.OnlineInterceptor
import com.saikalyandaroju.kotlinnews.utils.Constants
import com.saikalyandaroju.kotlinnews.utils.Constants.Companion.BASE_URL
import com.saikalyandaroju.kotlinnews.utils.Constants.Companion.CACHE_CONTROL_HEADER
import com.saikalyandaroju.kotlinnews.utils.Constants.Companion.PRAGMA_HEADER
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {


    //providing file for cache storage
    @Singleton
    @Provides
    fun getFile(@ApplicationContext context: Context): File {
        val file = File(context.getCacheDir(), "my_network_cache")
        if (!file.exists()) {
            file.mkdir()
        }
        return file
    }

    //providing cache of 10MB size
    @Singleton
    @Provides
    fun getCache(file: File): Cache? {
        return Cache(file, 10 * 1024 * 1024)
    }


    //providing Interceptor
    @Singleton
    @Provides
    @OnlineInterceptor
    fun getNetworkInterceptor(@ApplicationContext context: Context): Interceptor {
        return object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val response = chain.proceed(chain.request().newBuilder().build())

                val cacheControl: CacheControl = CacheControl.Builder()
                    .maxAge(1, TimeUnit.MINUTES)
                    // if another request made within 1 min,it will be from cache.
                    .build()




                return response.newBuilder().removeHeader(PRAGMA_HEADER)
                    .removeHeader(CACHE_CONTROL_HEADER)
                    .removeHeader("Expires")
                    .addHeader(CACHE_CONTROL_HEADER, cacheControl.toString())
                    .build()


            }

        }
    }


    @Singleton
    @Provides
    @com.saikalyandaroju.kotlinnews.di.qualifiers.Interceptor
    fun getInterceptor(@ApplicationContext context: Context): Interceptor {
        return object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                var request = chain.request()

                if (request.cacheControl.noCache) {
                    return chain.proceed(request)  // if no cache go for request from network.
                }
                val cacheControl: CacheControl

                if (!isInternetAvailable(context)) {

                    cacheControl = CacheControl.Builder()
                        .maxStale(2, TimeUnit.DAYS).onlyIfCached()
                        .build()

                    Log.d("check", "$cacheControl\n NO internt avaialabe")
                    request = request.newBuilder().removeHeader(PRAGMA_HEADER)
                        .removeHeader(CACHE_CONTROL_HEADER)
                        .addHeader(CACHE_CONTROL_HEADER, cacheControl.toString())
                        .build()
                }

                return chain.proceed(request)


            }

        }
    }


    //checking connection
    private fun isInternetAvailable(@ApplicationContext context: Context): Boolean {
        try {
            val e = context.getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager
            val activeNetwork = e.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting
        } catch (e: Exception) {
            Log.w("", e.toString())
        }
        return false
    }

    //providing httplogginginterceptor
    @Singleton
    @Provides
    fun getHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    //providing okhttpclient
    @Singleton
    @Provides
    fun getokhttpclient(
        cache: Cache?,
        httpLoggingInterceptor: HttpLoggingInterceptor,
        @com.saikalyandaroju.kotlinnews.di.qualifiers.Interceptor interceptors: Interceptor,
        @OnlineInterceptor onlineInterceptor: Interceptor
    ): OkHttpClient {

        return OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor)
            .addNetworkInterceptor(onlineInterceptor) // only used when network is on.
            .addInterceptor(interceptors) // used in offline or online.
            .cache(cache)
            .readTimeout(Constants.CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
            .connectTimeout(Constants.CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS).writeTimeout(
                Constants.CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS
            )
            .build()
    }


    //provide retrofit instance
    @Singleton
    @Provides
    fun getRetrofitInstance(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder().client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL).build()
    }

}