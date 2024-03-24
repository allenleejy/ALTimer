package com.example.altimer
import androidx.lifecycle.ViewModel

class SharedTimesModel : ViewModel() {
    interface TimesUpdateListener {
        fun updateTimes()
    }
    var timesUpdateListener: TimesUpdateListener? = null
}
