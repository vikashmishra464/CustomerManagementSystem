import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.crmsystem.R

class AddCustomerActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etAddress: EditText
    private lateinit var etNotes: EditText
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_customer)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etAddress = findViewById(R.id.etAddress)
        etNotes = findViewById(R.id.etNotes)
        btnSave = findViewById(R.id.btnSave)

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val address = etAddress.text.toString().trim()
            val notes = etNotes.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Name, Email & Phone are required", Toast.LENGTH_SHORT).show()
            } else {
                // Save to SharedPreferences
                val sharedPreferences = getSharedPreferences("CustomerData", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()

                editor.putString("name", name)
                editor.putString("email", email)
                editor.putString("phone", phone)
                editor.putString("address", address)
                editor.putString("notes", notes)
                editor.apply()

                Toast.makeText(this, "Customer Saved!", Toast.LENGTH_SHORT).show()

                // Go back or open list
                finish() // or startActivity(Intent(this, CustomerListActivity::class.java))
            }
        }
    }
}
