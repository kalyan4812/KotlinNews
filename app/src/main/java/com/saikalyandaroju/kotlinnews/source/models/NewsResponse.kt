package com.saikalyandaroju.kotlinnews.source.models

data class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)