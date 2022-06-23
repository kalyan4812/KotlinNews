package com.saikalyandaroju.kotlinnews.auth.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemChangeListener
import com.denzcoskun.imageslider.models.SlideModel
import com.saikalyandaroju.kotlinnews.R
import kotlinx.android.synthetic.main.fragment_on_board.*
import kotlinx.android.synthetic.main.optional.view.*


class OnBoardFragment : Fragment() {

    private var slideList: ArrayList<SlideModel> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            com.saikalyandaroju.kotlinnews.R.layout.fragment_on_board,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inits()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun inits() {

        val drawable= ResourcesCompat.getDrawable(getResources(), R.drawable.image, null)

        drawable?.
        setColorFilter(PorterDuffColorFilter(ContextCompat.getColor(requireContext(),R.color.color_secondary),
            PorterDuff.Mode.SRC_IN))


        slideList.add(
            SlideModel(
                R.drawable.world,
                "NEWS"
            )
        )
        slideList.add(
            SlideModel(
                R.drawable.world,
                "SAVE"
            )
        )

        imageSlider.setImageList(slideList,ScaleTypes.FIT)

        imageSlider.startSliding(1500)



        imageSlider.setItemChangeListener(object : ItemChangeListener {
            override fun onItemChanged(i: Int) {
                title?.clearComposingText()
                description?.clearComposingText()
                when (i) {
                    0 -> {

                        description?.setText("Get the latest news on internet without delay...")
                    }
                    1 -> {

                        description?.setText("And also you can search for the news and save if you like :) ")
                    }

                    else -> {}
                }
            }
        })

        skip.setOnClickListener(View.OnClickListener {

            findNavController().navigate(R.id.action_onBoardFragment_to_mainActivity)

        })

    }


}