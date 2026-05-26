package com.bamboo.ktf

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    private var navigated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        showAppOpenAndNavigate()
    }

    private fun showAppOpenAndNavigate() {
        val manager = KtfApplication.appOpenAdManager
        if (manager == null) {
            navigateToMain()
            return
        }

        manager.showIfAvailable(this) {
            navigateToMain()
        }
    }

    private fun navigateToMain() {
        if (navigated) return
        navigated = true
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

