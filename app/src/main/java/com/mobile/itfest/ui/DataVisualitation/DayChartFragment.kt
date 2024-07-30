package com.mobile.itfest.ui.DataVisualitation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Timestamp
import com.mobile.itfest.R
import com.mobile.itfest.data.Result
import com.mobile.itfest.data.model.FocusTime
import com.mobile.itfest.databinding.FragmentDayChartBinding
import com.mobile.itfest.ui.ViewModelFactory
import com.mobile.itfest.ui.main.MainViewModel
import com.mobile.itfest.utils.util.ChartType
import java.util.Date

class DayChartFragment : Fragment() {

    private lateinit var _binding: FragmentDayChartBinding
    private val viewModel by lazy {
        ViewModelProvider(this, ViewModelFactory.getInstance(requireContext()))
            .get(DurationDataViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDayChartBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.retrieveFocusTime().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    initiateDayChart(result.data)
                   /* initiateDayChart()*/
                }
                else -> {
                    // Handle error
                    _binding.androidChart1.setChart(
                        ChartType.LINE,
                        arrayOf(),
                        arrayOf(),
                        arrayOf(),
                        "No data available (Error)"
                    )
                    Log.d(TAG, "Error retrieving focus time data")
                }
            }
        }
    }

    private fun initiateDayChart(focusTimeList: List<FocusTime>) {
     /*   val focusTimeList = dummyFocusTimeList*/
        // Get the current date from the device
        val currentDate = Date()
        val currentYear = currentDate.year
        val currentMonth = currentDate.month
        val currentDay = currentDate.date

        // Filter the focusTimeList to include only elements with the same date as the current date
        val filteredFocusTimeList = focusTimeList.filter { focusTime ->
            val timeStampDate = focusTime.timeStamp.toDate()
            timeStampDate.month == currentMonth && timeStampDate.date == currentDay && timeStampDate.year == currentYear
        }

        // Create x-axis labels for hours from 0 AM to 11 PM
        val xAxisLabels = arrayOf(
            "0 AM", "1 AM", "2 AM", "3 AM", "4 AM", "5 AM", "6 AM", "7 AM", "8 AM", "9 AM", "10 AM", "11 AM",
            "12 PM", "1 PM", "2 PM", "3 PM", "4 PM", "5 PM", "6 PM", "7 PM", "8 PM", "9 PM", "10 PM", "11 PM"
        )

        // Initialize y-axis values for focus time in minutes
        val yAxisValues = Array(24) { 0 }

        // Populate y-axis values based on the filteredFocusTimeList
        for (focusTime in filteredFocusTimeList) {
            // Convert focusTime from milliseconds to minutes
            val focusTimeInMinutes = (focusTime.focusTime / 60000).toInt()

            // Get the hour from the timestamp
            val hour = focusTime.timeStamp.toDate().hours

            // Add the focus time to the corresponding hour
            yAxisValues[hour] += focusTimeInMinutes
        }


        // Set the chart with the appropriate labels and values
        _binding.androidChart1.setChart(
            ChartType.LINE,
            xAxisLabels,
            yAxisValues,
            arrayOf("#1E96FC"), // Single color for all points
            "Focus Time (minutes)"
        )
    }

    companion object {
        private const val TAG = "DayChartFragment"
    }
}