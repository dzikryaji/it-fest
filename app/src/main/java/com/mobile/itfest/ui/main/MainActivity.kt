package com.mobile.itfest.ui.main

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mobile.itfest.databinding.ActivityMainBinding
import com.mobile.itfest.ui.ViewModelFactory
import com.mobile.itfest.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var progressAnimator: ObjectAnimator
    private var isPaused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupProgressBar()
        setListener()
    }

    private fun setupProgressBar() {
        val duration = 2 * 60 * 1000 // 2 Minutes
        binding.progressBarCircular.max = duration
        progressAnimator = ObjectAnimator.ofInt(binding.progressBarCircular, "progress", 0, duration)
        progressAnimator.duration = duration.toLong()
        progressAnimator.interpolator = LinearInterpolator()

        progressAnimator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Int
            val remainingTime = duration - progress
            val minutes = remainingTime / 60000
            val seconds = (remainingTime % 60000) / 1000
            binding.tvTime.text = String.format("%02d:%02d", minutes, seconds)
        }
    }

    private fun setListener() {

        binding.apply {

            btnLogout.setOnClickListener {
                viewModel.logout()
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

            btnStart.setOnClickListener {
                if (!progressAnimator.isRunning) {
                    progressAnimator.start()
                    btnStart.visibility = View.GONE
                    btnPause.visibility = View.VISIBLE
                    isPaused = false
                }
            }

            btnPause.setOnClickListener {
                if (progressAnimator.isRunning) {
                    if (isPaused) {
                        progressAnimator.resume()
                        btnPause.text = "Pause"
                    } else {
                        progressAnimator.pause()
                        btnPause.text = "Resume"
                    }
                    isPaused = !isPaused
                }
            }

            btnReset.setOnClickListener {
                progressAnimator.cancel()
                progressBarCircular.progress = 0
                tvTime.text = "02:00"
                btnStart.visibility = View.VISIBLE
                btnPause.visibility = View.GONE
                isPaused = false
            }
        }
    }
}