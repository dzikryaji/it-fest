package com.mobile.itfest.ui.main

import androidx.lifecycle.ViewModel
import com.mobile.itfest.data.Repository

class MainViewModel(private val repository: Repository): ViewModel() {
    fun checkLoggedInState() = repository.checkLoggedInState()
}