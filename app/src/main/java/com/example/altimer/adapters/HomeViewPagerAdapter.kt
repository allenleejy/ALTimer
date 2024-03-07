package com.example.altimer.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.altimer.ui.home.HomeFragment
import com.example.altimer.ui.homefragments.TimerFragment
import com.example.altimer.ui.homefragments.TimesFragment

class HomeViewPagerAdapter(fragmentActivity: HomeFragment) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2 // Number of fragments

    override fun createFragment(position: Int): Fragment {
        // Return your fragments based on the position
        return when (position) {
            0 -> TimerFragment()
            1 -> TimesFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}