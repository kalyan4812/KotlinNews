package com.saikalyandaroju.kotlinnews.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.saikalyandaroju.kotlinnews.R
import com.saikalyandaroju.kotlinnews.model.adapters.NewsAdapter
import com.saikalyandaroju.kotlinnews.utils.baseclasses.BaseFragment
import com.saikalyandaroju.kotlinnews.model.source.models.Article
import com.saikalyandaroju.kotlinnews.ui.viewmodel.NewsViewModel
import com.saikalyandaroju.kotlinnews.utils.Network.NetworkResponseHandler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_news.*
import javax.inject.Inject

@AndroidEntryPoint
class NewsFragment : BaseFragment<NewsViewModel>() {

    val viewModel: NewsViewModel by viewModels()
    private val TAG = "NewsFragment"

    @Inject
    lateinit var newsAdapter: NewsAdapter


    override fun getLayoutId(): Int {
        return R.layout.fragment_news
    }

    override fun onViewReady(view: View?, savedStateInstance: Bundle?, arguments: Bundle?) {
        initRecyclerView(view)
        subscribeToObservers()

        newsAdapter.setOnClickListener(object : NewsAdapter.ClickListener {
            override fun onClick(article: Article?) {
                val bundle = Bundle()
                bundle.apply {
                    putSerializable("article", article)
                }

                findNavController().navigate(R.id.action_newsFragment_to_articleFragment, bundle)
            }

        })

    }


    private fun subscribeToObservers() {
        viewModel.breakingNewsresponse?.observe(viewLifecycleOwner, Observer { response ->

            when (response) {
                is NetworkResponseHandler.Success -> {

                    hideProgressBar()

                    response.data?.let { newsresponse ->
                        newsAdapter.setList(newsresponse.articles)
                    }
                }
                is NetworkResponseHandler.Error -> {
                    hideProgressBar()
                    response.message?.let {
                        Log.d(TAG, it)
                    }
                }
                is NetworkResponseHandler.Loading -> {
                    showProgressBar()
                }
            }

        })
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun initRecyclerView(view: View?) {

        rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }


    }

}