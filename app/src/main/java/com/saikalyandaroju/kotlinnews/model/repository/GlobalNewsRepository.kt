package com.saikalyandaroju.kotlinnews.model.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.saikalyandaroju.kotlinnews.model.source.models.Article
import com.saikalyandaroju.kotlinnews.model.source.models.NewsResponse
import com.saikalyandaroju.kotlinnews.utils.Network.NetworkResponseHandler
import retrofit2.Response

interface GlobalNewsRepository {

    suspend fun getBreakingNews(countryCode: String, pagenumber: Int): LiveData<PagingData<Article>>


    suspend fun getSearchedNews(query: String, pagenumber: Int): NetworkResponseHandler<NewsResponse>

    // db related.

    suspend fun insertArticle(article: Article): Long

    suspend fun deleteArticle(article: Article)

    fun getSavedNews(): LiveData<List<Article>>
}