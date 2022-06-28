package com.saikalyandaroju.kotlinnews.auth.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.saikalyandaroju.kotlinnews.R
import com.saikalyandaroju.kotlinnews.ui.activities.MainActivity
import com.saikalyandaroju.kotlinnews.utils.Constants.Companion.OTP_STEP
import com.saikalyandaroju.kotlinnews.utils.Constants.Companion.PROFILE_STEP
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.*
import java.lang.Runnable
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        splashanimation.enableMergePathsForKitKatAndAbove(true)

        GlobalScope.launch(Dispatchers.Main) {
            delay(2500)


            //Handler(Looper.getMainLooper()).postDelayed(Runnable {
            val otp_step = sharedPreferences.getBoolean(OTP_STEP, false)
            val profile_step = sharedPreferences.getBoolean(PROFILE_STEP, false)


            // check for right destination.

            if (!otp_step && !profile_step) {

                naviagte(DESTINATION.AUTH_ACTIVITY)
            } else if (otp_step && !profile_step) {

                naviagte(DESTINATION.PROFILE_ACTIVITY)
            } else if (otp_step && profile_step) {

                naviagte(DESTINATION.MAIN_ACTIVITY)
            }

            //}, 2500)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun naviagte(destination: DESTINATION) {

        var intent: Intent
        intent = when (destination) {

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







