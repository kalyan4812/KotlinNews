package com.saikalyandaroju.kotlinnews.model.source.local

import androidx.room.*
import com.saikalyandaroju.kotlinnews.model.source.local.Convertors.Convertor
import com.saikalyandaroju.kotlinnews.model.source.models.Article

@Database(entities = [Article::class], version = 1)
@TypeConverters(Convertor::class)
abstract class ArticleDatabase : RoomDatabase() {
    abstract fun getArticleDao(): ArticleDao

   /* companion object {

        @Volatile
        private var articleDatabase: ArticleDatabase? = null
        private val LOCK = Any()


        operator fun invoke(context: Context) =
            articleDatabase ?: synchronized(LOCK) {
                articleDatabase ?: getDatabaseObject(context).also { articleDatabase = it }
            }

        // invoke will be called automatically,
        /*
          working of invoke
           * return articledb instance if it is not null
           * if null ,use double locking and return db object,and set the db object ,so that it can be used later.
         */

        // when we write ArticleDatabase(),invoke function will be called.

        fun getDatabaseObject(context: Context): ArticleDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                DATABASENAME
            ).build()
        }
    }*/
}