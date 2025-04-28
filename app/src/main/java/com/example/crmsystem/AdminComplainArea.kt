package com.example.crmsystem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.DiffUtil

// Data class updated to include feedback
data class ComplaintWithFeedback(
    val userId: String = "",
    val complaintText: String = "",
    val timestamp: Long = 0,
    val complaintId: String = "",
    val feedback: String? = null
)

class AdminComplainArea : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var complaintsRecyclerView: RecyclerView
    private lateinit var adminComplaintsAdapter: AdminComplaintsAdapter
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_complain_area)

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase
        database = FirebaseDatabase.getInstance()

        // Initialize UI elements
        complaintsRecyclerView = findViewById(R.id.adminComplaintsRecyclerView)
        backButton = findViewById(R.id.adminBackButton)

        // Set up RecyclerView with DiffUtil
        adminComplaintsAdapter = AdminComplaintsAdapter { complaint, feedbackText ->
            submitFeedback(complaint, feedbackText)
        }
        complaintsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@AdminComplainArea)
            adapter = adminComplaintsAdapter
        }

        // Load all complaints
        loadAllComplaints()

        // Set up back button
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun submitFeedback(complaint: ComplaintWithFeedback, feedbackText: String) {
        if (feedbackText.isEmpty()) {
            Toast.makeText(this, "Please enter feedback", Toast.LENGTH_SHORT).show()
            return
        }

        // Update complaint with feedback in Firebase
        database.reference.child("complaints").child(complaint.complaintId)
            .child("feedback").setValue(feedbackText)
            .addOnSuccessListener {
                Toast.makeText(this, "Feedback submitted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error submitting feedback: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadAllComplaints() {
        database.reference.child("complaints")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val complaints = mutableListOf<ComplaintWithFeedback>()
                    for (data in snapshot.children) {
                        val complaint = data.getValue(ComplaintWithFeedback::class.java)
                        complaint?.let { complaints.add(it) }
                    }
                    complaints.sortByDescending { it.timestamp }
                    adminComplaintsAdapter.submitList(complaints)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@AdminComplainArea, "Error loading complaints: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}

class AdminComplaintsAdapter(
    private val onFeedbackSubmit: (ComplaintWithFeedback, String) -> Unit
) : RecyclerView.Adapter<AdminComplaintsAdapter.AdminComplaintViewHolder>() {
    private var complaints: List<ComplaintWithFeedback> = emptyList()

    class AdminComplaintViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val complaintText: TextView = itemView.findViewById(R.id.adminComplaintText)
        val complaintDate: TextView = itemView.findViewById(R.id.adminComplaintDate)
        val feedbackEditText: EditText = itemView.findViewById(R.id.feedbackEditText)
        val submitFeedbackButton: Button = itemView.findViewById(R.id.submitFeedbackButton)
        val feedbackText: TextView = itemView.findViewById(R.id.feedbackText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminComplaintViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_complaint, parent, false)
        return AdminComplaintViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminComplaintViewHolder, position: Int) {
        val complaint = complaints[position]
        holder.complaintText.text = complaint.complaintText
        holder.complaintDate.text = SimpleDateFormat(
            "dd MMM yyyy, HH:mm",
            Locale.getDefault()
        ).format(Date(complaint.timestamp))

        // Display existing feedback if any
        holder.feedbackText.text = complaint.feedback ?: "No feedback yet"
        holder.feedbackEditText.setText("")

        holder.submitFeedbackButton.setOnClickListener {
            val feedbackText = holder.feedbackEditText.text.toString().trim()
            onFeedbackSubmit(complaint, feedbackText)
        }
    }

    override fun getItemCount(): Int = complaints.size

    fun submitList(newComplaints: List<ComplaintWithFeedback>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize() = complaints.size
            override fun getNewListSize() = newComplaints.size
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                complaints[oldItemPosition].complaintId == newComplaints[newItemPosition].complaintId
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                complaints[oldItemPosition] == newComplaints[newItemPosition]
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        complaints = newComplaints
        diffResult.dispatchUpdatesTo(this)
    }
}