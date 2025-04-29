package com.example.crmsystem

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject
import java.util.UUID
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

data class Notification(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val timestamp: Long = 0L
)

class NotificationCenterAdmin : AppCompatActivity() {
    private lateinit var db: DatabaseReference
    private lateinit var adapter: NotificationAdapter
    private lateinit var btnSend: Button
    private var isEditing = false
    private var editingNotificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_center_admin)

        db = FirebaseDatabase.getInstance().getReference("notifications")
        val etTitle = findViewById<EditText>(R.id.etNotificationTitle)
        val etMessage = findViewById<EditText>(R.id.etNotificationMessage)
        btnSend = findViewById<Button>(R.id.btnSendNotification)
        val rvNotifications = findViewById<RecyclerView>(R.id.rvNotifications)

        // Setup RecyclerView
        adapter = NotificationAdapter(
            onEdit = { notification ->
                isEditing = true
                editingNotificationId = notification.id
                etTitle.setText(notification.title)
                etMessage.setText(notification.message)
                btnSend.text = "Update Notification"
            },
            onDelete = { notification ->
                deleteNotification(notification.id)
            }
        )
        rvNotifications.layoutManager = LinearLayoutManager(this)
        rvNotifications.adapter = adapter

        // Load notifications
        loadNotifications()

        // Send or update notification
        btnSend.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val message = etMessage.text.toString().trim()
            if (title.isNotEmpty() && message.isNotEmpty()) {
                if (isEditing) {
                    editingNotificationId?.let { id ->
                        updateNotification(id, title, message)
                    }
                    isEditing = false
                    editingNotificationId = null
                    btnSend.text = "Send Notification"
                } else {
                    sendNotification(title, message)
                }
                etTitle.text.clear()
                etMessage.text.clear()
            } else {
                Toast.makeText(this, "Please enter title and message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendNotification(title: String, message: String) {
        val notification = Notification(
            id = UUID.randomUUID().toString(),
            title = title,
            message = message,
            timestamp = System.currentTimeMillis()
        )

        // Save to Realtime Database
        db.child(notification.id).setValue(notification)
            .addOnSuccessListener {
                Log.d("FCM", "Notification saved to database: ${notification.id}")
                Toast.makeText(this, "Notification saved", Toast.LENGTH_SHORT).show()
                // Send FCM notification
                sendFCMNotification(title, message)
            }
            .addOnFailureListener { e ->
                Log.e("FCM", "Failed to save notification: ${e.message}")
                Toast.makeText(this, "Failed to save notification: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateNotification(id: String, title: String, message: String) {
        val updatedNotification = mapOf(
            "title" to title,
            "message" to message,
            "timestamp" to System.currentTimeMillis()
        )
        db.child(id).updateChildren(updatedNotification)
            .addOnSuccessListener {
                Log.d("FCM", "Notification updated: $id")
                Toast.makeText(this, "Notification updated", Toast.LENGTH_SHORT).show()
                loadNotifications()
            }
            .addOnFailureListener { e ->
                Log.e("FCM", "Failed to update notification: ${e.message}")
                Toast.makeText(this, "Failed to update notification: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteNotification(id: String) {
        db.child(id).removeValue()
            .addOnSuccessListener {
                Log.d("FCM", "Notification deleted: $id")
                Toast.makeText(this, "Notification deleted", Toast.LENGTH_SHORT).show()
                loadNotifications()
            }
            .addOnFailureListener { e ->
                Log.e("FCM", "Failed to delete notification: ${e.message}")
                Toast.makeText(this, "Failed to delete notification: ${e.message}", Toast.LENGTH_SHORT).show()
            }
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
                Toast.makeText(this@NotificationCenterAdmin, "Failed to load notifications: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendFCMNotification(title: String, message: String) {
        val json = JSONObject().apply {
            put("to", "/topics/all_users")
            put("notification", JSONObject().apply {
                put("title", title)
                put("body", message)
            })
        }

        val client = OkHttpClient()
        val requestBody = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("https://fcm.googleapis.com/fcm/send")
            .post(requestBody)
            .addHeader("Authorization", "key=YOUR_FCM_SERVER_KEY") // Replace with your actual FCM server key
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Log.e("FCM", "Failed to send notification: ${e.message}")
                    Toast.makeText(this@NotificationCenterAdmin, "Failed to send notification: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Log.d("FCM", "Notification sent successfully: ${response.body?.string()}")
                        Toast.makeText(this@NotificationCenterAdmin, "Notification sent", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("FCM", "Failed to send notification: ${response.code} - ${response.body?.string()}")
                        Toast.makeText(this@NotificationCenterAdmin, "Failed to send notification: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}