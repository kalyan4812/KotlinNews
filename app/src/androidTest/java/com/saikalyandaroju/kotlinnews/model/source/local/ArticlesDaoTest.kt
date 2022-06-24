package com.saikalyandaroju.kotlinnews.model.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth
import com.saikalyandaroju.kotlinnews.MainCoroutineRule
import com.saikalyandaroju.kotlinnews.Utils.getOrAwaitValue
import com.saikalyandaroju.kotlinnews.model.source.models.Article
import com.saikalyandaroju.kotlinnews.model.source.models.Source
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
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
    val rule = InstantTaskExecutorRule()


    private lateinit var articleDao: ArticleDao
    private lateinit var articleDatabase: ArticleDatabase

    //MockK includes a rule which uses this to set up and tear
    // down your mocks without needing to manually call
    //   @get:Rule
    // val mockkRule = MockKRule(this)

    @Before
    fun setUp() {
        unmockkAll()
        clearAllMocks()
        MockKAnnotations.init(this)
        articleDatabase = mockk()
        articleDao = mockk()
        every { articleDatabase.getArticleDao() } returns articleDao
        every { articleDatabase.close() } just runs

    }


    @After
    fun tearDown() {
        articleDatabase.close()
    }


    @Test
    fun test_article_insert_delete() = runBlockingTest {


        val article: Article = mockk()



        coEvery {
            articleDao.insertArticle(article)
        } returns 1L

        val ans = articleDao.insertArticle(article)
        Truth.assertThat(ans).isEqualTo(1L)


        coEvery {
            articleDao.deleteArticle(article)
        } just runs


    }

    @Test
    fun test_read_article() = runBlockingTest {

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
        val list = MutableLiveData<List<Article>>(listOf(article))
        coEvery {
            articleDao.getAllArticles()
        } returns list


        val res = articleDao.getAllArticles().getOrAwaitValue()

        Truth.assertThat(res).contains(article)
        Truth.assertThat(res.size).isEqualTo(1)


    }


}