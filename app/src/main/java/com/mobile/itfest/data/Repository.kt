package com.mobile.itfest.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class Repository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    fun checkLoggedInState(): Boolean {
        val user = auth.currentUser
        Log.d(TAG, "checkLoggedInState: $user")
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

    suspend fun register(name: String, email: String, password: String): Result<String> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            authResult.user?.let {
               updateName(name)
            } ?: Result.Error("User creation failed")
        } catch (e: Exception) {
            Log.d(TAG, "register: ${e.message}")
            Result.Error("Something went wrong: ${e.message}")
        }
    }

    private suspend fun updateName(name: String): Result<String> {
        val user = auth.currentUser
        return if (user != null) {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            try {
                user.updateProfile(profileUpdates).await()
                Result.Success("Profile updated successfully")
            } catch (e: Exception) {
                Log.d(TAG, "updateProfile: ${e.message}")
                Result.Error("Something went wrong: ${e.message}")
            }
        } else {
            Result.Error("No authenticated user found")
        }
    }

    fun logout() = auth.signOut()

    
    
    companion object{
        private const val TAG = "Repository"
    }
}