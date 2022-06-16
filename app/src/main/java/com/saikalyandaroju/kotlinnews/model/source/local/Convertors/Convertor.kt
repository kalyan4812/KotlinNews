package com.saikalyandaroju.kotlinnews.model.source.local.Convertors

import androidx.room.TypeConverter
import com.saikalyandaroju.kotlinnews.model.source.models.Source

class Convertor {


    @TypeConverter
    fun fromSource(source: Source): String {
        return source.name
    }

    @TypeConverter
    fun toSource(name: String): Source {
        return Source(name,name)
    }
}