package com.mobile.itfest.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference

class Repository(
    private val auth: FirebaseAuth,
    private val collection: CollectionReference
) {
    fun checkLoggedInState(): Boolean {
        val user = auth.currentUser
        return user != null
    }
}