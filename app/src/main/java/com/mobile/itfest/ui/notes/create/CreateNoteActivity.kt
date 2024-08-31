package com.mobile.itfest.ui.notes.create

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mobile.itfest.data.Result
import com.mobile.itfest.data.model.Note
import com.mobile.itfest.databinding.ActivityCreateNoteBinding
import com.mobile.itfest.ui.ViewModelFactory
import com.mobile.itfest.utils.ToastManager.showToast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class CreateNoteActivity : AppCompatActivity() {
    private val viewModel by viewModels<CreateNoteViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityCreateNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
        setListener()
    }

    private fun setView() {
        binding.apply {
            tvDate.text = "Date: ${getTodayDate()}"
        }
    }

    private fun setListener() {
        binding.ivBack.setOnClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
        binding.apply {
            val title = etTitle.text.toString().trim()
            val agenda = etAgenda.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val noteId = UUID.randomUUID().toString()

            if (title.isEmpty() && agenda.isEmpty() && description.isEmpty()) {
                return@apply
            }

            val note = Note(
                id = noteId,
                title = title.ifEmpty { "Title" },
                agenda = agenda.ifEmpty { "Agenda" },
                description = description
            )

            viewModel.uploadNote(note).observe(this@CreateNoteActivity) { result ->
                when (result) {
                    is Result.Success -> {
                        showToast(applicationContext, result.data)
                        super.onBackPressed()
                    }

                    is Result.Loading -> {
                        Log.d("TimerFragment", "Loading upload data")
                    }

                    is Result.Error -> {
                        showToast(applicationContext, result.error)
                        super.onBackPressed()
                    }
                }
            }
        }
    }

    private fun getTodayDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}