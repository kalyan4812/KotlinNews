package com.saikalyandaroju.kotlinnews.model.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.saikalyandaroju.kotlinnews.Utils.getOrAwaitValue
import com.saikalyandaroju.kotlinnews.model.source.models.Article
import com.saikalyandaroju.kotlinnews.model.source.models.Source
import io.mockk.*
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
class ArticlesDaoTest {

    @get:Rule
    var rule =
        InstantTaskExecutorRule() // tells junit to execute one function after another,not asynchronously.


    private lateinit var articleDao: ArticleDao
    private lateinit var articleDatabase: ArticleDatabase

    //MockK includes a rule which uses this to set up and tear
    // down your mocks without needing to manually call
 //   @get:Rule
   // val mockkRule = MockKRule(this)

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        articleDatabase = mockk()
        articleDao = mockk()
        every { articleDatabase.getArticleDao() } returns articleDao


    }

    @After
    fun tearDown() {
        articleDatabase.close()
    }


    @Test
    fun test_article_insert_delete()= runBlockingTest {


        val article: Article = mockk()



        coEvery {
            articleDao.insertArticle(article)
        } returns 1L

        coVerify {
            articleDao.insertArticle(article) == 1L

        }

        coEvery {
            articleDao.deleteArticle(article)
        } just runs


    }

    @Test
    fun test_read_article()= runBlockingTest {

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
        coEvery {
            articleDao.getAllArticles().getOrAwaitValue()
        } returns listOf(article)

        coVerify {
            val list = articleDao.getAllArticles().getOrAwaitValue()
            list.contains(article)
            list.size == 1
        }

    }


}