package com.mobile.itfest.ui.notes.main

import androidx.lifecycle.ViewModel
import com.mobile.itfest.data.Repository

class NoteViewModel(private val repository: Repository): ViewModel() {

    val notes = repository.notes

    fun startNoteListener() = repository.startNoteListener()

    fun stopNoteListener() = repository.stopNoteListener()

}