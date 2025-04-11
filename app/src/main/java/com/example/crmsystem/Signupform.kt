package com.example.crmsystem

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Signupform : AppCompatActivity() {

    private lateinit var signupButton: AppCompatButton
    private lateinit var edFName: EditText
    private lateinit var edLName: EditText
    private lateinit var edEmail: EditText
    private lateinit var edPhone: EditText
    private lateinit var edPassword: EditText
    private lateinit var checkbox: CheckBox
    private lateinit var loginText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signupform)

        signupButton = findViewById(R.id.signupButton)
        edFName = findViewById(R.id.edFName)
        edLName = findViewById(R.id.edLName)
        edEmail = findViewById(R.id.edEmail)
        edPhone = findViewById(R.id.edPhone)
        edPassword = findViewById(R.id.edPassword)
        checkbox = findViewById(R.id.checkbox)
        loginText = findViewById(R.id.loginText)

        signupButton.setOnClickListener {
            val firstNameText = edFName.text.toString().trim()
            val lastNameText = edLName.text.toString().trim()
            val emailText = edEmail.text.toString().trim()
            val phoneText = edPhone.text.toString().trim()
            val passwordText = edPassword.text.toString().trim()
            val isTermsChecked = checkbox.isChecked

            when {
                TextUtils.isEmpty(firstNameText) -> {
                    edFName.error = "First name is required"
                    edFName.requestFocus()
                }
                TextUtils.isEmpty(emailText) -> {
                    edEmail.error = "Email is required"
                    edEmail.requestFocus()
                }
                TextUtils.isEmpty(phoneText) -> {
                    edPhone.error = "Phone number is required"
                    edPhone.requestFocus()
                }
                TextUtils.isEmpty(passwordText) -> {
                    edPassword.error = "Password is required"
                    edPassword.requestFocus()
                }
                !isTermsChecked -> {
                    Toast.makeText(this, "You must agree to the terms", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val intent = Intent(this, Loginpage::class.java)
                    startActivity(intent)
                }
            }
        }

        loginText.setOnClickListener {
            val intent = Intent(this, Loginpage::class.java)
            startActivity(intent)
            finish()
        }

    }
}