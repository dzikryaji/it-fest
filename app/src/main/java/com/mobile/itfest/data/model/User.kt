package com.mobile.itfest.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val name: String = "",
    val email: String = "",
    val point: Long = 0,
    var totalFocusTime: Long = 0,
    var id: String = ""
) : Parcelable