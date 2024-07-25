package com.mobile.itfest.ui.main

import android.animation.ObjectAnimator
import android.app.Notification.FOREGROUND_SERVICE_IMMEDIATE
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.mobile.itfest.R
import com.mobile.itfest.data.Result
import com.mobile.itfest.data.model.FocusTime
import com.mobile.itfest.databinding.ActivityMainBinding
import com.mobile.itfest.ui.ViewModelFactory
import com.mobile.itfest.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean? ->
        if (!isGranted!!)
            Toast.makeText(this,
                "Unable to display notification due to permission decline",
                Toast.LENGTH_LONG)
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var notificationManager: NotificationManager
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var progressAnimator: ObjectAnimator
    private lateinit var focusTime: FocusTime
    private val duration = 2 * 60 * 1000L // 2 Minutes
    private var elapsedTime = 0L // Initial elapsed time
    private var isPaused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createChannel()
        setupTimer()
        setListener()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED)
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        viewModel.retrieveFocusTime().observe(this) {
            if (it is Result.Success) {
                Log.d(TAG, "onCreate: ${it.data}")
            }
        }
    }

    private fun setupTimer() {
        binding.progressBarCircular.max = duration.toInt()
        countDownTimer = createCountDownTimer(duration)
    }

    private fun createCountDownTimer(timeDuration: Long): CountDownTimer {
        return object : CountDownTimer(timeDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                elapsedTime = duration - millisUntilFinished
                animateProgressBar(binding.progressBarCircular.progress, elapsedTime.toInt())

                val formattedTime = formatTime(millisUntilFinished.toInt())
                buildNotification(formattedTime)
                binding.tvTime.text = formattedTime
            }

            override fun onFinish() {
                binding.tvTime.text = "00:00"
                animateProgressBar(binding.progressBarCircular.progress, duration.toInt())
                focusTime.focusTime = duration
                viewModel.uploadFocusTime(focusTime)
                notificationManager.cancel(NOTIFICATION_ID)
            }
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
                if (!isPaused) {
                    countDownTimer.start()
                    btnStart.visibility = View.GONE
                    btnPause.visibility = View.VISIBLE
                    focusTime = FocusTime(focusTime = 0L, timeStamp = Timestamp.now())
                }
            }

            btnPause.setOnClickListener {
                if (isPaused) {
                    countDownTimer = createCountDownTimer(duration - elapsedTime)
                    countDownTimer.start()
                    btnPause.text = "Pause"
                } else {
                    countDownTimer.cancel()
                    btnPause.text = "Resume"
                    if (elapsedTime - focusTime.focusTime > 5000) {
                        focusTime.focusTime = elapsedTime
                        viewModel.uploadFocusTime(focusTime)
                    }
                }
                isPaused = !isPaused
            }

            btnReset.setOnClickListener {
                countDownTimer.cancel()
                progressAnimator.cancel()
                binding.progressBarCircular.progress = 0
                binding.tvTime.text = "02:00"
                elapsedTime = 0L // Reset elapsed time
                countDownTimer = createCountDownTimer(duration) // Reinitialize the timer
                btnStart.visibility = View.VISIBLE
                btnPause.visibility = View.GONE
                isPaused = false
                notificationManager.cancel(NOTIFICATION_ID)
            }
        }
    }

    private fun createChannel() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW

            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                importance
            )
            notificationChannel.description = CHANNEL_NAME

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun buildNotification(time: String) {
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Timer Running")
            .setContentText(time)
            .setOnlyAlertOnce(true)
            .setSound(null)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            notificationBuilder.setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE)
        }

        val notification = notificationBuilder.build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun formatTime(remainingTime: Int): String {
        val minutes = remainingTime / 60000
        val seconds = (remainingTime % 60000) / 1000
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun animateProgressBar(fromProgress: Int, toProgress: Int) {
        progressAnimator = ObjectAnimator.ofInt(binding.progressBarCircular, "progress", fromProgress, toProgress)
        progressAnimator.duration = 1000 // 1 second for each tick
        progressAnimator.interpolator = LinearInterpolator()
        progressAnimator.start()
    }

    companion object {
        private const val TAG = "MainActivity"
        const val CHANNEL_ID = "timer_channel_id"
        const val CHANNEL_NAME = "Timer Channel"
        const val NOTIFICATION_ID = 1
    }
}
