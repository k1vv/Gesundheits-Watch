package com.example.exercisesamplecompose.presentation.summary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.exercisesamplecompose.app.Screen
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val uiState = MutableStateFlow(
        SummaryScreenState(
            averageHeartRate = savedStateHandle.get<Float>(Screen.Summary.averageHeartRateArg)!!
                .toDouble(),
            totalDistance = savedStateHandle.get<Float>(Screen.Summary.totalDistanceArg)!!
                .toDouble(),
            totalCalories = savedStateHandle.get<Float>(Screen.Summary.totalCaloriesArg)!!
                .toDouble(),
            elapsedTime = Duration.parse(savedStateHandle[Screen.Summary.elapsedTimeArg]!!),
        )
    )

    fun saveDataToFirebase(heartRate: Double) {
        val database = FirebaseDatabase.getInstance()
        val userId = "Exercise" // You should have a user ID to associate data with a specific user

        // Reference to the user's data node in the database
        val userRef = database.getReference("Watch").child(userId)

        // Create a data object to be saved
        val workoutData = mapOf(
            "averageHeartRate" to heartRate,

            // Add any other data you want to save
        )

        // Save data to Firebase
        userRef.setValue(workoutData)
    }
}