package com.mobile.itfest.data.model

import com.google.firebase.Timestamp

data class Note(
    val id: String = "",
    val title: String = "Title",
    val agenda: String = "Agenda",
    val description: String = "",
    val dateCreated: Timestamp = Timestamp.now(),
    var dateLastEdited: Timestamp = Timestamp.now(),
)