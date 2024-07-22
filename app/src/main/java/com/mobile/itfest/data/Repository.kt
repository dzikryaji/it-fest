package com.mobile.itfest.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.tasks.await

class Repository(
    private val auth: FirebaseAuth,
    private val collection: CollectionReference
) {
    fun checkLoggedInState(): Boolean {
        val user = auth.currentUser
        return user != null
    }

    suspend fun login(email:String, password:String): Result<String> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            authResult.user?.let {
                Result.Success("Login Success")
            } ?: Result.Error("Wrong Email or Password")
        } catch (e: Exception) {
            Result.Error("Something went wrong: ${e.message}")
        }
    }

    suspend fun register(email: String, password: String): Result<String> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            authResult.user?.let {
                Result.Success("User created successfully")
            } ?: Result.Error("User creation failed")
        } catch (e: Exception) {
            Result.Error("Something went wrong: ${e.message}")
        }
    }
}