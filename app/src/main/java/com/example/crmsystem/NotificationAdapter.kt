package com.example.crmsystem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotificationAdapter(
    private val onEdit: (Notification) -> Unit,
    private val onDelete: (Notification) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {
    private var notifications: List<Notification> = emptyList()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notifications[position]
        holder.tvTitle.text = notification.title
        holder.tvMessage.text = notification.message
        holder.btnEdit.setOnClickListener { onEdit(notification) }
        holder.btnDelete.setOnClickListener { onDelete(notification) }
    }

    override fun getItemCount(): Int = notifications.size

    fun submitList(newNotifications: List<Notification>) {
        notifications = newNotifications
        notifyDataSetChanged()
    }
}