package com.mobile.itfest.ui.main.timer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mobile.itfest.databinding.FragmentTimerBinding


class TimerFragment : Fragment() {
    private lateinit var binding: FragmentTimerBinding
//    private val viewModel by viewModels<MainViewModel> {
//        ViewModelFactory.getInstance(requireActivity())
//    }
//
//    private val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { isGranted: Boolean? ->
//        if (!isGranted!!)
//            ToastManager.showToast(requireActivity(), "Unable to display notification due to permission decline")
//    }
//
//    private lateinit var notificationManager: NotificationManager
//    private lateinit var countDownTimer: CountDownTimer
//    private lateinit var progressAnimator: ObjectAnimator
//    private lateinit var focusTime: FocusTime
//    private val duration = 2 * 60 * 1000L // 2 Minutes
//    private var elapsedTime = 0L // Initial elapsed time
//    private var isPaused = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTimerBinding.inflate(inflater, container, false)

//        createChannel()
//        setupTimer()
//        setListener()
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
//            if (ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.POST_NOTIFICATIONS) !=
//                PackageManager.PERMISSION_GRANTED)
//                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
//        }
//
//        viewModel.retrieveFocusTime().observe(viewLifecycleOwner) {
//            if (it is Result.Success) {
//                Log.d(TAG, "onCreate: ${it.data}")
//            }
//        }
//        viewModel.fetchTop10UsersByFocusTime()
//
//        binding.buttonChart.setOnClickListener {
//            val intent = Intent(requireActivity(), DurationDataActivity::class.java)
//            startActivity(intent)
//        }

        return binding.root
    }

//    private fun setupTimer() {
//        binding.progressBarCircular.max = duration.toInt()
//        countDownTimer = createCountDownTimer(duration)
//    }
//
//    private fun createCountDownTimer(timeDuration: Long): CountDownTimer {
//        return object : CountDownTimer(timeDuration, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//                elapsedTime = duration - millisUntilFinished
//                animateProgressBar(binding.progressBarCircular.progress, elapsedTime.toInt())
//
//                val formattedTime = formatTime(millisUntilFinished.toInt())
//                buildNotification(formattedTime)
//                binding.tvTime.text = formattedTime
//            }
//
//            override fun onFinish() {
//                binding.tvTime.text = "00:00"
//                animateProgressBar(binding.progressBarCircular.progress, duration.toInt())
//                focusTime.focusTime = duration
//                viewModel.uploadFocusTime(focusTime)
//                notificationManager.cancel(NOTIFICATION_ID)
//            }
//        }
//    }
//
//    private fun setListener() {
//        binding.apply {
//            btnLogout.setOnClickListener {
//                viewModel.logout()
//                val intent = Intent(requireActivity(), LoginActivity::class.java)
//                startActivity(intent)
//                requireActivity().finish()
//            }
//
//            btnStart.setOnClickListener {
//                if (!isPaused) {
//                    countDownTimer.start()
//                    btnStart.visibility = View.GONE
//                    btnPause.visibility = View.VISIBLE
//                    focusTime = FocusTime(focusTime = 0L, timeStamp = Timestamp.now())
//                }
//            }
//
//            btnPause.setOnClickListener {
//                if (isPaused) {
//                    countDownTimer = createCountDownTimer(duration - elapsedTime)
//                    countDownTimer.start()
//                    btnPause.text = "Pause"
//                } else {
//                    countDownTimer.cancel()
//                    btnPause.text = "Resume"
//                    if (elapsedTime - focusTime.focusTime > 5000) {
//                        focusTime.focusTime = elapsedTime
//                        viewModel.uploadFocusTime(focusTime)
//                    }
//                }
//                isPaused = !isPaused
//            }
//
//            btnReset.setOnClickListener {
//                if (!isPaused) {
//                    if (elapsedTime - focusTime.focusTime > 5000) {
//                        focusTime.focusTime = elapsedTime
//                        viewModel.uploadFocusTime(focusTime)
//                    }
//                }
//                countDownTimer.cancel()
//                progressAnimator.cancel()
//                binding.progressBarCircular.progress = 0
//                binding.tvTime.text = "02:00"
//                elapsedTime = 0L // Reset elapsed time
//                countDownTimer = createCountDownTimer(duration) // Reinitialize the timer
//                btnStart.visibility = View.VISIBLE
//                btnPause.visibility = View.GONE
//                isPaused = false
//                notificationManager.cancel(NOTIFICATION_ID)
//            }
//        }
//    }
//
//    private fun createChannel() {
//        notificationManager = requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val importance = NotificationManager.IMPORTANCE_LOW
//
//            val notificationChannel = NotificationChannel(
//                CHANNEL_ID,
//                CHANNEL_NAME,
//                importance
//            )
//            notificationChannel.description = CHANNEL_NAME
//
//            notificationManager.createNotificationChannel(notificationChannel)
//        }
//    }
//
//    private fun buildNotification(time: String) {
//        val notificationBuilder = NotificationCompat.Builder(requireActivity(), CHANNEL_ID)
//            .setSmallIcon(R.mipmap.ic_launcher)
//            .setContentTitle("Timer Running")
//            .setContentText(time)
//            .setOnlyAlertOnce(true)
//            .setSound(null)
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setOngoing(true)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            notificationBuilder.setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE)
//        }
//
//        val notification = notificationBuilder.build()
//        notificationManager.notify(NOTIFICATION_ID, notification)
//    }
//
//    private fun formatTime(remainingTime: Int): String {
//        val minutes = remainingTime / 60000
//        val seconds = (remainingTime % 60000) / 1000
//        return String.format("%02d:%02d", minutes, seconds)
//    }
//
//    private fun animateProgressBar(fromProgress: Int, toProgress: Int) {
//        progressAnimator = ObjectAnimator.ofInt(binding.progressBarCircular, "progress", fromProgress, toProgress)
//        progressAnimator.duration = 1000 // 1 second for each tick
//        progressAnimator.interpolator = LinearInterpolator()
//        progressAnimator.start()
//    }
//
//    companion object {
//        private const val TAG = "TimerFragment"
//        const val CHANNEL_ID = "timer_channel_id"
//        const val CHANNEL_NAME = "Timer Channel"
//        const val NOTIFICATION_ID = 1
//    }
}