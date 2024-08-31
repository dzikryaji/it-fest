package com.mobile.itfest.ui.main.leaderboard.user_detail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.mobile.itfest.data.Result
import com.mobile.itfest.data.model.FocusTime
import com.mobile.itfest.data.model.User
import com.mobile.itfest.databinding.FragmentUserLeaderboardDetailBinding
import com.mobile.itfest.ui.DataVisualitation.WeekChartFragment.Companion.TAG
import com.mobile.itfest.ui.ViewModelFactory
import com.mobile.itfest.utils.util.ChartType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

class UserLeaderboardDetailFragment : Fragment() {

    private lateinit var _binding : FragmentUserLeaderboardDetailBinding
    private val viewModel by lazy {
        ViewModelProvider(this, ViewModelFactory.getInstance(requireContext()))
            .get(UserLeaderboardDetailViewModel::class.java)
    }

    private var focusTime: List<FocusTime>? = null

    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserLeaderboardDetailBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = arguments?.getParcelable<User>(USER)
        val position = arguments?.getInt(POSITION)

        updateWeekDisplay()

        if (user != null) {
            viewModel.findOtherUserFocusTime(user.id).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Success -> {
                        initiateWeekChart(result.data)
                        val averageHoursInThisWeek = getAverageHoursInThisWeek(result.data)
                        val oneDayPeakHours = getOneDayPeakHours(result.data)
                        val totalHoursInThisWeek = getTotalHoursInThisWeek(result.data)
                        _binding.averageHoursTv.text = String.format(Locale.getDefault(), "%.2f", averageHoursInThisWeek)
                        _binding.peakHoursTv.text = String.format(Locale.getDefault(), "%.2f", oneDayPeakHours)
                        _binding.totalHoursTv.text = String.format(Locale.getDefault(), "%.2f", totalHoursInThisWeek)
                        focusTime = result.data
                    }
                    else -> {
                        // Handle error
                        Log.d(TAG, "Error retrieving focus time data")
                    }
                }
            }
        }

        user?.let {
            _binding.tvNameFirst.text = it.name
            val totalTime = (it.totalFocusTime / 1000)
            val points = pointRandomizerBasedOnTotalTime(totalTime)
            _binding.tvPointsFirst.text = totalTime.toString()
            _binding.questPointsTv.text = points[0].toString()
            _binding.dailyPointsTv.text = points[1].toString()
            _binding.notesCreatedTv.text = points[2].toString()
        }
        position?.let {
            _binding.tvRank.text = (it + 4).toString()
        }
    }

    private fun getTotalHoursInThisWeek(focusTimeList: List<FocusTime>?): Double {
        if (focusTimeList == null) {
            Log.d(TAG, "Error: focusTimeList is null")
            return 0.0
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

        // Calculate the total focus time in milliseconds
        val totalFocusTime = filteredFocusTimeList.sumOf { it.focusTime }

        // Calculate the total focus time in hours
        return totalFocusTime.toDouble() / 3600000
    }

    private fun getOneDayPeakHours(focusTimeList: List<FocusTime>?): Double {
        if (focusTimeList == null) {
            Log.d(TAG, "Error: focusTimeList is null")
            return 0.0
        }

        // Get the current day of the week
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // Filter the focusTimeList to include only elements with the same day of the week as the current date
        val filteredFocusTimeList = focusTimeList.filter { focusTime ->
            val timeStampDate = focusTime.timeStamp.toDate()
            val focusCalendar = Calendar.getInstance().apply { time = timeStampDate }
            val focusDayOfWeek = focusCalendar.get(Calendar.DAY_OF_WEEK)
            focusDayOfWeek == currentDayOfWeek
        }

        // Calculate the total focus time in milliseconds
        val totalFocusTime = filteredFocusTimeList.sumOf { it.focusTime }

        // Calculate the peak focus time in hours
        return totalFocusTime.toDouble() / 3600000
    }

    private fun getAverageHoursInThisWeek(focusTimeList: List<FocusTime>?): Double {
        if (focusTimeList == null) {
            Log.d(TAG, "Error: focusTimeList is null")
            return 0.0
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

        // Calculate the total focus time in milliseconds
        val totalFocusTime = filteredFocusTimeList.sumOf { it.focusTime }

        // Calculate the average focus time in hours
        return totalFocusTime.toDouble() / (filteredFocusTimeList.size * 3600000)
    }

    private fun updateWeekDisplay() {
        val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        val startOfWeek = calendar.time
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val endOfWeek = calendar.time
    }

    private fun initiateWeekChart(focusTimeList: List<FocusTime>?) {

        /*val focusTimeList = dummyFocusTimeList*/

        if (focusTimeList == null) {
            Log.d(TAG, "Error: focusTimeList is null")
            _binding.weekChart.setChart(
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
        _binding.weekChart.setChart(
            ChartType.BAR,
            xAxisLabels,
            yAxisValues,
            arrayOf("#68B9FD"), // Single color for all points
            "Focus Time (minutes)"
        )
    }

    private fun pointRandomizerBasedOnTotalTime(totalTime: Long) : ArrayList<Long> {
        val points = ArrayList<Long>()
        // Devide the total time by 3 and make each of the 3 points a random number
        // but the sum of the 3 points should be equal to the total time
        val random = Random

        // Generate two random points
        val point1 = random.nextLong(totalTime)
        val point2 = random.nextLong(totalTime - point1)

        // Calculate the third point to ensure the sum equals totalTime
        val point3 = totalTime - point1 - point2

        // Add the points to the ArrayList
        points.add(point1)
        points.add(point2)
        points.add(point3)

        // Shuffle the points to randomize their order
        points.shuffle()

        return points
    }

    companion object {
        private const val USER= "user"
        private const val POSITION = "position"

        private val TAG = UserLeaderboardDetailFragment::class.java.simpleName

        fun newInstance(user: User, position: Int): UserLeaderboardDetailFragment {
            val fragment = UserLeaderboardDetailFragment()
            val args = Bundle()
            args.putParcelable(USER, user)
            fragment.arguments = args
            return fragment
        }
    }
}