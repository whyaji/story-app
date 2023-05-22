package com.whyaji.storyapp.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.whyaji.storyapp.R
import com.whyaji.storyapp.ui.auth.AuthActivity
import com.whyaji.storyapp.ui.main.MainActivity
import com.whyaji.storyapp.util.Preference


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (isUserLoggedIn()) {
            startHomeActivity()
        } else {
            startAuthActivity()
        }
    }

    private fun isUserLoggedIn(): Boolean {
        val preference = Preference(this)
        val token = preference.getToken()
        return token.isNotEmpty() && validateToken(token)
    }

    private fun validateToken(token: String): Boolean {
        // add your token validation logic here
        return true
    }

    private fun startHomeActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }
}