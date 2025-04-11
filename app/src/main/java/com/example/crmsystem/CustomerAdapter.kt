// CustomerAdapter.kt
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.crm.model.Customer
import com.example.crmsystem.CustomerDetailActivity
import com.example.crmsystem.R

class CustomerAdapter(private val customerList: List<Customer>) :
    RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder>() {

    // ViewHolder class to hold each item layout
    inner class CustomerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvName)
        val phone: TextView = view.findViewById(R.id.tvPhone)
        val email: TextView = view.findViewById(R.id.tvEmail)
    }

    // Create view for each item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_customer, parent, false)
        return CustomerViewHolder(view)
    }

    // Bind data to the view
    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val customer = customerList[position]
        holder.name.text = customer.name
        holder.phone.text = "📞 " + customer.phone
        holder.email.text = "✉️ " + customer.email

        // 👇 Add this click listener to open details
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, CustomerDetailActivity::class.java)
            intent.putExtra("customer_id", customer.id) // Make sure 'id' exists in Customer
            context.startActivity(intent)
        }
    }


    // Return the total number of items
    override fun getItemCount(): Int = customerList.size
}
