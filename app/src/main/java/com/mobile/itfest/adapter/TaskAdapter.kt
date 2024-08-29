package com.mobile.itfest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.mobile.itfest.data.model.Task
import com.mobile.itfest.databinding.ItemTaskBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TaskAdapter(private val listener: TaskClickListener): ListAdapter<Task, TaskAdapter.MyViewHolder>(DIFF_CALLBACK) {

    interface TaskClickListener {
        fun onEditClick(task: Task)
        fun onDeleteClick(task: Task)
        fun onMarkAsDoneClick(task: Task)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewHolder(
        ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    fun formatTimestampToString(timestamp: Timestamp): String {
        // Convert Firestore Timestamp to Date
        val date = timestamp.toDate()

        // Create a SimpleDateFormat to format the date
        val dateFormat = SimpleDateFormat("dd MMMM yyyy | HH:mm", Locale.getDefault())

        // Format the date and return the resulting string
        return dateFormat.format(date)
    }

    inner class MyViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.apply {
                tvName.text = task.name
                tvDeadline.text = formatTimestampToString(task.deadline)
                btnEdit.setOnClickListener {
                    listener.onEditClick(task)
                }

                btnDelete.setOnClickListener {
                    listener.onDeleteClick(task)
                }

                btnCheck.setOnClickListener {
                    listener.onMarkAsDoneClick(task)
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(
                oldItem: Task,
                newItem: Task
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: Task,
                newItem: Task
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}