package com.saikalyandaroju.kotlinnews.utils

class Constants {
    companion object {
        const val API_KEY: String = "36a3663eac9a41df9f5b6f7e26b96b5a"
        const val BASE_URL: String = "https://newsapi.org"
        const val QUERY_PAGE_SIZE: Int = 20


        const val PRAGMA_HEADER = "Pragma" // it is a header ,attched to http request ,it may not allow request
        // to use caching.so we remove it while caching the response.
        const val CACHE_CONTROL_HEADER = "Cache-Control" // cache control from server.
        const val CONNECTION_TIMEOUT: Long = 30000


        const val DATABASENAME:String="ARTICLE_DATABASE"

    }
}