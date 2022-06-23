package com.saikalyandaroju.kotlinnews.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.google.common.base.CharMatcher
import com.google.common.base.CharMatcher.any
import com.google.common.truth.Truth
import com.saikalyandaroju.kotlinnews.MainCoroutineRule
import com.saikalyandaroju.kotlinnews.model.repository.FakeTestNewsRepository
import com.saikalyandaroju.kotlinnews.model.repository.NewsRepository
import com.saikalyandaroju.kotlinnews.model.source.models.Article
import com.saikalyandaroju.kotlinnews.model.source.models.NewsResponse
import com.saikalyandaroju.kotlinnews.model.source.models.Source
import com.saikalyandaroju.kotlinnews.utils.Network.NetworkResponseHandler
import com.saikalyandaroju.kotlinnews.utils.getOrAwaitValueTest
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@RunWith(JUnit4::class)
class TestMockkNewsViewModel {

    @get:Rule
    var rule =
        InstantTaskExecutorRule() // tells junit to execute one function after another,not asynchronously.

    @get:Rule
    var mainCoroutineRule =
        MainCoroutineRule() // since we are in unit test folder not in android test +
    //below functions use couroutine scope to run and they relay on MainLoopDispatcher and since
    // don't have access to it here,we can to define our own junit rule.

    private lateinit var newsViewModel: NewsViewModel


    private lateinit var res: NetworkResponseHandler<NewsResponse>

    @MockK
    private lateinit var fakeTestNewsRepository: NewsRepository

    @Before
    fun setUp() {
        unmockkAll()
        clearAllMocks()
        MockKAnnotations.init(this)
        newsViewModel = NewsViewModel(fakeTestNewsRepository, SavedStateHandle())


        val k = mockk<NetworkResponseHandler<NewsResponse>>()
        coEvery {
            fakeTestNewsRepository.getBreakingNews("us", 1)
        } returns k

    }


    @After
    fun tearDown() {

    }

    @Test
    fun test_insert_delete_article() = runBlockingTest {

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
            fakeTestNewsRepository.insertArticle(article)

        } returns 8L



        newsViewModel.saveArticle(article)

        newsViewModel.rowValue.observeForever {
            Truth.assertThat(it).isEqualTo(it)
        }



        coEvery {
            newsViewModel.deleteArticle(article)
        }

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
            fakeTestNewsRepository.getSavedNews()
        } returns live

        val res = newsViewModel.getSavedNews().getOrAwaitValueTest()

        Truth.assertThat(res).isEqualTo(live.value)
        Truth.assertThat(res.size).isEqualTo(live.value?.size)
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

        res = NetworkResponseHandler.Success(NewsResponse(listOf(article), "success", 1))

        coEvery {
            fakeTestNewsRepository.getBreakingNews("us", 1)
        } returns res


        newsViewModel.getBreakingNews("us")

        newsViewModel.breakingNewsresponse.observeForever {
            val ans = it
            Truth.assertThat(ans).isEqualTo(res)
            Truth.assertThat(res.data?.articles?.size).isEqualTo(ans.data?.articles?.size)
            Truth.assertThat(ans.data?.articles?.get(0)?.author).isEqualTo("kalyan")
        }


    }
}