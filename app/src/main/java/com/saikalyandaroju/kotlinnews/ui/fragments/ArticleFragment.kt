package com.saikalyandaroju.kotlinnews.ui.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.saikalyandaroju.kotlinnews.R
import com.saikalyandaroju.kotlinnews.utils.baseclasses.BaseFragment
import com.saikalyandaroju.kotlinnews.model.source.models.Article
import com.saikalyandaroju.kotlinnews.ui.viewmodel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_article.*


@AndroidEntryPoint
class ArticleFragment : BaseFragment<NewsViewModel>() {

    val args: ArticleFragmentArgs by navArgs() // generated by navigation component.

    val viewModel: NewsViewModel by viewModels()

    override fun getLayoutId(): Int {
        return R.layout.fragment_article
    }


    override fun onViewReady(view: View?, savedStateInstance: Bundle?, arguments: Bundle?) {


        val article = args.article

        setUpWebView(article)

        setUpListeners(article)

        viewModel.isItemPresent.observe(viewLifecycleOwner, Observer {
            if (it) {
                Snackbar.make(requireView(), "Article exists already..", Snackbar.LENGTH_SHORT)
                    .show()
            } else {
                Snackbar.make(requireView(), "Article was saved..", Snackbar.LENGTH_SHORT).show()
            }
        })


    }

    private fun setUpListeners(article: Article) {
        fab.setOnClickListener {

            viewModel.saveArticle(article)


        }
    }

    private fun setUpWebView(article: Article) {

        webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url)
        }

    }

}