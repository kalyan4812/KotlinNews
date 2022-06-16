package com.saikalyandaroju.kotlinnews.model.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.saikalyandaroju.kotlinnews.R
import com.saikalyandaroju.kotlinnews.model.source.models.Article
import kotlinx.android.synthetic.main.item_article.view.*


class NewsAdapter(var requestManager: RequestManager) : RecyclerView.Adapter<NewsAdapter.NewsHolder>() {

    private lateinit var onClickListener: ClickListener



    //DiffUtil calculates differrnces b/w old list and new list ,only updates items which are changed.
    //And it also happen in background so no blockage of main thread.

    private val callback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url; // urls are unique
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

    }

    private val differ =
        AsyncListDiffer(this, callback) // runs asynchronously,calculates diff b/w old and new list.


    fun setList(articles: List<Article>) {
        differ.submitList(articles)
    }

    fun getList(): List<Article> {
        return differ.currentList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsHolder {
        return NewsHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NewsHolder, position: Int) {
        val article = differ.currentList[position]
        holder.itemView.apply {
            //Glide.with(holder.itemView.getContext()).load(article.urlToImage)


            requestManager.load(article.urlToImage).placeholder(R.drawable.placeholder).error(R.drawable.placeholder)
                .into(ivArticleImage);
            tvSource.setText(article.title);

            tvDescription.setText(article.description);
            if (article.source != null) {
                tvSource.setText(article.source.name);
            }



            setOnClickListener(View.OnClickListener {
                val pos = position
                if (onClickListener != null && pos != -1) {
                    onClickListener.onClick(differ.getCurrentList().get(pos));
                }
            })
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class NewsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {



    }

    interface ClickListener {
        fun onClick(article: Article?)
    }

    fun setOnClickListener(clickListener: ClickListener) {
        this.onClickListener = clickListener
    }
}