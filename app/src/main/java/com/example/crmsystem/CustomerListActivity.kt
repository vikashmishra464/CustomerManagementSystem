package com.example.crmsystem

import CustomerAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crm.model.Customer
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CustomerListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var adapter: CustomerAdapter
    private val customers = mutableListOf<Customer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_list)

        recyclerView = findViewById(R.id.recyclerView)
        fab = findViewById(R.id.fabAdd)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Sample customers
        customers.add(Customer(1, "Himanshu Sharma", "9876543210", "himanshu@example.com"))
        customers.add(Customer(2, "Sumant Kumar", "9998887776", "sumant@example.com"))

        adapter = CustomerAdapter(customers)
        recyclerView.adapter = adapter

        fab.setOnClickListener {
            Toast.makeText(this, "➕ Add New Customer clicked", Toast.LENGTH_SHORT).show()
            // Later: Open a form dialog or activity

        }
    }
}
