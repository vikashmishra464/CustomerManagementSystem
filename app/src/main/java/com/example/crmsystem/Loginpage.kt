package com.example.crmsystem

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Loginpage : AppCompatActivity() {

    private lateinit var createAccount: TextView
    private lateinit var loginButton: AppCompatButton
    private lateinit var edUserName: EditText
    private lateinit var edPassword: EditText
    private lateinit var forgotPasswordTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_loginpage)

        createAccount = findViewById(R.id.createAccount)
        loginButton = findViewById(R.id.loginButton)
        edUserName = findViewById(R.id.edUserName)
        edPassword = findViewById(R.id.edPassword)
        forgotPasswordTextView = findViewById(R.id.textView)

        loginButton.setOnClickListener {
            val username = edUserName.text.toString().trim()
            val password = edPassword.text.toString().trim()

            if (username.isNotEmpty() && password.length > 5) {
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                edUserName.error = "Please enter username or e-mail"
                edPassword.error = "Please enter correct password"
            }
        }

        createAccount.setOnClickListener {
            val intent = Intent(this, Signupform::class.java)
            startActivity(intent)
            finish()
        }

        forgotPasswordTextView.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

    }
}