package com.saikalyandaroju.kotlinnews.auth.activities

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.saikalyandaroju.kotlinnews.R
import com.saikalyandaroju.kotlinnews.ui.activities.MainActivity
import com.saikalyandaroju.kotlinnews.utils.Constants.Companion.OTP_STEP
import com.saikalyandaroju.kotlinnews.utils.Constants.Companion.PROFILE_STEP
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val otp_step = sharedPreferences.getBoolean(OTP_STEP, false)
        val profile_step = sharedPreferences.getBoolean(PROFILE_STEP, false)


        if (!otp_step && !profile_step) {
            println("aaaaaaaaaa")
            naviagte(DESTINATION.AUTH_ACTIVITY)
        } else if (otp_step && !profile_step) {
            println("bbbbbbbbbbbbbbbbbb")
            naviagte(DESTINATION.PROFILE_ACTIVITY)
        } else if (otp_step && profile_step) {
            println("ccccccccccccccccccccc")
            naviagte(DESTINATION.MAIN_ACTIVITY)
        }


    }

    private fun naviagte(destination: DESTINATION) {

        var intent: Intent
        intent= when (destination) {

            DESTINATION.AUTH_ACTIVITY -> {
                Intent(this, AuthActivity::class.java)

            }
            DESTINATION.PROFILE_ACTIVITY -> {
                Intent(this, AuthActivity::class.java).apply {
                    putExtra("navigate_to", "profile")

                }
            }

            DESTINATION.MAIN_ACTIVITY -> {
                 Intent(this, MainActivity::class.java)

            }



        }
        startActivity(intent)
        this.finish()

    }
}


enum class DESTINATION {
    AUTH_ACTIVITY,
    PROFILE_ACTIVITY,
    MAIN_ACTIVITY
}




