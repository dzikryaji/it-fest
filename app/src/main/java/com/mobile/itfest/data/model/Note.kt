package com.mobile.itfest.data.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Note(
    val id: String = "",
    val title: String = "Title",
    val agenda: String = "Agenda",
    val description: String = "",
    val dateCreated: Timestamp = Timestamp.now(),
    var dateLastEdited: Timestamp = Timestamp.now(),
) : Parcelable