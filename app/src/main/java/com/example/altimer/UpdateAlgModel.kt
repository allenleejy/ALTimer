package com.example.altimer

import androidx.lifecycle.ViewModel

class UpdateAlgModel : ViewModel() {
    interface AlgUpdateListener {
        fun updateAlgs()
    }
    var updateAlgListener: AlgUpdateListener? = null
}

