package com.example.crmsystem

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging

class NotificationCenterUser : AppCompatActivity() {
    private lateinit var db: DatabaseReference
    private lateinit var adapter: NotificationAdapter

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d("FCM", if (isGranted) "Notification permission granted" else "Notification permission denied")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_center_user)

        db = FirebaseDatabase.getInstance().getReference("notifications")
        val rvNotifications = findViewById<RecyclerView>(R.id.rvNotifications)

        // Setup RecyclerView
        adapter = NotificationAdapter(
            onEdit = {}, // No edit for users
            onDelete = {} // No delete for users
        )
        rvNotifications.layoutManager = LinearLayoutManager(this)
        rvNotifications.adapter = adapter

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Subscribe to FCM topic
        FirebaseMessaging.getInstance().subscribeToTopic("all_users")
            .addOnCompleteListener { task ->
                Log.d("FCM", if (task.isSuccessful) "Subscribed to all_users" else "Subscription failed: ${task.exception?.message}")
            }

        // Load notifications
        loadNotifications()
    }

    private fun loadNotifications() {
        db.orderByChild("timestamp").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val notifications = mutableListOf<Notification>()
                for (child in snapshot.children) {
                    try {
                        val notification = child.getValue(Notification::class.java)
                        notification?.let { notifications.add(it) }
                    } catch (e: Exception) {
                        Log.e("FCM", "Failed to deserialize notification: ${e.message}")
                    }
                }
                Log.d("FCM", "Loaded ${notifications.size} notifications")
                adapter.submitList(notifications)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FCM", "Failed to load notifications: ${error.message}")
            }
        })
    }
}