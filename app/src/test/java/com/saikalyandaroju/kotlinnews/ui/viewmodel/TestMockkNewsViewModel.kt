package com.saikalyandaroju.kotlinnews.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.saikalyandaroju.kotlinnews.MainCoroutineRule
import com.saikalyandaroju.kotlinnews.model.repository.FakeTestNewsRepository
import com.saikalyandaroju.kotlinnews.model.repository.NewsRepository
import com.saikalyandaroju.kotlinnews.model.source.models.Article
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
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

    @MockK
    private lateinit var fakeTestNewsRepository:NewsRepository

    @Before
    fun setUp(){
        MockKAnnotations.init(this)
        newsViewModel= NewsViewModel(fakeTestNewsRepository, SavedStateHandle())

    }


    @After
    fun tearDown(){

    }

    @Test
    fun test_insert_delete_article()= runBlockingTest{

        coEvery {
            newsViewModel.getBreakingNews("us")
        }

        coVerify{
           // newsViewModel.saveArticle(article)
        }
    }
}