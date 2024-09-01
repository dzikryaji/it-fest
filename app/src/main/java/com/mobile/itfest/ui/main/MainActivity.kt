package com.mobile.itfest.ui.main

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.mobile.itfest.R
import com.mobile.itfest.databinding.ActivityMainBinding
import com.mobile.itfest.ui.ViewModelFactory
import com.mobile.itfest.ui.main.leaderboard.LeaderboardFragment
import com.mobile.itfest.ui.main.profile.ProfileFragment
import com.mobile.itfest.ui.main.timer.TimerFragment

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private lateinit var timerFragment: TimerFragment
    private lateinit var leaderboardFragment: LeaderboardFragment
    private lateinit var profileFragment: ProfileFragment
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        timerFragment = TimerFragment()
        leaderboardFragment = LeaderboardFragment()
        profileFragment = ProfileFragment()

        setBottomNavView()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED)
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun setBottomNavView() {
        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_timer -> {
                    replaceFragment(timerFragment)
                    return@setOnItemSelectedListener true
                }

                R.id.navigation_leaderboard -> {
                    replaceFragment(leaderboardFragment)
                    return@setOnItemSelectedListener true
                }

                R.id.navigation_profile -> {
                    replaceFragment(profileFragment)
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
        replaceFragment(timerFragment)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    override fun onStart() {
        super.onStart()
        viewModel.startTaskListener()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchTop10UsersByFocusTime()
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopTaskListener()
    }
}
