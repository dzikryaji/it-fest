package com.mobile.itfest.ui.focus

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mobile.itfest.R
import com.mobile.itfest.databinding.ActivityFocusBinding

class FocusActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFocusBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFocusBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullScreen()
    }

    private fun setFullScreen() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            enableEdgeToEdge()
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}