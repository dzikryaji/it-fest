package com.mobile.itfest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.itfest.R
import com.mobile.itfest.data.model.User
import com.mobile.itfest.databinding.ItemLeaderboardBinding
import com.mobile.itfest.ui.main.leaderboard.user_detail.UserLeaderboardDetailFragment

class LeaderboardAdapter (private val fragment: Fragment): ListAdapter<User, LeaderboardAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewHolder(
        ItemLeaderboardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it, position)
        }
    }

    inner class MyViewHolder(private val binding: ItemLeaderboardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User, position: Int) {
            binding.apply {
                tvRank.text = (position + 4).toString()
                tvName.text = user.name
                tvPoints.text = (user.totalFocusTime / 1000).toString()

                userLayout.setOnClickListener{
                    val userLeaderboardDetailFragment = UserLeaderboardDetailFragment.newInstance(user, position)
                    fragment.parentFragmentManager.beginTransaction()
                        .replace(R.id.container, userLeaderboardDetailFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        }
    }



    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(
                oldItem: User,
                newItem: User
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: User,
                newItem: User
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}