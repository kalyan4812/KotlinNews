package com.saikalyandaroju.kotlinnews.utils

sealed class NetworkResponseHandler<T>(val data:T?=null,val message:String?=null){


    class Success<T>(data: T):NetworkResponseHandler<T>(data)
    class Error<T>(message: String,data: T?):NetworkResponseHandler<T>(data,message)
    class Loading<T>:NetworkResponseHandler<T>()

}
