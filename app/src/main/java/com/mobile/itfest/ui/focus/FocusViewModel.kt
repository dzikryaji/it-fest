package com.mobile.itfest.ui.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.itfest.data.Repository
import com.mobile.itfest.data.model.FocusTime
import kotlinx.coroutines.launch

class FocusViewModel(private val repository: Repository):ViewModel() {
    fun uploadFocusTime(focusTime: FocusTime) {
        viewModelScope.launch {
            repository.uploadFocusTime(focusTime)
        }
    }
}