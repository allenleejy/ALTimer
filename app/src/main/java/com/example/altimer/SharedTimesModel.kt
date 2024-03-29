package com.example.altimer
import androidx.lifecycle.ViewModel

class SharedTimesModel : ViewModel() {
    interface TimesUpdateListener {
        fun updateTimes()
    }
    interface EventUpdateListener {
        fun updateEvent()
    }
    var timesUpdateListener: TimesUpdateListener? = null
    var eventUpdateListener: EventUpdateListener? = null
}
