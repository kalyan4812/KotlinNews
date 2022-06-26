package com.saikalyandaroju.kotlinnews.utils

class Constants {
    companion object {
        const val BASE_URL: String = "https://newsapi.org"
        const val QUERY_PAGE_SIZE: Int = 20


        const val PRAGMA_HEADER =
            "Pragma" // it is a header ,attched to http request ,it may not allow request

        // to use caching.so we remove it while caching the response.
        const val CACHE_CONTROL_HEADER = "Cache-Control" // cache control from server.
        const val CONNECTION_TIMEOUT: Long = 30000


        const val DATABASENAME: String = "ARTICLE_DATABASE"


        //SharedPrefs
        const val SHARED_DB_KEY = "user_info"
        const val U_NAME = "uname"
        const val U_NUMBER = "unumber"
        const val U_PROFILEPIC = "upic"
        const val OTP_STEP = "completed_otp_step"
        const val PROFILE_STEP = "completed_profile_step"
        const val NEW_USER = "new_user"

    }
}