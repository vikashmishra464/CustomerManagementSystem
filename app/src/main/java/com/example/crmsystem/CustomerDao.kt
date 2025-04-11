package com.example.crm.data  // ✅ Replace with your actual package name

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Dao
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.example.crm.model.Customer  // ✅ Import your entity


@Dao
interface CustomerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer)

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Delete
    suspend fun deleteCustomer(customer: Customer)

    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAllCustomers(): LiveData<List<Customer>>

    @Query("SELECT * FROM customers WHERE id = :id")
    suspend fun getCustomerById(id: Int): Customer
}
