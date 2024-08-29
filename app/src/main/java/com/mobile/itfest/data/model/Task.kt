package com.mobile.itfest.data.model

import com.google.firebase.Timestamp

data class Task (
    val id: String = "",
    val name: String = "",
    val deadline: Timestamp = Timestamp.now(),
    val isDone: Boolean = false
)