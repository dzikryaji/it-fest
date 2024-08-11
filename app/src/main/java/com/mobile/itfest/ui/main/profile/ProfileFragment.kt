package com.mobile.itfest.ui.main.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mobile.itfest.R
import com.mobile.itfest.data.Result
import com.mobile.itfest.databinding.FragmentProfileBinding
import com.mobile.itfest.databinding.FragmentTimerBinding
import com.mobile.itfest.ui.ViewModelFactory
import com.mobile.itfest.ui.main.MainViewModel

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserve()
    }

    private fun setObserve() {
        viewModel.retrieveUser().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    val user = result.data
                    Log.d("ProfileFragment", "User retrieved: ${user.name}")
                    binding.profileNameTV.text = "${user.name}!"
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