package com.saikalyandaroju.kotlinnews.model.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.paging.*
import com.saikalyandaroju.kotlinnews.model.paging.ArticlePagingSource
import com.saikalyandaroju.kotlinnews.model.source.local.ArticleDao
import com.saikalyandaroju.kotlinnews.model.source.models.Article
import com.saikalyandaroju.kotlinnews.model.source.models.NewsResponse
import com.saikalyandaroju.kotlinnews.model.source.remote.NewsApi
import com.saikalyandaroju.kotlinnews.utils.Network.NetworkResponseHandler
import com.saikalyandaroju.kotlinnews.utils.Network.State


class NewsRepository(val articleDao: ArticleDao, val newsApi: NewsApi) : GlobalNewsRepository {


    // network related.

    override suspend fun getSearchedNews(
        query: String,
        pagenumber: Int
    ): LiveData<PagingData<Article>> {


        return Pager(
            config = PagingConfig(
                pageSize = 10,
                maxSize = 30, enablePlaceholders = false
            ), pagingSourceFactory = { ArticlePagingSource(newsApi, query, "us") }

        ).liveData

    }


    override suspend fun getBreakingNews(
        countryCode: String,
        pagenumber: Int
    ): LiveData<PagingData<Article>> {
        println("called.................................")
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                maxSize = 30, enablePlaceholders = false
            ), pagingSourceFactory = { ArticlePagingSource(newsApi, null, countryCode) }

        ).liveData


    }


    // db related.

    override suspend fun insertArticle(article: Article): Long {

        // Log.i("check", articleDao.toString())

        return articleDao.insertArticle(article)
    }

    override suspend fun deleteArticle(article: Article) = articleDao.deleteArticle(article)

    override fun getSavedNews() = articleDao.getAllArticles()


}