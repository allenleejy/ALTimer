package com.example.altimer.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.altimer.MainActivity
import com.example.altimer.R
import com.example.altimer.adapters.HomeViewPagerAdapter
import com.example.altimer.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment(){

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewPager = binding.viewPager
        tabLayout = binding.tabLayout

        //(activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        val adapter = HomeViewPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.setIcon(R.drawable.timer)
                1 -> tab.setIcon(R.drawable.times)
            }
        }.attach()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun fadeTabLayout(fadeOut: Boolean) {
        val duration = 300L // Set your desired duration here
        val alphaEnd = if (fadeOut) 0.0f else 1.0f

        tabLayout.animate()
            .alpha(alphaEnd)
            .setDuration(duration)
            .start()
    }

}