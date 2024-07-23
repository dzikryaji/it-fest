package com.mobile.itfest.ui.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mobile.itfest.databinding.ActivitySplashScreenBinding
import com.mobile.itfest.ui.ViewModelFactory
import com.mobile.itfest.ui.login.LoginActivity
import com.mobile.itfest.ui.main.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private val viewModel by viewModels<SplashScreenViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        lifecycleScope.launch {
            delay(3000L)
            val intent = if (viewModel.checkLoggedInState()) {
                Intent(this@SplashScreenActivity, MainActivity::class.java)
            } else {
                Intent(this@SplashScreenActivity, LoginActivity::class.java)
            }
            startActivity(intent)
            finish()
        }
    }
}