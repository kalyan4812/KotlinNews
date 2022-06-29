package com.saikalyandaroju.kotlinnews.model.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.saikalyandaroju.kotlinnews.model.source.models.Article
import com.saikalyandaroju.kotlinnews.model.source.models.NewsResponse
import com.saikalyandaroju.kotlinnews.model.source.remote.NewsApi
import com.saikalyandaroju.kotlinnews.utils.Network.NetworkResponseHandler
import okhttp3.internal.wait
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

const val NETWORK_PAGE_SIZE = 10

class ArticlePagingSource(
    val newsApi: NewsApi,
    private val query: String?,
    private val countryCode: String
) :
    PagingSource<Int, Article>() {

    // the first param in Pagingsource generic is Int=page number,which is used to distinguish between pages.

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {

        val current_page = params.key ?: 1 // for first time page will be null ,so make it 1.



        return try {

            val response = newsApi.getBreakingNews(countryCode, current_page)



            val res = response.body()?.articles ?: emptyList()

            val list = mutableListOf<Article>()
            list.addAll(res)

            val nextKey = if (res.isEmpty()) {
                null
            } else {
                // initial load size = 3 * NETWORK_PAGE_SIZE
                // ensure we're not requesting duplicating items, at the 2nd request

                if (
                    params.loadSize == 3 * NETWORK_PAGE_SIZE
                ) {
                    current_page + 1
                } else {
                    current_page + (params.loadSize / NETWORK_PAGE_SIZE)
                }
            }

            return LoadResult.Page(
                data = list,
                prevKey = if (current_page == 1) null else current_page - 1,
                nextKey = nextKey
            )

        } catch (e: IOException) {
            println(e.localizedMessage)
            LoadResult.Error(e)
        } catch (e: HttpException) {
            println(e.localizedMessage)
            LoadResult.Error(e)
        }


    }


}