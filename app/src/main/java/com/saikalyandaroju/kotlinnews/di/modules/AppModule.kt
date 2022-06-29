package com.saikalyandaroju.kotlinnews.di.modules

import android.R
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.saikalyandaroju.kotlinnews.model.adapters.NewsAdapter
import com.saikalyandaroju.kotlinnews.model.adapters.NewsPagingAdapter
import com.saikalyandaroju.kotlinnews.model.repository.GlobalNewsRepository
import com.saikalyandaroju.kotlinnews.model.repository.NewsRepository
import com.saikalyandaroju.kotlinnews.model.source.local.ArticleDao
import com.saikalyandaroju.kotlinnews.model.source.local.ArticleDatabase
import com.saikalyandaroju.kotlinnews.model.source.remote.NewsApi
import com.saikalyandaroju.kotlinnews.utils.Constants.Companion.SHARED_DB_KEY
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class AppModule {


    // activityretained component-survives on screen rotation and activity recreation.

    //provide ApiSource
    @Singleton  // newsapi will be singleton+ it will live as long as application.
    @Provides
    fun getApiSource(retrofit: Retrofit): NewsApi {
        return retrofit.create(NewsApi::class.java)
    }


    // provide db instance.
    @Singleton
    @Provides

    fun getDbDao(articleDatabase: ArticleDatabase): ArticleDao {
        return articleDatabase.getArticleDao()
    }


    // provide glide instance.
    @Singleton
    @Provides
    fun provideRequestOptions(): RequestOptions {
        return RequestOptions.placeholderOf(R.drawable.alert_dark_frame)
            .error(R.drawable.alert_dark_frame)
    }

    @Singleton
    @Provides
    fun provideGlideInstance(
        application: Application,
        requestOptions: RequestOptions
    ): RequestManager {
        return Glide.with(application).setDefaultRequestOptions(requestOptions)
    }


    @Singleton
    @Provides
    fun provideRepository(articleDao: ArticleDao, newsApi: NewsApi):GlobalNewsRepository {
        return NewsRepository(articleDao, newsApi)
    }

    @Singleton
    @Provides
    fun providePagingAdapter(requestManager: RequestManager): NewsPagingAdapter {
        return NewsPagingAdapter(requestManager)
    }

    @Singleton
    @Provides
    fun provideNormalAdapter(requestManager: RequestManager): NewsAdapter {
        return NewsAdapter(requestManager)
    }

    @Singleton
    @Provides
    fun getPreference(@ApplicationContext context:Context):SharedPreferences{
        return context.getSharedPreferences(SHARED_DB_KEY,Context.MODE_PRIVATE)
    }




}