package com.mobile.itfest.ui.notes.selection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.itfest.data.Repository
import com.mobile.itfest.data.Result
import com.mobile.itfest.data.model.Task
import kotlinx.coroutines.launch

class NoteTypeSelectionViewModel(private val repository: Repository) : ViewModel()  {

    val isBuyPremium = repository.isBuyPremium

    fun unlockPremium() {
        repository.unlockPremium(true)
    }
}