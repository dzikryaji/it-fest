package com.mobile.itfest.ui.main.leaderboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.itfest.adapter.LeaderboardAdapter
import com.mobile.itfest.databinding.FragmentLeaderboardBinding
import com.mobile.itfest.ui.ViewModelFactory
import com.mobile.itfest.ui.main.MainViewModel


class LeaderboardFragment : Fragment() {

    private lateinit var binding: FragmentLeaderboardBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLeaderboardBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserve()
    }

    private fun setObserve() {
        viewModel.leaderboard.observe(viewLifecycleOwner) { data ->
            Log.d("LeaderboardFragment", "User retrieved: $data")
            val first = data[0]
            val second = data[1]
            val third = data[2]
            val userList = ArrayList(data.subList(3, data.size))

            binding.apply {
                tvNameFirst.text = first.name
                tvNameSecond.text = second.name
                tvNameThird.text = third.name

                tvPointsFirst.text = "${first.totalFocusTime / 1000} pts"
                tvPointsSecond.text = "${second.totalFocusTime / 1000} pts"
                tvPointsThird.text = "${third.totalFocusTime / 1000} pts"

                val adapter = LeaderboardAdapter()
                rvLeaderboard.layoutManager = LinearLayoutManager(requireActivity())
                rvLeaderboard.adapter = adapter
                adapter.submitList(userList)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchTop10UsersByFocusTime()
    }
}