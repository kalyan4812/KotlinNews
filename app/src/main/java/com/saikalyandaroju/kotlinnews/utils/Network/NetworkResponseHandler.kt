package com.saikalyandaroju.kotlinnews.utils.Network

sealed class NetworkResponseHandler<T>(val status: State,val data:T?=null,val message:String?=null){


    class Success<T>(data: T): NetworkResponseHandler<T>(State.SUCCESS,data,null)

    class Error<T>(status:State,data: T?,message: String?): NetworkResponseHandler<T>(State.ERROR,data,message)

    class Loading<T>: NetworkResponseHandler<T>(State.LOADING)

}
enum class State{
    SUCCESS,
    ERROR,
    LOADING
}