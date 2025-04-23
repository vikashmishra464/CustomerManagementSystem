package com.example.crmsystem

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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

        firebaseAuth = FirebaseAuth.getInstance()
        val database = com.google.firebase.database.FirebaseDatabase.getInstance()
        val createAccount = binding.createAccount
        val loginButton = binding.loginButton
        val edUserName = binding.edUserName
        val edPassword = binding.edPassword
        val forgotPasswordTextView = binding.textView

        binding.loginButton.setOnClickListener {
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
                            val uid = firebaseAuth.currentUser?.uid
                            if (uid != null) {
                                // Get role from database
                                val ref = database.reference.child("users").child(uid)

                                ref.get().addOnSuccessListener { snapshot ->
                                    val role = snapshot.child("role").value.toString()

                                    if (role == "admin") {
                                        startActivity(Intent(this, DashboardActivity::class.java))
                                    } else if (role == "user") {
                                        startActivity(Intent(this, UserDashboard::class.java))
                                    } else {
                                        Toast.makeText(this, "Unknown role", Toast.LENGTH_SHORT).show()
                                    }
                                }.addOnFailureListener {
                                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                else{
                    Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT)
                        .show()
                }
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

