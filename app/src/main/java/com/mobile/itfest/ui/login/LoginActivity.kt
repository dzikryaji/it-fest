package com.mobile.itfest.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mobile.itfest.data.Result
import com.mobile.itfest.databinding.ActivityLoginBinding
import com.mobile.itfest.ui.ViewModelFactory
import com.mobile.itfest.ui.main.MainActivity
import com.mobile.itfest.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListener()
    }

    private fun setListener() {
        binding.apply {
            btnLogin.setOnClickListener {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                viewModel.login(email, password).observe(this@LoginActivity) { result ->
                    when (result) {
                        is Result.Loading -> {
                            progressIndicator.visibility = View.VISIBLE
                        }

                        is Result.Success -> {
                            progressIndicator.visibility = View.GONE
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                            Toast.makeText(this@LoginActivity, result.data, Toast.LENGTH_SHORT).show()
                        }

                        is Result.Error -> {
                            progressIndicator.visibility = View.GONE
                            Toast.makeText(this@LoginActivity, result.error, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            tvRegister.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}