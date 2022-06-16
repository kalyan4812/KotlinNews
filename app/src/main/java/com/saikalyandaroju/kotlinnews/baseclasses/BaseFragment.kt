package com.saikalyandaroju.kotlinnews.baseclasses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.saikalyandaroju.kotlinnews.repository.NewsRepository
import com.saikalyandaroju.kotlinnews.source.local.ArticleDatabase
import com.saikalyandaroju.kotlinnews.ui.viewmodel.NewsViewModel
import com.saikalyandaroju.kotlinnews.utils.NewsViewModelFactory


abstract class BaseFragment<V : ViewModel> : Fragment() {
    private lateinit var  viewmodel: V

    @LayoutRes
    abstract fun getLayoutId(): Int
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
    }

     fun setUpViewModel(){
         val newsRepository=NewsRepository(ArticleDatabase(this.requireContext()))
         val viewModelProviderFactory=NewsViewModelFactory(newsRepository)
         viewmodel=ViewModelProvider(this,viewModelProviderFactory).get(NewsViewModel::class.java) as V
     }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayoutId(), container, false);
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewReady(view, savedInstanceState, arguments)
    }

    open fun getViewModel(): V? {
        return viewmodel
    }

    abstract fun onViewReady(view: View?, savedStateInstance: Bundle?, arguments: Bundle?)
}