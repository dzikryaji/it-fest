package com.mobile.itfest.data.model

import com.google.firebase.Timestamp

data class FocusTime(
    var userId: String = "",
    var focusTime: Long = 0,
    val timeStamp: Timestamp = Timestamp.now()
)