package com.saikalyandaroju.kotlinnews.model.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.saikalyandaroju.kotlinnews.model.source.models.Article
import com.saikalyandaroju.kotlinnews.model.source.models.Source
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@ExperimentalCoroutinesApi
class TestRepository {


    private lateinit var repository: NewsRepository

    @get:Rule
    var rule =
        InstantTaskExecutorRule() // tells junit to execute one function after another,not asynchronously.

    //MockK includes a rule which uses this to set up and tear
    // down your mocks without needing to manually call
 //   @get:Rule
   // val mockkRule = MockKRule(this)

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        repository = mockk()
    }

    @Test
    fun test_getNews()= runBlockingTest {

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
            repository.getBreakingNews("us", 1).data?.articles
        } returns listOf(article)

        coVerify {
            val res=repository.getBreakingNews("us",1).data?.articles
            res?.contains(article)
        }

    }

    @Test
    fun test_insert_delete()= runBlockingTest {
        val article = mockk<Article>()
        coEvery {
            repository.articleDao.insertArticle(article)
        } returns 1L

        coEvery {
            repository.articleDao.deleteArticle(article)
        } just runs

    }

  /*  @Test
    fun test_read()= runBlockingTest {
        val article = mockk<Article>()
        coEvery {
            repository.articleDao.getAllArticles().getOrAwaitValueTest()
        } returns listOf(article)


    }*/


}