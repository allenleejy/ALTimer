package com.example.altimer
import androidx.lifecycle.ViewModel

class SharedUpdateModel : ViewModel() {
    interface StatsUpdateListener {
        fun updateStatistics()

    }
    var statsUpdateListener: StatsUpdateListener? = null
}