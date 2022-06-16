package com.saikalyandaroju.kotlinnews.utils

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.saikalyandaroju.kotlinnews.source.models.Article
import com.saikalyandaroju.kotlinnews.source.models.NewsResponse
import com.saikalyandaroju.kotlinnews.source.remote.NewsApi

class NewsPagingSource(val api:NewsApi): PagingSource<Int, Article>() {

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        TODO("Not yet implemented")
    }


}