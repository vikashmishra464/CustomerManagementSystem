package com.example.crmsystem

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DashboardActivity : AppCompatActivity() {

    private lateinit var cardViewCustomers: CardView
    private lateinit var cardAddCustomer: CardView
    private lateinit var cardLeads: CardView
    private lateinit var cardReports: CardView
    private lateinit var cardSettings: CardView
    private lateinit var cardLogout: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        // Initialize card views
        cardViewCustomers = findViewById(R.id.card_customers)
        cardAddCustomer = findViewById(R.id.card_add_customer)
        cardLeads = findViewById(R.id.card_leads)
        cardReports = findViewById(R.id.card_reports)
        cardSettings = findViewById(R.id.card_settings)
        cardLogout = findViewById(R.id.card_logout)

        // Set click listeners
        cardViewCustomers.setOnClickListener {
            startActivity(Intent(this, CustomerListActivity::class.java))
        }

        cardAddCustomer.setOnClickListener {
            startActivity(Intent(this, AddCustomerActivity::class.java))
        }

        cardLeads.setOnClickListener {
            Toast.makeText(this, "Leads feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        cardReports.setOnClickListener {
            Toast.makeText(this, "Reports feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        cardSettings.setOnClickListener {
            Toast.makeText(this, "Settings feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        cardLogout.setOnClickListener {
            // Clear session or perform logout logic here
            val intent = Intent(this, Loginpage::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
        }

    }
}