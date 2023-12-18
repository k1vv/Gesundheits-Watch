package com.example.exercisesamplecompose

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.exercisesamplecompose.app.MainActivity
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BackgroundService : Service() {                                                                // Service use to send battery data in the background to users phone//

    private lateinit var batteryReceiver: BroadcastReceiver

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                    val batteryLevel = getBatteryLevel(context!!)
                    val greeting = Build.PRODUCT

                    println("Battery Level Changed: $batteryLevel")
                    println("Greeting: $greeting")

                    GlobalScope.launch {                                                            
                        sendToFirebase(greeting, batteryLevel)
                        setExerciseTrue()
                        sendToFirebase()
                    }
                }
            }
        }

        registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        startForeground(1, notification)

        GlobalScope.launch {
            while (true) {
                delay(5000)
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        unregisterReceiver(batteryReceiver)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "channel_id",
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, "channel_id")
            .setContentTitle("Your App Name")
            .setContentText("Running in the background")
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun getBatteryLevel(context: Context): Int {
        val batteryIntent = context.registerReceiver(
            null, IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )

        if (batteryIntent != null) {
            val level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

            if (level != -1 && scale != -1) {
                // Calculate battery level as a percentage
                return (level.toFloat() / scale.toFloat() * 100).toInt()
            }
        }

        return 0
    }

    private fun sendToFirebase(greeting: String, batteryLevel: Int) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("SmartWatch/3/")

        // Create a map to send multiple values to Firebase
        val dataMap = mapOf(
            "DeviceName" to greeting,
            "batteryLevel" to batteryLevel
        )

        myRef.setValue(dataMap)
    }

    fun setExerciseTrue() {
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("SmartWatch/3/ExerciseStatus")
        userRef.setValue(false)

        val database1 = FirebaseDatabase.getInstance()
        val userRef1 = database1.getReference("SmartWatch/3/RunStatus")
        userRef1.setValue(false)
    }

    private fun sendToFirebase() {
        val smartWatchRef = FirebaseDatabase.getInstance().reference.child("SmartWatch")
        val smartWatchId = "3" // Set the SmartWatch ID to "1"

        val smartWatchIdRef = smartWatchRef.child(smartWatchId)
        val exerciseSessionsRef = smartWatchIdRef.child("ExerciseSessions")

        // Use a fixed key for each exercise session (e.g., "001")
        val exerciseKey = "002"

        // Reference to the specific exercise session
        val exerciseRef = exerciseSessionsRef.child(exerciseKey)

        // Update the data in Firebase
        exerciseRef.child("HeartRate").setValue(0)
        exerciseRef.child("Distance").setValue(0)
        exerciseRef.child("Calories").setValue(0)
    }
}