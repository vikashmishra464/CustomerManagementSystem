package com.example.crmsystem

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.crmsystem.databinding.ActivityLoginpageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Loginpage : AppCompatActivity() {

    private lateinit var binding: ActivityLoginpageBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginpageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()

        // Check if user is already logged in
        checkIfUserIsLoggedIn()

        val createAccount = binding.createAccount
        val loginButton = binding.loginButton
        val edUserName = binding.edUserName
        val edPassword = binding.edPassword
        val forgotPasswordTextView = binding.textView

        loginButton.setOnClickListener {
            val email = edUserName.text.toString().trim()
            val pass = edPassword.text.toString().trim()

            if (TextUtils.isEmpty(email)) {
                binding.edUserName.error = "Email is required"
            } else if (TextUtils.isEmpty(pass)) {
                binding.edPassword.error = "Password is required"
            } else {
                if (email.isNotEmpty() && pass.length > 5) {
                    firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            navigateToDashboard(firebaseAuth.currentUser?.uid)
                        } else {
                            Toast.makeText(this, it.exception?.message ?: "Login failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        createAccount.setOnClickListener {
            val intent = Intent(this, Signupform::class.java)
            startActivity(intent)
        }

        forgotPasswordTextView.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkIfUserIsLoggedIn() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            // User is already logged in, navigate to dashboard
            navigateToDashboard(currentUser.uid)
        }
    }

    private fun navigateToDashboard(uid: String?) {
        if (uid == null) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
            return
        }

        val database = FirebaseDatabase.getInstance()
        val ref = database.reference.child("users").child(uid)

        ref.get().addOnSuccessListener { snapshot ->
            val role = snapshot.child("role").value.toString()
            val intent = when (role) {
                "admin" -> Intent(this, DashboardActivity::class.java)
                "user" -> Intent(this, UserDashboard::class.java)
                else -> null
            }

            if (intent != null) {
                intent.putExtra("UID", uid)
                startActivity(intent)
                finish() // Close Loginpage
            } else {
                Toast.makeText(this, "Unknown role", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
}