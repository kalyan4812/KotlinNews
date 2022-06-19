package com.saikalyandaroju.kotlinnews.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.saikalyandaroju.kotlinnews.model.source.models.Article
import com.saikalyandaroju.kotlinnews.model.source.models.NewsResponse
import com.saikalyandaroju.kotlinnews.utils.Network.NetworkResponseHandler
import okhttp3.internal.EMPTY_RESPONSE
import retrofit2.Response

class FakeTestNewsRepository : GlobalNewsRepository {
    // simulates the behaviour of news repository.


    private val articles = mutableListOf<Article>()
    private val observableArticles = MutableLiveData<List<Article>>(articles)

    private var networkError = false

    fun setNetworkError(b: Boolean) {
        networkError = b
    }

    fun refreshLiveArticles() {
        observableArticles.postValue(articles)
    }


    override suspend fun getBreakingNews(
        countryCode: String,
        pagenumber: Int
    ): NetworkResponseHandler<NewsResponse> {

        if (networkError) {
            return NetworkResponseHandler.Error("Error", null)

        } else {
            return NetworkResponseHandler.Success(NewsResponse(listOf(), "Success", 0))
        }

    }

    override suspend fun getSearchedNews(
        query: String,
        pagenumber: Int
    ): NetworkResponseHandler<NewsResponse> {
        if (networkError) {
            return NetworkResponseHandler.Error("Error", null)

        } else {
            return NetworkResponseHandler.Success(NewsResponse(listOf(), "Success", 0))
        }
    }

    override suspend fun insertArticle(article: Article): Long {
        articles.add(article)
        refreshLiveArticles()
        return 0L
    }

    override suspend fun deleteArticle(article: Article) {
        articles.remove(article)
        refreshLiveArticles()

    }

    override fun getSavedNews(): LiveData<List<Article>> {
        return observableArticles
    }
}