package com.saikalyandaroju.kotlinnews.auth.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.navigation.findNavController
import com.saikalyandaroju.kotlinnews.R
import com.saikalyandaroju.kotlinnews.auth.fragments.SignupFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_auth.*

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        getInfo()


    }


    private fun getInfo() {
        val info = intent?.getStringExtra("navigate_to")

        if (info != null && info == "profile") {
            findNavController(R.id.authNavHostFragment).navigate(R.id.signupFragment)
            Toast.makeText(
                applicationContext,
                "Registered user ,please fill the profile",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }
}