package com.mobile.itfest.ui.focus

import android.app.Notification.FOREGROUND_SERVICE_IMMEDIATE
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Timestamp
import com.mobile.itfest.R
import com.mobile.itfest.data.model.FocusTime
import com.mobile.itfest.databinding.ActivityFocusBinding
import com.mobile.itfest.ui.oldMain.OldMainActivity

class FocusActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFocusBinding
    private lateinit var notificationManager: NotificationManager
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var focusTime: FocusTime
    private var duration = 25 * 60 * 1000L // 25 Minutes
    private var elapsedTime = 0L // Initial elapsed time
    private var isPaused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFocusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFullScreen()
        createChannel()
        setListener()
    }

    override fun onStart() {
        super.onStart()
        startTimer()
    }

    override fun onBackPressed() {
        notificationManager.cancel(OldMainActivity.NOTIFICATION_ID)
        countDownTimer.cancel()
        binding.tvHours.text = "00"
        binding.tvMinutes.text = "25"
        binding.tvSeconds.text = "00"
        super.onBackPressed()
    }

    private fun setFullScreen() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setListener() {
        binding.apply {
            btnPause.setOnClickListener {
                if (!isPaused) {
                    countDownTimer.cancel()
                    if (elapsedTime - focusTime.focusTime > 5000) {
                        focusTime.focusTime = elapsedTime
                    }
                    isPaused = true
                    btnResume.isEnabled = true
                }
            }

            btnResume.setOnClickListener {
                if (isPaused) {
                    countDownTimer = createCountDownTimer(duration - elapsedTime)
                    countDownTimer.start()
                    isPaused = false
                    btnResume.isEnabled = false
                }
            }

            btnAdd.setOnClickListener {
                duration += 10 * 60 * 1000L
                if(isPaused) {
                    val newMinute =  tvMinutes.text.toString().toInt() + 10
                    tvMinutes.text = newMinute.toString()
                } else {
                    countDownTimer.cancel()
                    countDownTimer = createCountDownTimer(duration - elapsedTime)
                    countDownTimer.start()
                }
            }

            ivBack.setOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun createCountDownTimer(timeDuration: Long): CountDownTimer {
        return object : CountDownTimer(timeDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                elapsedTime = duration - millisUntilFinished

                // Calculate remaining hours, minutes, and seconds
                val hours = millisUntilFinished / 1000 / 60 / 60
                val minutes = (millisUntilFinished / 1000 / 60) % 60
                val seconds = (millisUntilFinished / 1000) % 60

                // Build Notification
                buildNotification(String.format("%02d:%02d:%02d", hours, minutes, seconds))

                // Update the TextView elements
                binding.tvHours.text = String.format("%02d", hours)
                binding.tvMinutes.text = String.format("%02d", minutes)
                binding.tvSeconds.text = String.format("%02d", seconds)
            }

            override fun onFinish() {
                // Handle the finish logic here if needed
                binding.tvHours.text = "00"
                binding.tvMinutes.text = "00"
                binding.tvSeconds.text = "00"
            }
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

    private fun startTimer() {
        countDownTimer = createCountDownTimer(duration)
        focusTime = FocusTime(focusTime = 0L, timeStamp = Timestamp.now())
        countDownTimer.start()
    }

    private fun createChannel() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                importance
            )
            notificationChannel.description = CHANNEL_NAME

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        private const val TAG = "FocusActivity"
        const val CHANNEL_ID = "timer_channel_id"
        const val CHANNEL_NAME = "Timer Channel"
        const val NOTIFICATION_ID = 1
    }
}