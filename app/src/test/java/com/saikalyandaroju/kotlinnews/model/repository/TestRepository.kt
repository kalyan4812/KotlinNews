package com.saikalyandaroju.kotlinnews.model.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth
import com.saikalyandaroju.kotlinnews.MainCoroutineRule
import com.saikalyandaroju.kotlinnews.model.source.local.ArticleDao
import com.saikalyandaroju.kotlinnews.model.source.models.Article
import com.saikalyandaroju.kotlinnews.model.source.models.NewsResponse
import com.saikalyandaroju.kotlinnews.model.source.models.Source
import com.saikalyandaroju.kotlinnews.model.source.remote.NewsApi
import com.saikalyandaroju.kotlinnews.utils.Network.NetworkResponseHandler
import com.saikalyandaroju.kotlinnews.utils.getOrAwaitValueTest
import com.squareup.okhttp.Response
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@ExperimentalCoroutinesApi
//@RunWith(InstantExecutorExtension::class.java)
class TestRepository {


    private lateinit var repository: NewsRepository

    @MockK
    private lateinit var articleDao: ArticleDao

    @MockK
    private lateinit var newsApi: NewsApi

    @get:Rule
    var rule =
        InstantTaskExecutorRule() // tells junit to execute one function after another,not asynchronously.

    @get:Rule
    var mainCoroutineRule =
        MainCoroutineRule() // since we are in unit test folder not in android test +
    //below functions use couroutine scope to run and they relay on MainLoopDispatcher and since
    // don't have access to it here,we can to define our own junit rule.

    @Before
    fun setUp() {
        unmockkAll()
        clearAllMocks()
        MockKAnnotations.init(this, relaxUnitFun = true)
        repository = NewsRepository(articleDao,newsApi)
    }

    @Test
    fun test_getNews() = runBlockingTest {

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

        val res =retrofit2.Response.success(NewsResponse(listOf(article), "success", 1))

        coEvery {
            newsApi.getBreakingNews("us", 1)
        } returns res


        val ans = repository.getBreakingNews("us", 1)
        println(ans.data?.articles)
        Truth.assertThat(ans.data?.articles).isEqualTo(res.body()?.articles)
        Truth.assertThat(res.body()?.articles?.size).isEqualTo(ans.data?.articles?.size)
        Truth.assertThat(ans.data?.articles?.get(0)?.author).isEqualTo("kalyan")



    }


    @Test
    fun test_insert_delete() = runBlockingTest {
        val article = mockk<Article>()
        coEvery {
            articleDao.insertArticle(article)

        } returns 1L

        val res = repository.insertArticle(article)

        Truth.assertThat(res).isEqualTo(1L)

        coEvery {
            repository.deleteArticle(article)
        } just runs


    }

    @Test
    fun test_read() = runBlockingTest {
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
        val live = MutableLiveData<List<Article>>(listOf(article))
        coEvery {
            articleDao.getAllArticles()
        } returns live

        val res = repository.getSavedNews().getOrAwaitValueTest()

        Truth.assertThat(res).isEqualTo(live.value)
        Truth.assertThat(res.size).isEqualTo(live.value?.size)


    }


}