package com.saikalyandaroju.kotlinnews.source.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.saikalyandaroju.kotlinnews.source.models.Article


@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: Article): Long  // returns id of inserted article.

    @Delete
    suspend fun deleteArticle(article: Article?)

    @Query("SELECT * FROM articles")
    fun getAllArticles(): LiveData<List<Article?>?>?  // it is not a suspended function,since it is a live data.live data won't work with suspend functions.
}