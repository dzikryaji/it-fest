package com.mobile.itfest.ui.main.leaderboard.user_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mobile.itfest.data.Repository
import com.mobile.itfest.data.Result
import com.mobile.itfest.data.model.FocusTime

class UserLeaderboardDetailViewModel(private val repository: Repository) : ViewModel() {

    fun findOtherUserFocusTime(userId: String): LiveData<Result<List<FocusTime>>> = repository.findOtherUserFocusTimeByUserId(userId)
}