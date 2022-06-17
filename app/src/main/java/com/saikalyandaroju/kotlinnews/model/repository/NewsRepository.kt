package com.saikalyandaroju.kotlinnews.model.repository

import android.util.Log
import com.saikalyandaroju.kotlinnews.model.source.local.ArticleDao
import com.saikalyandaroju.kotlinnews.model.source.models.Article
import com.saikalyandaroju.kotlinnews.model.source.models.NewsResponse
import com.saikalyandaroju.kotlinnews.model.source.remote.NewsApi
import retrofit2.Response


class NewsRepository(val articleDao: ArticleDao, val newsApi: NewsApi) {


    // network related.

    suspend fun getBreakingNews(countryCode: String, pagenumber: Int) =
        newsApi.getBreakingNews(countryCode, pagenumber)


    suspend fun getSearchedNews(query: String, pagenumber: Int): Response<NewsResponse> {
        Log.i("check", newsApi.toString())

        return newsApi.searchForNews(query, pagenumber)
    }

    // db related.

    suspend fun insertArticle(article: Article): Long {

        Log.i("check", articleDao.toString())

        return articleDao.insertArticle(article)
    }

    suspend fun deleteArticle(article: Article) = articleDao.deleteArticle(article)

    fun getSavedNews() = articleDao.getAllArticles()


}