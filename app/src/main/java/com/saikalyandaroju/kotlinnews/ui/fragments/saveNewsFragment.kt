package com.saikalyandaroju.kotlinnews.ui.fragments

import android.graphics.*
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.saikalyandaroju.kotlinnews.R
import com.saikalyandaroju.kotlinnews.adapters.NewsAdapter
import com.saikalyandaroju.kotlinnews.baseclasses.BaseFragment
import com.saikalyandaroju.kotlinnews.source.models.Article
import com.saikalyandaroju.kotlinnews.ui.viewmodel.NewsViewModel
import kotlinx.android.synthetic.main.fragment_save_news.*


class saveNewsFragment : BaseFragment<NewsViewModel>() {

    lateinit var newsAdapter: NewsAdapter

    lateinit var callback: ItemTouchHelper.Callback

    override fun getLayoutId(): Int {
        return R.layout.fragment_save_news
    }

    override fun onViewReady(view: View?, savedStateInstance: Bundle?, arguments: Bundle?) {
        initSwiper(view);
        initRecyclerView(view)

        setUpListeners()

        subscribeToObservers()


    }

    private fun initSwiper(view: View?) {
        callback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition
                val article = newsAdapter.getList().get(pos)
                getViewModel()?.deleteArticle(article)

                Snackbar.make(view!!, "Deleted Successfully", Snackbar.LENGTH_SHORT).setAction(
                    "Undo"
                ) { getViewModel()?.saveArticle(article) }.show()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    val view = viewHolder.itemView
                    val p = Paint()
                    val bitmap:Bitmap
                    //dx>0 swipe left to right

                    if (dX < 0) {
                        bitmap =
                            BitmapFactory.decodeResource(resources, R.drawable.ic_delete_white_png)
                        p.setColor(Color.RED)
                        c.drawRect(
                            view.right + dX, view.top.toFloat(),
                            view.right.toFloat(), view.bottom.toFloat(), p
                        )

                        c.drawBitmap(
                            bitmap,
                            view.left.toFloat(),
                            view.top.toFloat() + (view.bottom.toFloat() - view.top.toFloat() - bitmap.height.toFloat()) / 2,
                            p
                        )
                    } else {
                        bitmap =
                            BitmapFactory.decodeResource(resources, R.drawable.ic_delete_white_png)
                        p.setColor(Color.GREEN)
                        c.drawRect(
                            view.left.toFloat(),
                            view.top.toFloat(), view.left + dX, view.bottom.toFloat(), p
                        )

                        c.drawBitmap(
                            bitmap,
                            view.left.toFloat(),
                            view.top.toFloat() + (view.bottom.toFloat() - view.top.toFloat() - bitmap.height) / 2,
                            p
                        )
                    }
                    viewHolder.itemView.translationX = dX

                } else {
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }

            }

        }

    }

    private fun subscribeToObservers() {
        getViewModel()?.getSavedNews()?.observe(viewLifecycleOwner, Observer { articles ->

            newsAdapter.setList(articles as List<Article>)

        })
    }

    private fun setUpListeners() {
        newsAdapter.setOnClickListener(object : NewsAdapter.ClickListener {
            override fun onClick(article: Article?) {
                val bundle = Bundle()
                bundle.apply {
                    putSerializable("article", article)
                }

                findNavController().navigate(
                    R.id.action_saveNewsFragment_to_articleFragment,
                    bundle
                )
            }

        })
    }

    private fun initRecyclerView(view: View?) {
        newsAdapter = NewsAdapter()
        rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }

       ItemTouchHelper(callback).attachToRecyclerView(rvSavedNews)
    }

}