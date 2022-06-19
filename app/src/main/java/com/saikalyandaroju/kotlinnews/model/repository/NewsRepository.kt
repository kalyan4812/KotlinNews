package com.saikalyandaroju.kotlinnews.model.repository

import android.util.Log
import com.saikalyandaroju.kotlinnews.model.source.local.ArticleDao
import com.saikalyandaroju.kotlinnews.model.source.models.Article
import com.saikalyandaroju.kotlinnews.model.source.models.NewsResponse
import com.saikalyandaroju.kotlinnews.model.source.remote.NewsApi
import com.saikalyandaroju.kotlinnews.utils.Network.NetworkResponseHandler
import retrofit2.Response


class NewsRepository(val articleDao: ArticleDao, val newsApi: NewsApi) : GlobalNewsRepository {


    // network related.

    override suspend fun getBreakingNews(
        countryCode: String,
        pagenumber: Int
    ): NetworkResponseHandler<NewsResponse> {
        newsApi.getBreakingNews(countryCode, pagenumber).body()?.let {
            return NetworkResponseHandler.Success(it)
        }

        return NetworkResponseHandler.Error("Error", null)

    }

    override suspend fun getSearchedNews(
        query: String,
        pagenumber: Int
    ): NetworkResponseHandler<NewsResponse> {
        Log.i("check", newsApi.toString())

        newsApi.searchForNews(query, pagenumber).body()?.let {
            return NetworkResponseHandler.Success(it)
        }

        return NetworkResponseHandler.Error("Error", null)

    }

    // db related.

    override suspend fun insertArticle(article: Article): Long {

        Log.i("check", articleDao.toString())

        return articleDao.insertArticle(article)
    }

    override suspend fun deleteArticle(article: Article) = articleDao.deleteArticle(article)

    override fun getSavedNews() = articleDao.getAllArticles()


}