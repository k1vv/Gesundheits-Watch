
package com.example.exercisesamplecompose.presentation.exercise

import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exercisesamplecompose.data.HealthServicesRepository
import com.example.exercisesamplecompose.data.ServiceState
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import android.os.Handler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val healthServicesRepository: HealthServicesRepository
) : ViewModel() {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private var timerRunning = false
    private var elapsedTimeInSeconds = 0L
    private val timerHandler = Handler(Looper.getMainLooper())

    val uiState: StateFlow<ExerciseScreenState> = healthServicesRepository.serviceState.map {
        ExerciseScreenState(
            hasExerciseCapabilities = healthServicesRepository.hasExerciseCapability(),
            isTrackingAnotherExercise = healthServicesRepository.isTrackingExerciseInAnotherApp(),
            serviceState = it,
            exerciseState = (it as? ServiceState.Connected)?.exerciseServiceState
        )
    }
        .onEach { sendToFirebase(it) }
        .stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(3_000),
        healthServicesRepository.serviceState.value.let {
            ExerciseScreenState(
                true,
                false,
                it,
                (it as? ServiceState.Connected)?.exerciseServiceState
            )
        }

    )

    suspend fun isExerciseInProgress(): Boolean {
        return healthServicesRepository.isExerciseInProgress()
    }

    fun startExercise() {
        setExerciseTrue(start = true)
        startTimer()
        healthServicesRepository.startExercise()
    }

    fun pauseExercise() {
        setPauseTrue(start = true)
        stopTimer()
        healthServicesRepository.pauseExercise()
    }

    fun endExercise() {
        setExerciseTrue(start = false)
        stopTimer()
        healthServicesRepository.endExercise()
    }

    fun resumeExercise() {
        setPauseTrue(start = false)
        startTimer()
        healthServicesRepository.resumeExercise()
    }

    private fun startTimer() {
        if (!timerRunning) {
            timerRunning = true
            elapsedTimeInSeconds = 0L
            viewModelScope.launch {
                while (timerRunning) {
                    delay(1000) // Update every second
                    elapsedTimeInSeconds++
                    sendElapsedTimeToFirebase(elapsedTimeInSeconds)
                }
            }
        }
    }

    private fun stopTimer() {
        timerRunning = false
    }

    private fun sendElapsedTimeToFirebase(elapsedTimeInSeconds: Long) {
        val smartWatchRef = database.child("SmartWatch")
        val smartWatchId = "3" // Set the SmartWatch ID to "1"

        val smartWatchIdRef = smartWatchRef.child(smartWatchId)
        val exerciseSessionsRef = smartWatchIdRef.child("ExerciseSessions")

        val exerciseKey = "002"
        val exerciseRef = exerciseSessionsRef.child(exerciseKey)

        exerciseRef.child("ElapsedTime").setValue(elapsedTimeInSeconds)
    }

    private fun sendToFirebase(exerciseScreenState: ExerciseScreenState) {
        val smartWatchRef = database.child("SmartWatch")
        val smartWatchId = "3" // Set the SmartWatch ID to "1"

        val smartWatchIdRef = smartWatchRef.child(smartWatchId)
        val exerciseSessionsRef = smartWatchIdRef.child("ExerciseSessions")

        // Use a fixed key for each exercise session (e.g., "001")
        val exerciseKey = "002"

        // Reference to the specific exercise session
        val exerciseRef = exerciseSessionsRef.child(exerciseKey)

        // Update the data in Firebase
        val timestampInSeconds = System.currentTimeMillis() / 1000
        exerciseRef.child("Timestamp").setValue(timestampInSeconds)

        // Extract duration from activeDurationCheckpoint
        val durationInSeconds = exerciseScreenState.exerciseState?.activeDurationCheckpoint?.activeDuration?.seconds
        exerciseRef.child("Duration").setValue(durationInSeconds)

        exerciseRef.child("HeartRate").setValue(exerciseScreenState.exerciseState?.exerciseMetrics?.heartRate)
        exerciseRef.child("Distance").setValue(exerciseScreenState.exerciseState?.exerciseMetrics?.distance)
        exerciseRef.child("Calories").setValue(exerciseScreenState.exerciseState?.exerciseMetrics?.calories)
    }

    fun setExerciseTrue(start: Boolean) {
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("SmartWatch/3/ExerciseStatus")
        userRef.setValue(start)

        val database1 = FirebaseDatabase.getInstance()
        val userRef1 = database1.getReference("SmartWatch/3/RunStatus")
        userRef1.setValue(false)
    }
    fun setPauseTrue(start: Boolean) {
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("SmartWatch/3/RunStatus")
        userRef.setValue(start)
    }
}



