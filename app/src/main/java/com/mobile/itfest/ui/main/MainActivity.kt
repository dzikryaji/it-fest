package com.mobile.itfest.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mobile.itfest.R
import com.mobile.itfest.databinding.ActivityMainBinding
import com.mobile.itfest.ui.main.leaderboard.LeaderboardFragment
import com.mobile.itfest.ui.main.profile.ProfileFragment
import com.mobile.itfest.ui.main.timer.TimerFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var timerFragment: TimerFragment
    private lateinit var leaderboardFragment: LeaderboardFragment
    private lateinit var profileFragment: ProfileFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        timerFragment = TimerFragment()
        leaderboardFragment = LeaderboardFragment()
        profileFragment = ProfileFragment()

        setBottomNavView()
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
}
