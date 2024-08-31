package com.mobile.itfest.ui.notes.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.itfest.data.Repository
import com.mobile.itfest.data.Result
import com.mobile.itfest.data.model.Note
import kotlinx.coroutines.launch

class CreateNoteViewModel(private val repository: Repository): ViewModel() {
    fun uploadNote(note: Note): LiveData<Result<String>> {
        val result = MutableLiveData<Result<String>>()
        result.value = Result.Loading
        viewModelScope.launch {
            result.value = repository.uploadNote(note)
        }
        return result
    }
}