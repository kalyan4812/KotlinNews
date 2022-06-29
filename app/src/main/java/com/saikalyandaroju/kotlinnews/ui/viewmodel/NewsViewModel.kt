package com.saikalyandaroju.kotlinnews.ui.viewmodel

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.saikalyandaroju.kotlinnews.model.repository.GlobalNewsRepository
import com.saikalyandaroju.kotlinnews.model.repository.NewsRepository
import com.saikalyandaroju.kotlinnews.model.source.models.Article
import com.saikalyandaroju.kotlinnews.model.source.models.NewsResponse
import com.saikalyandaroju.kotlinnews.utils.Network.NetworkResponseHandler
import com.saikalyandaroju.kotlinnews.utils.Network.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.internal.wait
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class NewsViewModel @Inject constructor(
    val newRepository: GlobalNewsRepository,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // by default ,we can't use params in constructor of view model.
    // to do constructur mechanism of getting object/our own instance we need cutsom viewmodelfactory.
    private val TAG = "NewsViewModel"

    val breakingNewsresponse: MutableLiveData<NetworkResponseHandler<PagingData<Article>>> =
        MutableLiveData()


    var breakingNewsPage = 1


    var isItemPresent = MutableLiveData<Boolean>()

    var rowValue = MutableLiveData<Long>()


    fun getIsItemPresent(): LiveData<Boolean> {
        return isItemPresent
    }

    fun getRowValue(): LiveData<Long> {
        return rowValue
    }

    init {
        getBreakingNews("us")
    }

    // now we need to call getBreakingnews fun in news repository,since it is a suspended function.you can only call it from another suspended or
    // from couroutine.and we know ,best way to start couroutine in a viewmodel is using a viewmodelscope.

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {

        breakingNewsresponse.postValue(NetworkResponseHandler.Loading())



        newRepository.getBreakingNews(countryCode, breakingNewsPage).observeForever {
            breakingNewsresponse.postValue(
                handleNewsResponse(
                    it
                )
            )
        }


    }

    private fun handleNewsResponse(response: PagingData<Article>?):
            NetworkResponseHandler<PagingData<Article>> {

        println("response is  $response")
        if (response != null) {
            return NetworkResponseHandler.Success(response)
        }


        return NetworkResponseHandler.Error(State.ERROR, null, null)

    }


    val searchNewsresponse: MutableLiveData<NetworkResponseHandler<PagingData<Article>>> =
        MutableLiveData()

    var searchNewsPage = 1

    fun getSearchNews(query: String) = viewModelScope.launch {
        searchNewsresponse.postValue(NetworkResponseHandler.Loading())
        newRepository.getSearchedNews(query, searchNewsPage).observeForever {
            searchNewsresponse.postValue(handleSearchNewsResponse(it))
        }
    }

    private fun handleSearchNewsResponse(response: PagingData<Article>?):
            NetworkResponseHandler<PagingData<Article>> {
        println("response is  $response")
        if (response != null) {
            return NetworkResponseHandler.Success(response)
        }


        return NetworkResponseHandler.Error(State.ERROR, null, null)

    }
//-------------------------------------------------------------------------------------------------

    fun saveArticle(article: Article) {


        viewModelScope.launch {

            val id = newRepository.insertArticle(article)
            if (id == -1L) {
                isItemPresent.postValue(true)
            } else {
                isItemPresent.postValue(false)
            }
            rowValue.postValue(id)


        }


    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newRepository.deleteArticle(article)
    }

    fun getSavedNews() =
        newRepository.getSavedNews()  // no need of couroutine,since it was not a suspend function.
    // we can observe live data in our fragments.

    override fun onCleared() {
        super.onCleared()
    }

}