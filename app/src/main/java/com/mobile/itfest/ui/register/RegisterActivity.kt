package com.mobile.itfest.ui.register

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mobile.itfest.R
import com.mobile.itfest.data.Result
import com.mobile.itfest.databinding.ActivityRegisterBinding
import com.mobile.itfest.ui.ViewModelFactory
import com.mobile.itfest.ui.main.MainActivity
import com.mobile.itfest.utils.ToastManager.showToast
import com.mobile.itfest.utils.isConnectedToInternet

class RegisterActivity : AppCompatActivity() {
    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFullScreenSize()
        setListeners()
    }

    private fun setFullScreenSize() {
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT))
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setListeners() {
        binding.apply {
            btnRegister.setOnClickListener {
                if (tilName.isErrorEnabled || edName.text.toString().isEmpty()) {
                    showToast(this@RegisterActivity, getString(R.string.error_name))
                    return@setOnClickListener
                } else if (tilEmail.isErrorEnabled || edEmail.text.toString().isEmpty()) {
                    showToast(this@RegisterActivity, getString(R.string.error_email))
                    return@setOnClickListener
                } else if (tilPassword.isErrorEnabled || edPassword.text.toString().isEmpty()) {
                    showToast(this@RegisterActivity, getString(R.string.error_password))
                    return@setOnClickListener
                }

                val name = edName.text.toString()
                val email = edEmail.text.toString()
                val password = edPassword.text.toString()

                if (isConnectedToInternet(this@RegisterActivity)) {
                    observeRegister(name, email, password)
                } else {
                    showToast(this@RegisterActivity, "Please check your network connection")
                }

            }

            btnLogin.setOnClickListener { finish() }
            btnBack.setOnClickListener { finish() }



            edName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.toString().isEmpty()) {
                        tilName.isErrorEnabled = true
                        tilName.error = getString(R.string.error_name)
                    } else {
                        tilName.isErrorEnabled = false
                        tilName.error = null
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

        }
    }

    private fun observeRegister(name: String, email: String, password: String) {
        viewModel.register(name, email, password).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressIndicator.visibility = View.VISIBLE
                }

                is Result.Error -> {
                    binding.progressIndicator.visibility = View.GONE
                    showToast(this, result.error)
                }

                is Result.Success -> {
                    binding.progressIndicator.visibility = View.GONE
                    showToast(applicationContext, result.data)
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}