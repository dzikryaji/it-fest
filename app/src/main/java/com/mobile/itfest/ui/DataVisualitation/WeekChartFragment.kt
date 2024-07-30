package com.mobile.itfest.ui.DataVisualitation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Timestamp
import com.mobile.itfest.R
import com.mobile.itfest.data.Result
import com.mobile.itfest.data.model.FocusTime
import com.mobile.itfest.databinding.FragmentDayChartBinding
import com.mobile.itfest.databinding.FragmentWeekChartBinding
import com.mobile.itfest.ui.ViewModelFactory
import com.mobile.itfest.utils.util.ChartType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class WeekChartFragment : Fragment() {

    /* val dummyFocusTimeList = listOf(
       FocusTime(
           userId = "3O7q68anrLcI93HY8V0aoe11Lon1",
           focusTime = 120000,
           timeStamp = Timestamp(Date(2024 - 1900, 6, 28, 15, 12, 11)) // July 28, 2024 at 3:12:11 PM UTC+7
       ),
       FocusTime(
           userId = "3O7q68anrLcI93HY8V0aoe11Lon1",
           focusTime = 180000,
           timeStamp = Timestamp(Date(2024 - 1900, 6, 30, 10, 45, 0)) // July 28, 2024 at 10:45:00 AM UTC+7
       ),
       FocusTime(
           userId = "3O7q68anrLcI93HY8V0aoe11Lon1",
           focusTime = 1000000,
           timeStamp = Timestamp(Date(2024 - 1900, 7, 15, 8, 30, 0)) // July 28, 2024 at 8:30:00 AM UTC+7
       )
   )*/

    private lateinit var _binding: FragmentWeekChartBinding
    private val viewModel by lazy {
        ViewModelProvider(this, ViewModelFactory.getInstance(requireContext()))
            .get(DurationDataViewModel::class.java)
    }
    private var focusTime: List<FocusTime>? = null

    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWeekChartBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateWeekDisplay()

        viewModel.retrieveFocusTime().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    initiateWeekChart(result.data)
                    focusTime = result.data
                }
                else -> {
                    // Handle error
                    Log.d(TAG, "Error retrieving focus time data")
                }
            }
        }

        _binding.prevButton.setOnClickListener {
            calendar.add(Calendar.WEEK_OF_YEAR, -1)
            updateWeekDisplay()
            initiateWeekChart(focusTime)
        }

        _binding.nextButton.setOnClickListener {
            calendar.add(Calendar.WEEK_OF_YEAR, 1)
            updateWeekDisplay()
            initiateWeekChart(focusTime)
        }
    }

    private fun updateWeekDisplay() {
        val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        val startOfWeek = calendar.time
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val endOfWeek = calendar.time
        _binding.weekTextView.text = "${dateFormat.format(startOfWeek)} - ${dateFormat.format(endOfWeek)}"
    }

    private fun initiateWeekChart(focusTimeList: List<FocusTime>?) {

        /*val focusTimeList = dummyFocusTimeList*/

        if (focusTimeList == null) {
            Log.d(TAG, "Error: focusTimeList is null")
            _binding.androidChart1.setChart(
                ChartType.LINE,
                arrayOf(),
                arrayOf(),
                arrayOf(),
                "No data available (Error)"
            )
            return
        }

        // Get the current week from the calendar
        val currentWeekOfYear = calendar.get(Calendar.WEEK_OF_YEAR)
        val currentYear = calendar.get(Calendar.YEAR)

        // Filter the focusTimeList to include only elements with the same week of the year as the current date
        val filteredFocusTimeList = focusTimeList.filter { focusTime ->
            val timeStampDate = focusTime.timeStamp.toDate()
            val focusCalendar = Calendar.getInstance().apply { time = timeStampDate }
            val focusWeekOfYear = focusCalendar.get(Calendar.WEEK_OF_YEAR)
            val focusYear = focusCalendar.get(Calendar.YEAR)
            focusWeekOfYear == currentWeekOfYear && focusYear == currentYear
        }


        // Create x-axis labels for days of the week (Monday to Sunday)
        val xAxisLabels = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

        // Initialize y-axis values for focus time in minutes
        val yAxisValues = Array(7) { 0 }

        // Populate y-axis values based on the filteredFocusTimeList
        for (focusTime in filteredFocusTimeList) {
            // Convert focusTime from milliseconds to minutes
            val focusTimeInMinutes = (focusTime.focusTime / 60000).toInt()

            // Get the day of the week from the timestamp (1 = Sunday, 2 = Monday, ..., 7 = Saturday)
            val dayOfWeek = focusTime.timeStamp.toDate().day

            // Adjust dayOfWeek to match the xAxisLabels index (0 = Monday, ..., 6 = Sunday)
            val adjustedDayOfWeek = if (dayOfWeek == 0) 6 else dayOfWeek - 1

            // Add the focus time to the corresponding day
            yAxisValues[adjustedDayOfWeek] += focusTimeInMinutes
        }

        // Set the chart with the appropriate labels and values
        _binding.androidChart1.setChart(
            ChartType.BAR,
            xAxisLabels,
            yAxisValues,
            arrayOf("#1E96FC"), // Single color for all points
            "Focus Time (minutes)"
        )
    }

    companion object {
        const val TAG = "WeekChartFragment"
    }
}