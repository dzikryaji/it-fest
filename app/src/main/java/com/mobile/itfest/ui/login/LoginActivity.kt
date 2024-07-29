package com.mobile.itfest.ui.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mobile.itfest.R
import com.mobile.itfest.data.Result
import com.mobile.itfest.databinding.ActivityLoginBinding
import com.mobile.itfest.ui.ViewModelFactory
import com.mobile.itfest.ui.main.MainActivity
import com.mobile.itfest.ui.register.RegisterActivity
import com.mobile.itfest.utils.ToastManager.showToast
import com.mobile.itfest.utils.isConnectedToInternet

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFullScreenSize()
        setListener()
    }

    private fun setFullScreenSize() {
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT))
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setListener() {
        binding.apply {
            btnLogin.setOnClickListener {
                if (tilEmail.isErrorEnabled || edEmail.text.toString().isEmpty()) {
                    return@setOnClickListener
                } else if (tilPassword.isErrorEnabled || edPassword.text.toString().isEmpty()) {
                    return@setOnClickListener
                }

                val email = edEmail.text.toString()
                val password = edPassword.text.toString()
                if (isConnectedToInternet(this@LoginActivity)) {
                    observeLogin(email, password)
                } else {
                    showToast(this@LoginActivity, "Please check your network connection")
                }

            }

            btnRegister.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }

            btnBack.setOnClickListener { finish() }
        }
    }

    private fun observeLogin(email: String, password: String) {
        binding.apply {
            viewModel.login(email, password).observe(this@LoginActivity) { result ->
                when (result) {
                    is Result.Loading -> {
                        progressIndicator.visibility = View.VISIBLE
                    }

                    is Result.Error -> {
                        progressIndicator.visibility = View.GONE
                        showToast(this@LoginActivity, result.error)
                    }

                    is Result.Success -> {
                        progressIndicator.visibility = View.GONE
                        showToast(applicationContext, result.data)
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }
}