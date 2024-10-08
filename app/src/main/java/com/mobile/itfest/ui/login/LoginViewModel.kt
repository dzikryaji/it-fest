package com.mobile.itfest.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.itfest.data.Repository
import com.mobile.itfest.data.Result
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: Repository): ViewModel() {
    private val result = MutableLiveData<Result<String>>()
    fun login(email:String, password:String) :LiveData<Result<String>> {
        result.value = Result.Loading
        viewModelScope.launch {
            result.value = repository.login(email, password)
        }
        return result
    }
}