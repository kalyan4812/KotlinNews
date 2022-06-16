package com.saikalyandaroju.kotlinnews.model.source.remote


class ServiceProvider {

   /* companion object {

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
    }*/
}