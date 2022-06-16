package com.saikalyandaroju.kotlinnews.repository

import com.saikalyandaroju.kotlinnews.source.local.ArticleDatabase
import com.saikalyandaroju.kotlinnews.source.models.Article
import com.saikalyandaroju.kotlinnews.source.remote.ServiceProvider

class NewsRepository(val articleDb: ArticleDatabase) {


    // network related.

    suspend fun getBreakingNews(countryCode: String, pagenumber: Int) =
        ServiceProvider.requestApi.getBreakingNews(countryCode, pagenumber)


    suspend fun getSearchedNews(query: String, pagenumber: Int) =
        ServiceProvider.requestApi.searchForNews(query, pagenumber)


    // db related.

    suspend fun insertArticle(article: Article) = articleDb.getArticleDao().insertArticle(article)

    suspend fun deleteArticle(article: Article) = articleDb.getArticleDao().deleteArticle(article)

    fun getSavedNews() = articleDb.getArticleDao().getAllArticles()


}