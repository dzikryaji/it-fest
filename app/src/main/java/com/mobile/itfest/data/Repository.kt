package com.mobile.itfest.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.mobile.itfest.data.model.FocusTime
import com.mobile.itfest.data.model.User
import kotlinx.coroutines.tasks.await

class Repository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    private val userCollection = firestore.collection("users")
    private val focusTimeCollection = firestore.collection("focus_time")


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
            e.printStackTrace()
            Log.d(TAG, "login: ${e.message}")
            Result.Error("${e.message}")
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<String> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            authResult.user?.let {
                val uid = it.uid
                val userData = mapOf(
                    "name" to name,
                    "email" to email,
                    "point" to 0L
                )
                userCollection.document(uid).set(userData).await()
                Result.Success("User Registered")
            } ?: Result.Error("User creation failed")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "register: ${e.message}")
            Result.Error("${e.message}")
        }
    }

    suspend fun retrieveUser(): Result<User> {
        val user = auth.currentUser
        return if (user != null) {
            val userProfile = userCollection.document(user.uid).get().await().toObject<User>()
            if (userProfile != null) {
                Result.Success(userProfile)
            } else {
                Result.Error("User profile not found")
            }
        } else {
            Result.Error("Please log in first")
        }
    }

    suspend fun addPoint(pointAdded: Long): Result<String> {
        val userResult = retrieveUser()
        return if (userResult is Result.Success) {
            val user = userResult.data
            val auth = FirebaseAuth.getInstance()
            val userId = auth.currentUser?.uid ?: return Result.Error("User ID is null")

            val newPoint = user.point + pointAdded
            val updates = mapOf(
                "point" to newPoint
            )

            return try {
                userCollection.document(userId).update(updates).await()
                Result.Success("Point Added")
            } catch (e: Exception) {
                Result.Error("Error adding point: ${e.message}")
            }
        } else {
            Result.Error("Failed to retrieve user")
        }
    }

    fun logout() = auth.signOut()

    suspend fun uploadFocusTime(focusTime: FocusTime) {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            focusTime.userId = userId

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
            val query = focusTimeCollection
                .whereEqualTo("userId", userId)

            query.addSnapshotListener { value, error ->
                error?.let {
                    Log.d(TAG, "retrieveUserFocusTime: ${it.message}")
                    result.value = it.message?.let { msg -> Result.Error(msg) }
                }
                value?.let { snapshot ->
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

    suspend fun fetchTop10UsersByFocusTime() {

        try {
            // Fetch all focus time records
            val focusTimeSnapshot = focusTimeCollection.get().await()
            val focusTimeRecords =
                focusTimeSnapshot.documents.mapNotNull { it.toObject(FocusTime::class.java) }

            // Aggregate focus time for each user
            val userFocusTimeMap = focusTimeRecords.groupBy { it.userId }
                .mapValues { entry -> entry.value.sumOf { it.focusTime } }
            Log.d(TAG, "User Focus Time Map: $userFocusTimeMap")

            // Sort users by total focus time (highest to lowest)
            val sortedUserFocusTimeList = userFocusTimeMap.entries
                .sortedByDescending { it.value }
                .map { it.toPair() }.take(10)
            Log.d(TAG, "Sorted User Focus Time List: $sortedUserFocusTimeList")

            // Fetch user details
            val top10 = sortedUserFocusTimeList.map { (userId, totalFocusTime) ->
                val userSnapshot = userCollection.document(userId).get().await()
                userSnapshot.toObject(User::class.java)?.copy(totalFocusTime = totalFocusTime)
            }

            Log.d(TAG, "Top 10: $top10")
        } catch (e: Exception) {
            // Handle errors and return an empty list or handle as needed
            println("Error fetching user data: ${e.message}")
            Log.d(TAG, "fetchTop10UsersByFocusTime: ${e.javaClass.name} : ${e.message}")
        }
    }


    companion object {
        private const val TAG = "Repository"
    }
}