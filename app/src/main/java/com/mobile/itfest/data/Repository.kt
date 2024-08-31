package com.mobile.itfest.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.mobile.itfest.data.model.FocusTime
import com.mobile.itfest.data.model.Task
import com.mobile.itfest.data.model.User
import kotlinx.coroutines.tasks.await

class Repository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    private val userCollection = firestore.collection("users")
    private val focusTimeCollection = firestore.collection("focus_time")

    private val _leaderboard = MutableLiveData<List<User>>()
    val leaderboard: LiveData<List<User>> get() = _leaderboard

    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> get() = _tasks

    private var taskListener: ListenerRegistration? = null

    private var _isBuyPremium = MutableLiveData<Boolean>()
    val isBuyPremium: LiveData<Boolean>
        get() = _isBuyPremium

    fun unlockPremium(state: Boolean) {
        _isBuyPremium.value = state
    }

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

            val newPoint = user.point + pointAdded
            val updates = mapOf(
                "point" to newPoint
            )

            return try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null) {
                    userCollection.document(userId).update(updates).await()
                    Result.Success("Point Added")
                } else {
                    Log.e("Firestore", "User not logged in")
                    Result.Error("User not logged in") // Return error result
                }

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
            val focusTimeRecords = focusTimeSnapshot.documents.mapNotNull { it.toObject(FocusTime::class.java) }

            // Aggregate focus time for each user
            val userFocusTimeMap = focusTimeRecords.groupBy { it.userId }
                .mapValues { entry -> entry.value.sumOf { it.focusTime } }
            Log.d(TAG, "User Focus Time Map: $userFocusTimeMap")

            // Sort users by total focus time (highest to lowest)
            val sortedUserFocusTimeList = userFocusTimeMap.entries
                .sortedByDescending { it.value }
                .map { it.toPair() }
                .take(10)
            Log.d(TAG, "Sorted User Focus Time List: $sortedUserFocusTimeList")

            // Fetch user details
            val top10 = sortedUserFocusTimeList.mapNotNull { (userId, totalFocusTime) ->
                val userSnapshot = userCollection.document(userId).get().await()
                userSnapshot.toObject(User::class.java)?.copy(totalFocusTime = totalFocusTime)
            }

            Log.d(TAG, "Top 10: $top10")
            _leaderboard.value = top10
        } catch (e: Exception) {
            Log.d(TAG, "fetchTop10UsersByFocusTime: ${e.javaClass.name} : ${e.message}")

        }
    }

    suspend fun uploadTask(task: Task): Result<String> {
        return try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                userCollection
                    .document(userId)
                    .collection("tasks")
                    .document(task.id)
                    .set(task)
                    .await()

                Log.d("Firestore", "Task successfully added: $task")
                Result.Success("Task added successfully") // Return success result
            } else {
                Log.e("Firestore", "User not logged in")
                Result.Error("User not logged in") // Return error result
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error adding task", e)
            Result.Error("Failed to upload task: ${e.message}") // Return error result with exception message
        }
    }

    suspend fun deleteTask(taskId: String): Result<String> {
        return try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                userCollection
                    .document(userId)
                    .collection("tasks")
                    .document(taskId)
                    .delete() // Delete the task document
                    .await()

                Log.d("Firestore", "Task successfully deleted: $taskId")
                Result.Success("Task deleted successfully") // Return success result
            } else {
                Log.e("Firestore", "User not logged in")
                Result.Error("User not logged in") // Return error result
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error deleting task", e)
            Result.Error("Failed to delete task: ${e.message}") // Return error result with exception message
        }
    }

    suspend fun markTaskAsDone(taskId: String): Result<String> {
        return try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                userCollection
                    .document(userId)
                    .collection("tasks")
                    .document(taskId)
                    .update("done", true) // Update the isDone field to true
                    .await()

                Result.Success("Task Completed") // Return success result
            } else {
                Log.e("Firestore", "User not logged in")
                Result.Error("User not logged in") // Return error result
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error marking task as done", e)
            Result.Error("Failed to mark task as done: ${e.message}") // Return error result with exception message
        }
    }

    fun startTaskListener() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { uid ->
            taskListener = userCollection
                .document(uid)
                .collection("tasks")
                .whereEqualTo("done", false)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        // Handle error, e.g., log it
                        return@addSnapshotListener
                    }
                    snapshot?.let {
                        val taskItems = it.toObjects(Task::class.java)
                        Log.d(TAG, "startTaskListener: $taskItems")
                        _tasks.postValue(taskItems) // Update LiveData with the list of tasks
                    }
                }
        }
    }

    fun stopTaskListener() {
        taskListener?.remove()
    }


    companion object {
        private const val TAG = "Repository"
    }
}