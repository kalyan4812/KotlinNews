package com.saikalyandaroju.kotlinnews.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.saikalyandaroju.kotlinnews.R
import com.saikalyandaroju.kotlinnews.repository.NewsRepository
import com.saikalyandaroju.kotlinnews.source.local.ArticleDatabase
import com.saikalyandaroju.kotlinnews.ui.viewmodel.NewsViewModel
import com.saikalyandaroju.kotlinnews.utils.NewsViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var viewmodel:NewsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // connecting bottom nav with navigation component.
        bottomNavigationView.setupWithNavController(newsNavHostFragment.findNavController())

        setUp();

    }

    private fun setUp() {

        val newsRepository= NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory= NewsViewModelFactory(newsRepository)
        viewmodel= ViewModelProvider(this,viewModelProviderFactory).get(NewsViewModel::class.java)
    }

}