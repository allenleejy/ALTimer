package com.example.altimer

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AboutProgrammerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aboutprogrammer)

        val description = intent.getStringExtra("description")
        val descriptionTextView = findViewById<TextView>(R.id.descriptionTextView)

        descriptionTextView.text = description
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }
}