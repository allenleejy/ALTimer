package com.example.lab4.onboardingFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.altimer.MainActivity
import com.example.altimer.R


class ThirdFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_third, container, false)
    }

    override fun onStart() {
        super.onStart()
        val button = requireView().findViewById<Button>(R.id.button)
        button.setOnClickListener{
            hideOnboarding()
        }
    }
    private fun hideOnboarding() {
        val mainActivity = requireActivity() as MainActivity
        mainActivity.removeOnboarding()
    }
}