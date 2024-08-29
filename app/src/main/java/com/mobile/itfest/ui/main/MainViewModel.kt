package com.mobile.itfest.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.itfest.data.Repository
import com.mobile.itfest.data.Result
import com.mobile.itfest.data.model.FocusTime
import com.mobile.itfest.data.model.Task
import com.mobile.itfest.data.model.User
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {

    private val user = MutableLiveData<Result<User>>()
    private val top10Users = MutableLiveData<Result<List<User>>>()
    val tasks = repository.tasks

    fun logout() = repository.logout()


    fun retrieveFocusTime(): LiveData<Result<List<FocusTime>>> = repository.retrieveUserFocusTime()

    fun fetchTop10UsersByFocusTime(): LiveData<Result<List<User>>> {
        if (top10Users.value == null || top10Users.value is Result.Error) {
            top10Users.value = Result.Loading
            viewModelScope.launch {
                top10Users.value = repository.fetchTop10UsersByFocusTime()
            }
        }
        return top10Users
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

    fun uploadTask(task: Task): LiveData<Result<String>> {
        val result = MutableLiveData<Result<String>>()
        result.value = Result.Loading
        viewModelScope.launch {
            result.value = repository.uploadTask(task)
        }
        return result
    }

    fun deleteTask(task: Task): LiveData<Result<String>> {
        val result = MutableLiveData<Result<String>>()
        result.value = Result.Loading
        viewModelScope.launch {
            result.value = repository.deleteTask(task.id)
        }
        return result
    }

    fun markTaskAsDone(task: Task): LiveData<Result<String>> {
        val result = MutableLiveData<Result<String>>()
        result.value = Result.Loading
        viewModelScope.launch {
            result.value = repository.markTaskAsDone(task.id)
        }
        return result
    }

    fun startTaskListener() = repository.startTaskListener()

    fun stopTaskListener() = repository.stopTaskListener()
}