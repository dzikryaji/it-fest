package com.mobile.itfest.ui.main.timer

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.mobile.itfest.R
import com.mobile.itfest.adapter.TaskAdapter
import com.mobile.itfest.data.Result
import com.mobile.itfest.data.model.Task
import com.mobile.itfest.databinding.DialogTaskBinding
import com.mobile.itfest.databinding.FragmentTimerBinding
import com.mobile.itfest.ui.ViewModelFactory
import com.mobile.itfest.ui.focus.FocusActivity
import com.mobile.itfest.ui.main.MainViewModel
import com.mobile.itfest.utils.ToastManager.showToast
import com.mobile.itfest.utils.dp
import java.util.Calendar
import java.util.UUID


class TimerFragment : Fragment(), TaskAdapter.TaskClickListener {
    private lateinit var binding: FragmentTimerBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    private lateinit var adapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        setObserve()
        setListener()
    }

    private fun setListener() {
        binding.btnTimer.setOnClickListener {
            val intent = Intent(requireActivity(), FocusActivity::class.java)

            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity() as Activity,
                    Pair(binding.llTimer, "timer"),
                    Pair(binding.tvHours, "hours"),
                    Pair(binding.tvMinutes, "minutes"),
                    Pair(binding.tvSeconds, "seconds"),
                )

            requireActivity().startActivity(intent, optionsCompat.toBundle())
        }

        binding.btnTask.setOnClickListener {
            showCustomDialog()
        }


    }

    private fun setObserve() {
        viewModel.retrieveUser().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    val user = result.data
                    Log.d("TimerFragment", "User retrieved: ${user.name}")
                    binding.tvUser.text = "Hello, ${user.name}!"
                }

                is Result.Loading -> {
                    Log.d("TimerFragment", "Loading user data")
                }

                is Result.Error -> {
                    Log.e("TimerFragment", "Error retrieving user: ${result.error}")
                }
            }
        }

        viewModel.tasks.observe(viewLifecycleOwner) {tasks ->
            tasks?.let {
                val sortedTasks = it.sortedBy { task ->
                    task.deadline.toDate()
                }
                adapter.submitList(sortedTasks)
            }
        }
    }

    private fun setAdapter() {
        adapter = TaskAdapter(this)
        binding.rvTask.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvTask.adapter = adapter
    }

    private fun showCustomDialog(task: Task? = null) {
        // Use view binding to inflate the custom dialog layout
        val binding = DialogTaskBinding.inflate(layoutInflater)

        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)

        // Set a custom background if needed
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)

        Log.d("Dialog", "showCustomDialog: bisa")

        // Get the current window attributes
        val layoutParams = dialog.window?.attributes
        layoutParams?.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams?.height = WindowManager.LayoutParams.WRAP_CONTENT

        // Apply margins directly
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        // Wrap the dialog's root view in a FrameLayout to apply margins
        val marginInPx = 16.dp
        val layout = dialog.window?.decorView as? ViewGroup
        layout?.setPadding(marginInPx, marginInPx, marginInPx, marginInPx)

        binding.apply {
            btnAdd.setOnClickListener {
                // Validate fields
                val name = edName.text.toString().trim()
                val deadlineText = edDeadline.text.toString().trim()
                val clockText = edClock.text.toString().trim()

                if (name.isEmpty() || deadlineText.isEmpty() || clockText.isEmpty()) {
                    // Show a toast message if any field is empty
                    Toast.makeText(
                        requireContext(),
                        "Please fill in all fields",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                // Parse the deadline to a Timestamp
                val parts = deadlineText.split("/")
                if (parts.size != 3) {
                    Toast.makeText(requireContext(), "Invalid date format", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }

                val day = parts[0].toIntOrNull() ?: return@setOnClickListener
                val month = parts[1].toIntOrNull()?.minus(1) ?: return@setOnClickListener // Months are zero-based
                val year = parts[2].toIntOrNull() ?: return@setOnClickListener

                // Parse the time from clockText
                val timeParts = clockText.split(":")
                if (timeParts.size != 2) {
                    Toast.makeText(requireContext(), "Invalid time format", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }

                val hour = timeParts[0].toIntOrNull() ?: return@setOnClickListener
                val minute = timeParts[1].toIntOrNull() ?: return@setOnClickListener

                // Create a Calendar instance for the deadline
                val calendar = Calendar.getInstance().apply {
                    set(year, month, day, hour, minute, 0) // Set the time as well
                }

                // Check if the selected date and time is in the past
                if (calendar.timeInMillis < System.currentTimeMillis()) {
                    Toast.makeText(requireContext(), "Deadline cannot be in the past", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val deadline = Timestamp(calendar.time)

                val taskId = task?.id ?: UUID.randomUUID().toString()

                // Create a Task object
                val newTask = Task(
                    id = taskId,
                    name = name,
                    deadline = deadline,
                    isDone = false
                )

                // Upload to Firestore
                viewModel.uploadTask(newTask).observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is Result.Success -> {
                            showToast(requireActivity(), result.data)
                            dialog.dismiss()
                        }

                        is Result.Loading -> {
                            Log.d("TimerFragment", "Loading upload data")
                        }

                        is Result.Error -> {
                            showToast(requireActivity(), result.error)
                            dialog.dismiss()
                        }
                    }
                }
            }

            edDeadline.setOnClickListener {
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(
                    requireActivity(),
                    { _, selectedYear, selectedMonth, selectedDay ->
                        val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                        edDeadline.setText(formattedDate)
                    },
                    year,
                    month,
                    day
                )

                // Set minimum date to prevent past date selection
                datePickerDialog.datePicker.minDate = calendar.timeInMillis
                datePickerDialog.show()
            }

            edClock.setOnClickListener {
                val calendar = Calendar.getInstance()
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)

                val timePickerDialog = TimePickerDialog(
                    requireActivity(),
                    { _, selectedHour, selectedMinute ->
                        val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                        edClock.setText(formattedTime)
                    },
                    hour,
                    minute,
                    true
                )

                timePickerDialog.show()
            }

        }

        dialog.show()
    }

    companion object{
        private const val TAG = "TimerFragment"
    }

    override fun onEditClick(task: Task) {
        showCustomDialog(task)
    }

    override fun onDeleteClick(task: Task) {
        viewModel.deleteTask(task).observe(viewLifecycleOwner){ result ->
            when (result) {
                is Result.Success -> {
                    showToast(requireActivity(), result.data)
                }

                is Result.Loading -> {
                    Log.d("TimerFragment", "Loading upload data")
                }

                is Result.Error -> {
                    showToast(requireActivity(), result.error)
                }
            }
        }
    }

    override fun onMarkAsDoneClick(task: Task) {
        viewModel.markTaskAsDone(task).observe(viewLifecycleOwner){ result ->
            when (result) {
                is Result.Success -> {
                    showToast(requireActivity(), result.data)
                }

                is Result.Loading -> {
                    Log.d("TimerFragment", "Loading upload data")
                }

                is Result.Error -> {
                    showToast(requireActivity(), result.error)
                }
            }
        }
    }
}