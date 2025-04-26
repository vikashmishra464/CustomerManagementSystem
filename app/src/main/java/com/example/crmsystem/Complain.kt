package com.example.crmsystem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

data class Complaint(
    val userId: String = "",
    val complaintText: String = "",
    val timestamp: Long = 0,
    val complaintId: String = ""
)

class Complain : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var complaintEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var backButton: Button
    private lateinit var complaintsRecyclerView: RecyclerView
    private lateinit var complaintsAdapter: ComplaintsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_complain)

        // Handle window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Initialize UI elements
        complaintEditText = findViewById(R.id.complaintEditText)
        submitButton = findViewById(R.id.submitButton)
        backButton = findViewById(R.id.backButton)
        complaintsRecyclerView = findViewById(R.id.complaintsRecyclerView)

        // Set up RecyclerView
        complaintsAdapter = ComplaintsAdapter()
        complaintsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@Complain)
            adapter = complaintsAdapter
        }

        // Load previous complaints
        loadPreviousComplaints()

        // Set up submit button
        submitButton.setOnClickListener {
            submitComplaint()
        }

        // Set up back button
        backButton.setOnClickListener {
            finish() // Close the activity and return to the previous screen
        }
    }

    private fun submitComplaint() {
        val complaintText = complaintEditText.text.toString().trim()
        val user = auth.currentUser

        if (user == null) {
            Toast.makeText(this, "Please log in to submit a complaint", Toast.LENGTH_SHORT).show()
            return
        }

        if (complaintText.isEmpty()) {
            Toast.makeText(this, "Please enter a complaint", Toast.LENGTH_SHORT).show()
            return
        }

        val complaint = Complaint(
            userId = user.uid,
            complaintText = complaintText,
            timestamp = System.currentTimeMillis(),
            complaintId = UUID.randomUUID().toString()
        )

        // Save complaint to Firebase Realtime Database
        database.reference.child("complaints").child(complaint.complaintId)
            .setValue(complaint)
            .addOnSuccessListener {
                Toast.makeText(this, "Complaint submitted successfully", Toast.LENGTH_SHORT).show()
                complaintEditText.text.clear()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error submitting complaint: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadPreviousComplaints() {
        val user = auth.currentUser
        if (user == null) {
            complaintsAdapter.submitList(emptyList())
            return
        }

        // Fetch complaints for the current user
        database.reference.child("complaints")
            .orderByChild("userId")
            .equalTo(user.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val complaints = mutableListOf<Complaint>()
                    for (data in snapshot.children) {
                        val complaint = data.getValue(Complaint::class.java)
                        complaint?.let { complaints.add(it) }
                    }
                    complaints.sortByDescending { it.timestamp }
                    complaintsAdapter.submitList(complaints)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@Complain, "Error loading complaints: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}

class ComplaintsAdapter : RecyclerView.Adapter<ComplaintsAdapter.ComplaintViewHolder>() {
    private var complaints: List<Complaint> = emptyList()

    class ComplaintViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val complaintText: TextView = itemView.findViewById(R.id.complaintText)
        val complaintDate: TextView = itemView.findViewById(R.id.complaintDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComplaintViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_complaint, parent, false)
        return ComplaintViewHolder(view)
    }

    override fun onBindViewHolder(holder: ComplaintViewHolder, position: Int) {
        val complaint = complaints[position]
        holder.complaintText.text = complaint.complaintText
        holder.complaintDate.text = SimpleDateFormat(
            "dd MMM yyyy, HH:mm",
            Locale.getDefault()
        ).format(Date(complaint.timestamp))
    }

    override fun getItemCount(): Int = complaints.size

    fun submitList(newComplaints: List<Complaint>) {
        complaints = newComplaints
        notifyDataSetChanged()
    }
}