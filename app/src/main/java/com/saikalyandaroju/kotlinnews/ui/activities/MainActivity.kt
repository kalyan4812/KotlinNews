package com.saikalyandaroju.kotlinnews.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.saikalyandaroju.kotlinnews.R
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // connecting bottom nav with navigation component.
        bottomNavigationView.setupWithNavController(newsNavHostFragment.findNavController())


    }



}