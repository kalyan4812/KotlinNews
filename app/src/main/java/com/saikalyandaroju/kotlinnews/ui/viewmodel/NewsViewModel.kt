package com.saikalyandaroju.kotlinnews.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saikalyandaroju.kotlinnews.repository.NewsRepository
import com.saikalyandaroju.kotlinnews.source.models.Article
import com.saikalyandaroju.kotlinnews.source.models.NewsResponse
import com.saikalyandaroju.kotlinnews.utils.NetworkResponseHandler
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(val newRepository: NewsRepository) : ViewModel() {

    // by default ,we can't use params in constructor of view model.
    // to do constructur mechanism of getting object/our own instance we need cutsom viewmodelfactory.


    val breakingNewsresponse: MutableLiveData<NetworkResponseHandler<NewsResponse>> =
        MutableLiveData()

    var breakingNewsPage = 1


    init {
        getBreakingNews("us")
    }

    // now we need to call getBreakingnews fun in news repository,since it is a suspended function.you can only call it from another suspended or
    // from couroutine.and we know ,best way to start couroutine in a viewmodel is using a viewmodelscope.

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {

        breakingNewsresponse.postValue(NetworkResponseHandler.Loading())

        val response = newRepository.getBreakingNews(countryCode, breakingNewsPage)

        breakingNewsresponse.postValue(handleNewsResponse(response))

    }

    private fun handleNewsResponse(response: Response<NewsResponse>): NetworkResponseHandler<NewsResponse>? {
        if (response.isSuccessful) {
            response.body()?.let { result ->
                Log.i("result",result.articles.toString())
                return NetworkResponseHandler.Success(result)
            }
        }

        return NetworkResponseHandler.Error(response.message(), null)

    }


    val searchNewsresponse: MutableLiveData<NetworkResponseHandler<NewsResponse>> =
        MutableLiveData()

    var searchNewsPage = 1

    fun getSearchNews(query: String) = viewModelScope.launch {
        searchNewsresponse.postValue(NetworkResponseHandler.Loading())
        val response = newRepository.getSearchedNews(query, searchNewsPage)
        searchNewsresponse.postValue(handleSearchNewsResponse(response))
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): NetworkResponseHandler<NewsResponse>? {
        if (response.isSuccessful) {
            response.body()?.let { result ->

                return NetworkResponseHandler.Success(result)
            }
        }

        return NetworkResponseHandler.Error(response.message(), null)
    }
//-------------------------------------------------------------------------------------------------

    fun saveArticle(article: Article) = viewModelScope.launch {
        newRepository.insertArticle(article)
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newRepository.deleteArticle(article)
    }

    fun getSavedNews() =
        newRepository.getSavedNews()  // no need of couroutine,since it was not a suspend function.
    // we can observe live data in our fragments.

}