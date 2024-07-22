package com.mobile.itfest.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.itfest.data.Repository
import com.mobile.itfest.data.Result
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: Repository):ViewModel() {
    private val result = MutableLiveData<Result<String>>()

    fun register(email: String, password: String): LiveData<Result<String>> {
        result.value = Result.Loading
        viewModelScope.launch {
            result.value = repository.register(email, password)
        }
        return result
    }
}