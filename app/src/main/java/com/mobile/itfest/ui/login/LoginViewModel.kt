package com.mobile.itfest.ui.login

import androidx.lifecycle.ViewModel
import com.mobile.itfest.data.Repository

class LoginViewModel(private val repository: Repository): ViewModel() {
    fun checkLoggedInState() = repository.checkLoggedInState()
}