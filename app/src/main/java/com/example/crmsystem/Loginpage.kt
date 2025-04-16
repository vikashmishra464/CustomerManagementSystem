package com.example.crmsystem

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.crmsystem.databinding.ActivityLoginpageBinding
import com.google.firebase.auth.FirebaseAuth

class Loginpage : AppCompatActivity() {

    private lateinit var binding: ActivityLoginpageBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginpageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val createAccount = binding.createAccount
        val loginButton = binding.loginButton
        val edUserName = binding.edUserName
        val edPassword = binding.edPassword
        val forgotPasswordTextView = binding.textView

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
            val email = edUserName.text.toString().trim()
            val pass = edPassword.text.toString().trim()

            if (email.isNotEmpty() && pass.length > 5) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, DashboardActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

                    }
                }
            }
            else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

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