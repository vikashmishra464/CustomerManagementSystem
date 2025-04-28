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
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import android.widget.Button
import androidx.appcompat.app.AlertDialog

data class User(
    val uid: String = "",
    val firstName: String = "Unknown",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = ""
)

class ViewCustomers : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var customersRecyclerView: RecyclerView
    private lateinit var customersAdapter: CustomersAdapter
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_customers)

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
        customersRecyclerView = findViewById(R.id.customersRecyclerView)
        backButton = findViewById(R.id.backButton)

        // Set up RecyclerView
        customersAdapter = CustomersAdapter { user ->
            showUserDetailsDialog(user)
        }
        customersRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ViewCustomers)
            adapter = customersAdapter
        }

        // Load users
        loadUsers()

        // Set up back button
        backButton.setOnClickListener {
            finish() // Close the activity and return to the previous screen
        }
    }

    private fun loadUsers() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Please log in to view users", Toast.LENGTH_SHORT).show()
            customersAdapter.submitList(emptyList())
            return
        }

        // Check if user is admin
        database.reference.child("users").child(currentUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        Toast.makeText(this@ViewCustomers, "User data not found", Toast.LENGTH_SHORT).show()
                        customersAdapter.submitList(emptyList())
                        return
                    }
                    val role = snapshot.child("role").getValue(String::class.java)
                    if (role == "admin") {
                        // Fetch users with role "user"
                        database.reference.child("users")
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val users = mutableListOf<User>()
                                    for (data in snapshot.children) {
                                        val user = data.getValue(User::class.java)
                                        if (user != null && user.role == "user") {
                                            users.add(user.copy(uid = data.key ?: ""))
                                        }
                                    }
                                    customersAdapter.submitList(users)
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(this@ViewCustomers, "Error loading users: ${error.message}", Toast.LENGTH_SHORT).show()
                                }
                            })
                    } else {
                        Toast.makeText(this@ViewCustomers, "Access denied: Admins only", Toast.LENGTH_SHORT).show()
                        customersAdapter.submitList(emptyList())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ViewCustomers, "Error checking admin status: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showUserDetailsDialog(user: User) {
        val message = """
            First Name: ${user.firstName}
            Last Name: ${user.lastName}
            Email: ${user.email}
            Phone: ${user.phone}
            UID: ${user.uid}
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("User Details")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }
}

class CustomersAdapter(private val onUserClick: (User) -> Unit) : RecyclerView.Adapter<CustomersAdapter.CustomerViewHolder>() {
    private var users: List<User> = emptyList()

    class CustomerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.nameText)
        val emailText: TextView = itemView.findViewById(R.id.emailText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_customer, parent, false)
        return CustomerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val user = users[position]
        holder.nameText.text = user.firstName
        holder.emailText.text = user.email
        holder.itemView.setOnClickListener {
            onUserClick(user)
        }
    }

    override fun getItemCount(): Int = users.size

    fun submitList(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }
}