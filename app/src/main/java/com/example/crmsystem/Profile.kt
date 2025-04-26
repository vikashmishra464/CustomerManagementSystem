package com.example.crmsystem

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.crmsystem.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Profile : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database.reference.child("users")

        // Check if user is logged in
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, Loginpage::class.java))
            finish()
            return
        }
        // Show loading progress
        binding.progressBar.visibility = View.VISIBLE

        // Fetch user data
        val uid = currentUser.uid
        databaseReference.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.progressBar.visibility = View.GONE
                if (snapshot.exists()) {
                    val user = snapshot.getValue(UserModel::class.java)
                    if (user != null) {
                        // Display user details
                        binding.tvFirstName.text = user.firstName
                        binding.tvLastName.text = user.lastName
                        binding.tvEmail.text = user.email
                        binding.tvPhone.text = user.phone
                        binding.tvRole.text = user.role
                    } else {
                        Toast.makeText(this@Profile, "Failed to load user data", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@Profile, "User data not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@Profile, error.message, Toast.LENGTH_SHORT).show()
            }
        })

        // Logout button click listener
        binding.btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, Loginpage::class.java))
            finish()
        }

        // Back button click listener
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}