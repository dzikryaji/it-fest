package com.mobile.itfest.ui.main.timer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mobile.itfest.data.Result
import com.mobile.itfest.databinding.FragmentTimerBinding
import com.mobile.itfest.ui.ViewModelFactory
import com.mobile.itfest.ui.focus.FocusActivity
import com.mobile.itfest.ui.main.MainViewModel


class TimerFragment : Fragment() {
    private lateinit var binding: FragmentTimerBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

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
    }
}