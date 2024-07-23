package com.mobile.itfest.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.itfest.data.Repository

object Injection {
    fun provideRepository(context: Context): Repository {
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        return Repository(auth, firestore)
    }
}