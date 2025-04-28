package com.example.crmsystem

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AddCustomerActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_customer)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Initialize UI elements
        firstNameEditText = findViewById(R.id.firstNameEditText)
        lastNameEditText = findViewById(R.id.lastNameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        submitButton = findViewById(R.id.submitButton)
        backButton = findViewById(R.id.backButton)

        // Set up submit button
        submitButton.setOnClickListener {
            addCustomer()
        }

        // Set up back button
        backButton.setOnClickListener {
            finish() // Close the activity and return to the previous screen
        }
    }

    private fun addCustomer() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Please log in to add a customer", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if current user is admin
        database.reference.child("users").child(currentUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        Toast.makeText(this@AddCustomerActivity, "User data not found", Toast.LENGTH_SHORT).show()
                        return
                    }
                    val role = snapshot.child("role").getValue(String::class.java)
                    if (role == "admin") {
                        // Proceed with adding customer
                        val firstName = firstNameEditText.text.toString().trim()
                        val lastName = lastNameEditText.text.toString().trim()
                        val email = emailEditText.text.toString().trim()
                        val phone = phoneEditText.text.toString().trim()

                        // Validate inputs
                        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                            Toast.makeText(this@AddCustomerActivity, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                            return
                        }

                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            Toast.makeText(this@AddCustomerActivity, "Please enter a valid email", Toast.LENGTH_SHORT).show()
                            return
                        }

                        if (phone.length < 10) {
                            Toast.makeText(this@AddCustomerActivity, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
                            return
                        }

                        // Generate unique UID for the customer
                        val uid = database.reference.child("users").push().key ?: return

                        // Create user object
                        val user = User(
                            firstName = firstName,
                            lastName = lastName,
                            email = email,
                            phone = phone,
                            role = "user"
                        )

                        // Save to database
                        database.reference.child("users").child(uid)
                            .setValue(user)
                            .addOnSuccessListener {
                                Toast.makeText(this@AddCustomerActivity, "Customer added successfully", Toast.LENGTH_SHORT).show()
                                // Clear input fields
                                firstNameEditText.text.clear()
                                lastNameEditText.text.clear()
                                emailEditText.text.clear()
                                phoneEditText.text.clear()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this@AddCustomerActivity, "Error adding customer: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this@AddCustomerActivity, "Access denied: Admins only", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@AddCustomerActivity, "Error checking admin status: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}