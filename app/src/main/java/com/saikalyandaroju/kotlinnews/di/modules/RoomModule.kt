package com.saikalyandaroju.kotlinnews.di.modules

import android.content.Context
import androidx.room.Room
import com.saikalyandaroju.kotlinnews.model.source.local.ArticleDatabase
import com.saikalyandaroju.kotlinnews.utils.Constants.Companion.DATABASENAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)

@Module
class RoomModule {


    @Singleton
    @Provides
    fun provideDbName(): String {
        return DATABASENAME
    }

    @Singleton
    @Provides
    fun provideDatabase(name: String, @ApplicationContext context: Context):ArticleDatabase {
        return Room.databaseBuilder(context, ArticleDatabase::class.java, name)
            .fallbackToDestructiveMigration().build()
    }


}