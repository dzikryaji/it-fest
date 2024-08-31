package com.mobile.itfest.ui.notes.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.itfest.adapter.NotePerDayAdapter
import com.mobile.itfest.data.model.Note
import com.mobile.itfest.databinding.ActivityNoteMainBinding
import com.mobile.itfest.ui.ViewModelFactory
import com.mobile.itfest.ui.notes.selection.NoteTypeSelectionActivity
import java.text.SimpleDateFormat
import java.util.Locale

class NoteMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoteMainBinding
    private val viewModel by viewModels<NoteViewModel> { ViewModelFactory.getInstance(this) }
    private lateinit var adapter: NotePerDayAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        setListener()
        setAdapter()
        viewModel.startNoteListener()
        setObserve()
    }

    private fun setAdapter(){
        adapter = NotePerDayAdapter()
        binding.rvNotesPerDay.adapter = adapter
        binding.rvNotesPerDay.layoutManager = LinearLayoutManager(this)
    }

    private fun setObserve() {
        viewModel.notes.observe(this) {
            val groupedNotes = groupNotesByDate(it)
            Log.d(TAG, "Grouped Notes: $groupedNotes")
            adapter.submitList(groupedNotes)
            Log.d(TAG, "Item count: ${adapter.itemCount}")
        }
    }

    private fun setListener() {
        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.fabAddNote.setOnClickListener {
            val intent = Intent(this, NoteTypeSelectionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun groupNotesByDate(notes: List<Note>): List<List<Note>> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val groupedNotes = notes.groupBy { note ->
            dateFormat.format(note.dateLastEdited.toDate())
        }
        return groupedNotes.toSortedMap().map { (_, groupNotes) ->
            groupNotes.sortedBy { it.dateCreated }
        }.reversed()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopNoteListener()
    }

    companion object{
        private const val TAG = "NoteMainActivity"
    }
}