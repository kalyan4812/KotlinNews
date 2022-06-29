package com.saikalyandaroju.kotlinnews.model.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.saikalyandaroju.kotlinnews.R
import com.saikalyandaroju.kotlinnews.model.source.models.Article
import com.saikalyandaroju.kotlinnews.model.source.models.NewsResponse
import kotlinx.android.synthetic.main.item_article.view.*

class NewsPagingAdapter(var requestManager: RequestManager) :
    PagingDataAdapter<Article, NewsPagingAdapter.MyViewHolder>(callback) {
    private lateinit var onClickListener: ClickListener

    companion object {
        private val callback = object : DiffUtil.ItemCallback<Article>() {


            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem.url == newItem.url
            }

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem == newItem
            }

        }


    }

    public class MyViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val article = getItem(position)!!



        holder.itemView.apply {
            //Glide.with(holder.itemView.getContext()).load(article.urlToImage)


            requestManager.load(article.urlToImage).placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(ivArticleImage);
            tvSource.setText(article.title);

            tvDescription.setText(article.description);
            if (article.source != null) {
                tvSource.setText(article.source.name);
            }



            setOnClickListener(View.OnClickListener {
                val pos = position
                if (onClickListener != null && pos != -1) {
                    onClickListener.onClick(article);
                }
            })
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
        )
    }

    interface ClickListener {
        fun onClick(article: Article?)
    }

    fun setOnClickListener(clickListener: ClickListener) {
        this.onClickListener = clickListener
    }


}