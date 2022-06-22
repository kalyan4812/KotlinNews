package com.saikalyandaroju.kotlinnews.ui.viewmodel


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.saikalyandaroju.kotlinnews.model.repository.FakeTestNewsRepository
import com.saikalyandaroju.kotlinnews.utils.getOrAwaitValueTest
import org.junit.Before
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import com.saikalyandaroju.kotlinnews.MainCoroutineRule
import com.saikalyandaroju.kotlinnews.model.source.models.Article
import com.saikalyandaroju.kotlinnews.model.source.models.Source
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule

@ExperimentalCoroutinesApi
class TestNewsViewModel {

    private lateinit var newsViewModel: NewsViewModel

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
        newsViewModel = NewsViewModel(FakeTestNewsRepository(), SavedStateHandle())
    }


    @Test
    fun test_crud_article() {

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

        newsViewModel.saveArticle(article) //insert.


        val res = newsViewModel.getSavedNews().getOrAwaitValueTest()
        assertThat(res).contains(article)

        newsViewModel.deleteArticle(article)//delete


        val newres = newsViewModel.getSavedNews().getOrAwaitValueTest()
        assertThat(newres).doesNotContain(article)

    }

    @Test
    fun test_get_news() = runBlockingTest {
        val obj = newsViewModel.newRepository as
                FakeTestNewsRepository
        obj.setNetworkError(true)

        val res = obj.getBreakingNews("us", 1)

        //  assertThat(res.message).equals("Error")
        assertThat(res.data).isNull()

        obj.setNetworkError(false)

        val newres = obj.getBreakingNews("us", 1)

        //  assertThat(newres.message).equals("Success")
        assertThat(newres.data).isNotNull()


    }


}