package com.saikalyandaroju.kotlinnews.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.saikalyandaroju.kotlinnews.R
import com.saikalyandaroju.kotlinnews.model.adapters.NewsAdapter
import com.saikalyandaroju.kotlinnews.model.adapters.NewsPagingAdapter
import com.saikalyandaroju.kotlinnews.utils.baseclasses.BaseFragment
import com.saikalyandaroju.kotlinnews.model.source.models.Article
import com.saikalyandaroju.kotlinnews.ui.viewmodel.NewsViewModel
import com.saikalyandaroju.kotlinnews.utils.Network.NetworkResponseHandler
import com.saikalyandaroju.kotlinnews.utils.StartSnapHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_news.paginationProgressBar
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.android.synthetic.main.shimmer_holder.*
import kotlinx.android.synthetic.main.shimmer_holder.view.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class searchNewsFragment : BaseFragment<NewsViewModel>() {
    private val TAG = "searchNewsFragment"

    @Inject
    lateinit var pagingAdapter: NewsPagingAdapter

    val viewModel: NewsViewModel by viewModels()

    override fun getLayoutId(): Int {
        return R.layout.fragment_search_news
    }

    override fun onViewReady(view: View?, savedStateInstance: Bundle?, arguments: Bundle?) {
        holder.shimmerFrameLayout.startShimmer()
        holder.shimmerFrameLayout.setVisibility(View.VISIBLE)

        initRecyclerView(view)

        enhancedSearch()

        subscribeToObservers()

        pagingAdapter.setOnClickListener(object : NewsPagingAdapter.ClickListener {
            override fun onClick(article: Article?) {
                val bundle = Bundle()
                bundle.apply {
                    putSerializable("article", article)
                }

                findNavController().navigate(
                    R.id.action_searchNewsFragment_to_articleFragment,
                    bundle
                )
            }

        })

    }

    private fun enhancedSearch() {
        // implementing/adding delay to avoid uneccessary network calls.
        //

        var job: Job? = null


        etSearch.addTextChangedListener { query ->
            job?.cancel()   // whenever we type something cancel our current job.
            job = MainScope().launch {
                delay(500L)
                query?.let {
                    if (query.toString().isNotEmpty()) {
                        viewModel.getSearchNews(query.toString())
                    } else {

                    }
                }
            }
        }
    }

    private fun subscribeToObservers() {
        viewModel.searchNewsresponse.observe(viewLifecycleOwner, Observer { response ->

            when (response) {
                is NetworkResponseHandler.Success -> {

                    hideProgressBar()

                    response.data?.let { newsresponse ->
                        pagingAdapter.submitData(lifecycle, newsresponse)
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

        holder.shimmerFrameLayout.stopShimmer()
        holder.shimmerFrameLayout.setVisibility(View.GONE)
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun initRecyclerView(view: View?) {
        // this snaphelper makes recycler items always full visible/show views so that
        // they are fully visible not partial at start when you scroll.
        val snapHelper =StartSnapHelper()

        rvSearchNews.apply {
            adapter = pagingAdapter
            layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)

        }

        snapHelper.attachToRecyclerView(rvSearchNews)
    }

}