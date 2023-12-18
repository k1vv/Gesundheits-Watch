package com.example.exercisesamplecompose.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.exercisesamplecompose.BackgroundService
import com.example.exercisesamplecompose.R
import com.example.exercisesamplecompose.presentation.exercise.ExerciseScreenState
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainPageActivity : AppCompatActivity() {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_mainpage)

        val serviceIntent = Intent(this, BackgroundService::class.java)
        startService(serviceIntent)
    }

    fun onConnectButtonClick(view: View) {
        // Navigate to ConnectActivity
        val intent = Intent(this, ConnectActivity::class.java)
        startActivity(intent)
    }

    fun onMainButtonClick(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}