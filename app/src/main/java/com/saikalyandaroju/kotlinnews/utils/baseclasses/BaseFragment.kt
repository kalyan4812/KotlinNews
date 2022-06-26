package com.saikalyandaroju.kotlinnews.utils.baseclasses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel


abstract class BaseFragment<V : ViewModel> : Fragment() {


   // private val viewmodel: NewsViewModel by viewModels()

    @LayoutRes
    abstract fun getLayoutId(): Int
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
    }

    fun setUpViewModel() {


        /* val newsRepository=NewsRepository(ArticleDatabase(this.requireContext()))
         val viewModelProviderFactory=NewsViewModelFactory(newsRepository)
         viewmodel=ViewModelProvider(this,viewModelProviderFactory).get(NewsViewModel::class.java) as V*/


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

    /*open fun getViewModel(): V {
        return viewmodel as V
    }*/

    abstract fun onViewReady(view: View?, savedStateInstance: Bundle?, arguments: Bundle?)

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}