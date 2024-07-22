package com.mobile.itfest.ui.splashscreen

import androidx.lifecycle.ViewModel
import com.mobile.itfest.data.Repository

class SplashScreenViewModel(private val repository: Repository): ViewModel(){
    fun checkLoggedInState() = repository.checkLoggedInState()
}