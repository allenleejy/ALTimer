package com.example.altimer

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
object OnboardingManager {
    private const val ONBOARD_KEY = "onboard_check"
    fun saveOnboardingState(context: Context, onboardingState: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MySolves", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(ONBOARD_KEY, onboardingState)
        editor.apply()
    }
    fun getOnboardingState(context: Context): String {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MySolves", Context.MODE_PRIVATE)
        return sharedPreferences.getString(ONBOARD_KEY, "") ?: ""
    }
}

