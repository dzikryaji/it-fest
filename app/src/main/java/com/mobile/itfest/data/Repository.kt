package com.mobile.itfest.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.mobile.itfest.data.model.FocusTime
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

    suspend fun login(email: String, password: String): Result<String> {
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

    suspend fun uploadFocusTime(focusTime: FocusTime) {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            focusTime.userId = userId

            val focusTimeCollection = firestore.collection("focus_time")
            val query = focusTimeCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("timeStamp", focusTime.timeStamp)
                .get()
                .await()

            if (query.isEmpty) {
                // If no document with the same timestamp, add new one
                focusTimeCollection.add(focusTime).await()
            } else {
                // If document exists, check the elapsed time
                val document = query.documents.first()
                document.reference.update("focusTime", focusTime.focusTime)
            }

        } else {
            Log.d(TAG, "User not logged in")
        }
    }

    fun retrieveUserFocusTime(): LiveData<Result<List<FocusTime>>> {
        val result = MutableLiveData<Result<List<FocusTime>>>()
        result.value = Result.Loading
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            val focusTimeCollection = firestore.collection("focus_time")
            val query = focusTimeCollection
                .whereEqualTo("userId", userId)
            
            query.addSnapshotListener { value, error ->
                error?.let {
                    Log.d(TAG, "retrieveUserFocusTime: ${it.message}")
                    result.value = it.message?.let { msg -> Result.Error(msg) }
                }
                value?.let {snapshot ->
                    val focusTimeList = snapshot.documents
                        .mapNotNull { it.toObject<FocusTime>() } // Convert each document to FocusTime and filter out nulls
                    result.value = Result.Success(focusTimeList)
                }
            }

        } else {
            Log.d(TAG, "User not logged in")
            result.value = Result.Error("User not logged in")
        }
        return result
    }


    companion object {
        private const val TAG = "Repository"
    }
}