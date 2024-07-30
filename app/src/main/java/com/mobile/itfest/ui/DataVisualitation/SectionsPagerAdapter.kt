package com.mobile.itfest.ui.DataVisualitation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class SectionsPagerAdapter (activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DayChartFragment()
            1 -> WeekChartFragment()
            else -> MonthChartFragment()
        }
/*        val fragment = DayChartFragment()
        return fragment*/
    }
}