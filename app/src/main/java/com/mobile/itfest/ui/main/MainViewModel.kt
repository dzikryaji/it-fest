package com.mobile.itfest.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.itfest.data.Repository
import com.mobile.itfest.data.Result
import com.mobile.itfest.data.model.FocusTime
import com.mobile.itfest.data.model.User
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {

    fun logout() = repository.logout()

    fun uploadFocusTime(focusTime: FocusTime) {
        viewModelScope.launch {
            repository.uploadFocusTime(focusTime)
        }
    }

    fun retrieveFocusTime(): LiveData<Result<List<FocusTime>>> = repository.retrieveUserFocusTime()

    fun fetchTop10UsersByFocusTime() {
        viewModelScope.launch {
            repository.fetchTop10UsersByFocusTime()
        }
    }

    fun retrieveUser() : LiveData<Result<User>>{
        val result = MutableLiveData<Result<User>>()
        result.value = Result.Loading
        viewModelScope.launch {
            result.value = repository.retrieveUser()
        }
        return result
    }
}