package com.example.crmsystem

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CustomerDetailActivity : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvCompany: TextView
    private lateinit var tvAddress: TextView
    private lateinit var tvNotes: TextView
    private lateinit var tvCreatedDate: TextView
    private lateinit var btnEdit: Button
    private lateinit var btnDelete: Button

    private var customerId: Int = -1
    private var customer: Customer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_customer_detail)

        tvName = findViewById(R.id.tvName)
        tvEmail = findViewById(R.id.tvEmail)
        tvPhone = findViewById(R.id.tvPhone)
        tvCompany = findViewById(R.id.tvCompany)
        tvAddress = findViewById(R.id.tvAddress)
        tvNotes = findViewById(R.id.tvNotes)
        tvCreatedDate = findViewById(R.id.tvCreatedDate)
        btnEdit = findViewById(R.id.btnEdit)
        btnDelete = findViewById(R.id.btnDelete)

        customerId = intent.getIntExtra("customer_id", -1)
        if (customerId != -1) {
            loadCustomerDetails(customerId)
        } else {
            Toast.makeText(this, "Customer not found", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnEdit.setOnClickListener {
            // Optional: Add navigation to EditCustomerActivity
            Toast.makeText(this, "Edit feature coming soon", Toast.LENGTH_SHORT).show()
        }

        btnDelete.setOnClickListener {
            confirmDelete()
        }
    }

    private fun loadCustomerDetails(id: Int) {
        lifecycleScope.launch {
            customer = withContext(Dispatchers.IO) {
                CustomerDatabase.getDatabase(applicationContext).customerDao().getCustomerById(id)
            }

            customer?.let {
                tvName.text = "Name: ${it.name}"
                tvEmail.text = "Email: ${it.email}"
                tvPhone.text = "Phone: ${it.phone}"
                tvCompany.text = "Company: ${it.company}"
                tvAddress.text = "Address: ${it.address}"
                tvNotes.text = "Notes: ${it.notes}"
                tvCreatedDate.text = "Created: ${it.createdDate}"
            }
        }
    }

    private fun confirmDelete() {
        AlertDialog.Builder(this)
            .setTitle("Delete Customer")
            .setMessage("Are you sure you want to delete this customer?")
            .setPositiveButton("Yes") { _, _ -> deleteCustomer() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteCustomer() {
        customer?.let {
            lifecycleScope.launch(Dispatchers.IO) {
                CustomerDatabase.getDatabase(applicationContext).customerDao().deleteCustomer(it)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CustomerDetailActivity, "Customer deleted", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

    }
}