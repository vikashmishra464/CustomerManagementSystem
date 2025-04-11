package com.example.crmsystem

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplaceScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splace_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, Signupform::class.java)
            startActivity(intent);
            finish()
        }, 3000);

    }
}