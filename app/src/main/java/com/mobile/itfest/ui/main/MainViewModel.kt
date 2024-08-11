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

    private val user = MutableLiveData<Result<User>>()

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

    fun retrieveUser(): LiveData<Result<User>> {
        if (user.value == null || user.value is Result.Error) {
            user.value = Result.Loading
            viewModelScope.launch {
                user.value = repository.retrieveUser()
            }
        }
        return user
    }
}