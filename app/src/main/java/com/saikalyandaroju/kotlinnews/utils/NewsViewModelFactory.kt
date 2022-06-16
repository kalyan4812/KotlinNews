package com.saikalyandaroju.kotlinnews.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.saikalyandaroju.kotlinnews.repository.NewsRepository
import com.saikalyandaroju.kotlinnews.ui.viewmodel.NewsViewModel

class NewsViewModelFactory(val newsRepository: NewsRepository) : ViewModelProvider.Factory {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewsViewModel(newsRepository) as T
    }
}