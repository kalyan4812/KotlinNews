package com.saikalyandaroju.kotlinnews.model.source.local

import android.database.sqlite.SQLiteConstraintException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.saikalyandaroju.kotlinnews.Utils.getOrAwaitValue
import com.saikalyandaroju.kotlinnews.model.source.models.Article
import com.saikalyandaroju.kotlinnews.model.source.models.Source
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class ArticleDaoTest {


    @get:Rule
    var rule =
        InstantTaskExecutorRule() // tells junit to execute one function after another,not asynchronously.


    private lateinit var articleDao: ArticleDao
    private lateinit var articleDatabase: ArticleDatabase

    @Before
    fun setUp() {
        articleDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), ArticleDatabase::class.java
        ).allowMainThreadQueries().build()
        // in memorydatabse-store in ram.

        // we use main thread/single thread beacuse multiple threads may effect the outcome and also we
        // complete independency between the test cases.

        articleDao = articleDatabase.getArticleDao()


    }

    @After
    fun tearDown() {
        articleDatabase.close()
    }


    @Test
    fun test_article_insert_delete() =

        // runblocking - for executing couroutine in main thread.

        runBlockingTest {
            val source = Source("India news", "India news")
            val article = Article(
                "kalyan",
                "hello...",
                "message",
                "india news",
                source,
                "Breaking news",
                "http//:url",
                "http//:urlimage"
            )

            articleDao.insertArticle(article) // insert.


            // val allArticles=articleDao.getAllArticles() --> it returns live data ,and it runs asynchronously and we don't want it.
            // so we use a google provied util file to do it.

            val allArticles: List<Article> = articleDao.getAllArticles().getOrAwaitValue()

            assertThat(allArticles).contains(article) // check if list contains inserted article.


            articleDao.deleteArticle(article) // delete.

            val new_articles = articleDao.getAllArticles().getOrAwaitValue() //read.
            assertThat(new_articles).doesNotContain(article)

        }


    /*  @Test(expected = SQLiteConstraintException::class)
      @Throws(Exception::class)
      fun insert_emptyUrl_throwSQLiteConstraintException() = runBlockingTest{
          val source = Source("India news", "India news")
          val article = Article(
              "kalyan",
              "hello...",
              "message",
              "india news",
              source,
              "Breaking news",
              "",
              "http//:urlimage"
          )

          // insert
          articleDao.insertArticle(article)
      }*/


}