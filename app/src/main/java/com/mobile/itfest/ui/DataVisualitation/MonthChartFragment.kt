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
import com.mobile.itfest.databinding.FragmentMonthChartBinding
import com.mobile.itfest.databinding.FragmentWeekChartBinding
import com.mobile.itfest.ui.DataVisualitation.WeekChartFragment.Companion
import com.mobile.itfest.ui.ViewModelFactory
import com.mobile.itfest.utils.util.ChartType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MonthChartFragment : Fragment() {

  /*   val dummyFocusTimeList = listOf(
       FocusTime(
           userId = "3O7q68anrLcI93HY8V0aoe11Lon1",
           focusTime = 10000,
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
           timeStamp = Timestamp(Date(2024 - 1900, 1, 15, 8, 30, 0)) // July 28, 2024 at 8:30:00 AM UTC+7
       )
   )*/

    private lateinit var _binding: FragmentMonthChartBinding
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
        _binding = FragmentMonthChartBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateMonthDisplay()

        viewModel.retrieveFocusTime().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    initiateMonthChart(result.data)
                    focusTime = result.data
                }
                else -> {
                    // Handle error
                    Log.d(WeekChartFragment.TAG, "Error retrieving focus time data")
                }
            }
        }

        _binding.prevButton.setOnClickListener {
            calendar.add(Calendar.YEAR, -1)
            updateMonthDisplay()
            initiateMonthChart(focusTime)
        }

        _binding.nextButton.setOnClickListener {
            calendar.add(Calendar.YEAR, 1)
            updateMonthDisplay()
            initiateMonthChart(focusTime)
        }
    }

    private fun updateMonthDisplay() {
        val dateFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        _binding.monthTextView.text = dateFormat.format(calendar.time)
    }

    private fun initiateMonthChart(focusTimeList: List<FocusTime>?) {
/*        val focusTimeList = dummyFocusTimeList*/

        if (focusTimeList == null) {
            Log.d(TAG, "Error: focusTimeList is null")
            _binding.androidChart1.setChart(
                ChartType.BAR,
                arrayOf(),
                arrayOf(),
                arrayOf(),
                "No data available (Error)"
            )
            return
        }

        // Get the current year from the calendar
        val currentYear = calendar.get(Calendar.YEAR)

        // Filter the focusTimeList to include only elements with the same year as the current date
        val filteredFocusTimeList = focusTimeList.filter { focusTime ->
            val timeStampDate = focusTime.timeStamp.toDate()
            val focusCalendar = Calendar.getInstance().apply { time = timeStampDate }
            val focusYear = focusCalendar.get(Calendar.YEAR)
            focusYear == currentYear
        }

        // Create x-axis labels for months (January to December)
        val xAxisLabels = arrayOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )

        // Initialize y-axis values for focus time in minutes
        val yAxisValues = Array(12) { 0 }

        // Populate y-axis values based on the filteredFocusTimeList
        for (focusTime in filteredFocusTimeList) {
            // Convert focusTime from milliseconds to minutes
            val focusTimeInMinutes = (focusTime.focusTime / 60000).toInt()

            // Get the month from the timestamp (0 = January, ..., 11 = December)
            val month = focusTime.timeStamp.toDate().month

            // Add the focus time to the corresponding month
            yAxisValues[month] += focusTimeInMinutes
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
        private const val TAG = "MonthChartFragment"
    }
}