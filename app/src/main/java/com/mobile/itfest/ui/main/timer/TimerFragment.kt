package com.mobile.itfest.ui.main.timer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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
        binding.btnTimer.setOnClickListener {
            val intent = Intent(requireActivity(), FocusActivity::class.java)

            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity() as Activity,
                    Pair(binding.llTimer, "timer"),
                )

            requireActivity().startActivity(intent, optionsCompat.toBundle())
        }

        viewModel.retrieveUser().observe(viewLifecycleOwner){
            if (it is Result.Success) {
                val user = it.data
                binding.tvUser.text = "Hello, ${user.name}"
            }
        }
        return binding.root
    }
}