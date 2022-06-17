package com.saikalyandaroju.kotlinnews.model.source.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.saikalyandaroju.kotlinnews.model.source.models.Article


@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArticle(article: Article): Long  // returns id of inserted article.

    @Delete
    suspend fun deleteArticle(article: Article?)

    @Query("SELECT * FROM articles")
    fun getAllArticles(): LiveData<List<Article?>?>?  // it is not a suspended function,since it is a live data.live data won't work with suspend functions.
}