package com.mobile.itfest.data.model

data class User(
    val name: String = "",
    val email: String = "",
    val point: Long = 0,
    var totalFocusTime: Long = 0
)