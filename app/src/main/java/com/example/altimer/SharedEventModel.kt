package com.example.altimer
import androidx.lifecycle.ViewModel

class SharedEventModel : ViewModel() {
    interface EventUpdateListener {
        fun updateEvent()
    }
    var eventUpdateListener: EventUpdateListener? = null
}
