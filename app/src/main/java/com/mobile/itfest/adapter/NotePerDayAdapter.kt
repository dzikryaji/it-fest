package com.mobile.itfest.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.mobile.itfest.data.model.Note
import com.mobile.itfest.databinding.ItemNotesPerDayBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NotePerDayAdapter :
    ListAdapter<List<Note>, NotePerDayAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewHolder(
        ItemNotesPerDayBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private fun isNoteEditedToday(note: Note): Boolean {
        val noteDate = note.dateLastEdited.toDate()
        val today = Calendar.getInstance()

        return (noteDate.year == today.get(Calendar.YEAR) &&
                noteDate.month == today.get(Calendar.MONTH) &&
                noteDate.date == today.get(Calendar.DAY_OF_MONTH))
    }

    private fun isNoteEditedYesterday(note: Note): Boolean {
        val noteDate = note.dateLastEdited.toDate()
        val yesterday = Calendar.getInstance()
        yesterday.add(Calendar.DAY_OF_YEAR, -1)

        return (noteDate.year == yesterday.get(Calendar.YEAR) &&
                noteDate.month == yesterday.get(Calendar.MONTH) &&
                noteDate.date == yesterday.get(Calendar.DAY_OF_MONTH))
    }

    private fun formatTimestampToString(timestamp: Timestamp): String {
        val date = timestamp.toDate()
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

    inner class MyViewHolder(private val binding: ItemNotesPerDayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val adapter = NoteAdapter()

        fun bind(noteList: List<Note>) {
            binding.apply {
                tvDate.text = when {
                    isNoteEditedToday(noteList.first()) -> "Today"
                    isNoteEditedYesterday(noteList.first()) -> "Yesterday"
                    else -> formatTimestampToString(noteList.first().dateLastEdited)
                }

                Log.d(TAG, "bind: $noteList")

                rvItemNotes.layoutManager = LinearLayoutManager(itemView.context)
                rvItemNotes.adapter = adapter
                adapter.submitList(noteList)
            }
        }
    }

    companion object {
        private const val TAG = "NotePerDayAdapter"
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<List<Note>>() {
            override fun areItemsTheSame(oldItem: List<Note>, newItem: List<Note>): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: List<Note>, newItem: List<Note>): Boolean {
                return oldItem.first() == newItem.first()
            }
        }
    }
}