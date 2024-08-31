package com.mobile.itfest.ui.notes.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.mobile.itfest.databinding.ActivityNoteMainBinding
import com.mobile.itfest.ui.notes.selection.NoteTypeSelectionActivity

class NoteMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoteMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.fabAddNote.setOnClickListener {
            val intent = Intent(this, NoteTypeSelectionActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}