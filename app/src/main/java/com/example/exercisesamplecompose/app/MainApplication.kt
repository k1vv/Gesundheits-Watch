package com.example.exercisesamplecompose.app

import android.app.Application
import android.content.Intent
import com.example.exercisesamplecompose.BackgroundService
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ExerciseSampleApplication : Application()