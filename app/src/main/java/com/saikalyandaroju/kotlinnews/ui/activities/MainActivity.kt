package com.saikalyandaroju.kotlinnews.ui.activities

import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.saikalyandaroju.kotlinnews.R
import com.saikalyandaroju.kotlinnews.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import smartdevelop.ir.eram.showcaseviewlib.GuideView
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // connecting bottom nav with navigation component.
        bottomNavigationView.setupWithNavController(newsNavHostFragment.findNavController())


        val newuser = sharedPreferences.getBoolean(Constants.NEW_USER, false)

        if (newuser) {
            showcaseViews(this)
            sharedPreferences.edit().putBoolean(Constants.NEW_USER, false).apply()
        } else {
            sharedPreferences.edit().putBoolean(Constants.NEW_USER, false).apply()
        }


    }

    private fun showcaseViews(mainActivity: MainActivity) {

        GuideView.Builder(this)
            .setTitle("News")
            .setContentText("Get the latest news here...")
            .setGravity(Gravity.auto) //optional
            .setDismissType(DismissType.anywhere) //optional - default DismissType.targetView
            .setTargetView(this.bottomNavigationView.findViewById(R.id.newsFragment))
            .setContentTextSize(12) //optional
            .setTitleTextSize(14) //optional
            .setGuideListener(GuideListener {
                secondshowcase()
            })
            .build()
            .show()


    }

    private fun secondshowcase() {
        GuideView.Builder(this)
            .setTitle("Save")
            .setContentText("Save the news you like...")
            .setGravity(Gravity.auto) //optional
            .setDismissType(DismissType.anywhere) //optional - default DismissType.targetView
            .setTargetView(this.bottomNavigationView.findViewById(R.id.saveNewsFragment))
            .setGuideListener(GuideListener {
                thirdShowcase()
            })
            .setContentTextSize(12) //optional
            .setTitleTextSize(14) //optional
            .build()
            .show()
    }

    private fun thirdShowcase() {
        GuideView.Builder(this)
            .setTitle("Search")
            .setContentText("you can search for the news...")
            .setGravity(Gravity.auto) //optional
            .setDismissType(DismissType.anywhere) //optional - default DismissType.targetView
            .setTargetView(this.bottomNavigationView.findViewById(R.id.searchNewsFragment))
            .setGuideListener(GuideListener {
                fourthShowcase()
            })
            .setContentTextSize(12) //optional
            .setTitleTextSize(14) //optional
            .build()
            .show()
    }

    private fun fourthShowcase() {
        GuideView.Builder(this)
            .setTitle("Profile")
            .setContentText("Get your profile here...")
            .setGravity(Gravity.auto) //optional
            .setDismissType(DismissType.anywhere) //optional - default DismissType.targetView
            .setTargetView(this.bottomNavigationView.findViewById(R.id.profileFragment))
            .setContentTextSize(12) //optional
            .setTitleTextSize(14) //optional
            .build()
            .show()
    }

    override fun onPause() {
        super.onPause()

    }


    override fun onDestroy() {
        super.onDestroy()
    }


}