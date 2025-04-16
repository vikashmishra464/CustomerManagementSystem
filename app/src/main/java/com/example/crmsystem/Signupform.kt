package com.example.crmsystem

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.crmsystem.databinding.ActivitySignupformBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Signupform : AppCompatActivity() {

    private lateinit var binding: ActivitySignupformBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupformBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        binding.signupButton.setOnClickListener {
            val firstName = binding.edFName.text.toString().trim()
            val lastName = binding.edLName.text.toString().trim()
            val email = binding.edEmail.text.toString().trim()
            val phone = binding.edPhone.text.toString().trim()
            val password = binding.edPassword.text.toString().trim()
            val isTermsChecked = binding.checkbox.isChecked

            if (TextUtils.isEmpty(firstName)) {
                binding.edFName.error = "First name is required"
            } else if (TextUtils.isEmpty(email)) {
                binding.edEmail.error = "Email is required"
            } else if (TextUtils.isEmpty(phone)) {
                binding.edPhone.error = "Phone is required"
            } else if (TextUtils.isEmpty(password)) {
                binding.edPassword.error = "Password is required"
            } else if (!isTermsChecked) {
                Toast.makeText(this, "Agree to terms", Toast.LENGTH_SHORT).show()
            } else {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val uid = firebaseAuth.currentUser?.uid ?: ""
                            val user = UserModel(firstName, lastName, email, phone)

                            database.reference.child("users").child(uid).setValue(user)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "User Registered", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, Loginpage::class.java))
                                    finish()
                                }
                        } else {
                            Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        binding.loginText.setOnClickListener {
            startActivity(Intent(this, Loginpage::class.java))
        }
    }
}
