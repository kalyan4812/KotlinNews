package com.saikalyandaroju.kotlinnews.ui.fragments

import android.graphics.*
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.*
import com.google.android.material.snackbar.Snackbar
import com.saikalyandaroju.kotlinnews.R
import com.saikalyandaroju.kotlinnews.model.adapters.NewsAdapter
import com.saikalyandaroju.kotlinnews.model.adapters.NewsPagingAdapter
import com.saikalyandaroju.kotlinnews.utils.baseclasses.BaseFragment
import com.saikalyandaroju.kotlinnews.model.source.models.Article
import com.saikalyandaroju.kotlinnews.ui.viewmodel.NewsViewModel
import com.saikalyandaroju.kotlinnews.utils.StartSnapHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_save_news.*
import kotlinx.android.synthetic.main.shimmer_holder.*
import javax.inject.Inject

@AndroidEntryPoint
class saveNewsFragment : BaseFragment<NewsViewModel>() {

    @Inject
    lateinit var pagingAdapter: NewsAdapter

    val viewModel: NewsViewModel by viewModels()
    lateinit var callback: ItemTouchHelper.Callback


    override fun getLayoutId(): Int {
        return R.layout.fragment_save_news
    }

    override fun onViewReady(view: View?, savedStateInstance: Bundle?, arguments: Bundle?) {
        shimmerFrameLayout.startShimmer()
        shimmerFrameLayout.setVisibility(View.VISIBLE)
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
                val article = pagingAdapter.getList().get(pos)
                viewModel.deleteArticle(article)

                Snackbar.make(view!!, "Deleted Successfully", Snackbar.LENGTH_SHORT).setAction(
                    "Undo"
                ) { viewModel.saveArticle(article) }.show()
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
                    val bitmap: Bitmap
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
                            view.right.toFloat() - bitmap.width.toFloat(),
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
                            view.top.toFloat() + (view.bottom.toFloat() - view.top.toFloat() - bitmap.height.toFloat()) / 2,
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

        viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { articles ->

            pagingAdapter.setList(articles as List<Article>)

        })
        shimmerFrameLayout.stopShimmer()
        shimmerFrameLayout.setVisibility(View.GONE)
    }

    private fun setUpListeners() {
        pagingAdapter.setOnClickListener(object : NewsAdapter.ClickListener {
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

        // this snaphelper makes recycler items always full visible/show views so that
        // they are fully visible not partial at start when you scroll.
        val snapHelper: SnapHelper =StartSnapHelper()
        rvSavedNews.apply {
            adapter = pagingAdapter
            layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        }
        snapHelper.attachToRecyclerView(rvSavedNews)

        ItemTouchHelper(callback).attachToRecyclerView(rvSavedNews)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}