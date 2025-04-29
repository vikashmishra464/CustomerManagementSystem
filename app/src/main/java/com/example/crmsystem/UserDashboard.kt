package com.example.crmsystem

import android.content.Intent
import android.os.Bundle
import android.widget.RelativeLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class UserDashboard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_dashboard)
        val uid=intent.getStringExtra("UID")
        var userprofile=findViewById<RelativeLayout>(R.id.profilebox)
        var complains=findViewById<RelativeLayout>(R.id.complainbox)
        var notification=findViewById<RelativeLayout>(R.id.notificationBox)
        var coupon=findViewById<RelativeLayout>(R.id.couponBox)
        var feedback=findViewById<RelativeLayout>(R.id.feedbackBox)
        userprofile.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            intent.putExtra("UID", uid)
            startActivity(intent)
        }
        complains.setOnClickListener{
            val intent = Intent(this,Complain::class.java)
            startActivity(intent)
        }
        notification.setOnClickListener{
            val intent = Intent(this,NotificationCenterUser::class.java)
            startActivity(intent)
        }
        coupon.setOnClickListener{}
        feedback.setOnClickListener{}


    }
}