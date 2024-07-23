package com.mobile.itfest.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mobile.itfest.data.Result
import com.mobile.itfest.databinding.ActivityRegisterBinding
import com.mobile.itfest.ui.ViewModelFactory
import com.mobile.itfest.ui.login.LoginActivity
import com.mobile.itfest.ui.main.MainActivity

class RegisterActivity : AppCompatActivity() {
    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListener()
    }

    private fun setListener() {
        binding.apply {
            btnRegister.setOnClickListener {
                val name = etName.text.toString()
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                viewModel.register(name, email, password).observe(this@RegisterActivity) { result ->
                    when (result) {
                        is Result.Loading -> {
                            progressIndicator.visibility = View.VISIBLE
                        }

                        is Result.Success -> {
                            progressIndicator.visibility = View.GONE
                            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                            Toast.makeText(this@RegisterActivity, result.data, Toast.LENGTH_SHORT).show()
                        }

                        is Result.Error -> {
                            progressIndicator.visibility = View.GONE
                            Toast.makeText(this@RegisterActivity, result.error, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            tvLogin.setOnClickListener {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}