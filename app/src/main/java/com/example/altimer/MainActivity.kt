package com.example.altimer

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.example.altimer.databinding.ActivityMainBinding
import com.google.android.material.appbar.AppBarLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbar : Toolbar
    private lateinit var navSettings : ImageView
    private lateinit var puzzleSelection: RelativeLayout
    private lateinit var puzzleName : TextView
    private lateinit var sharedEventModel: SharedEventModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = findViewById(R.id.custom_toolbar)
        navSettings = findViewById(R.id.nav_settings)
        puzzleName = findViewById(R.id.puzzleName)
        puzzleSelection = findViewById(R.id.puzzleSelection)

        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        navView.setupWithNavController(navController)

        navSettings.setOnClickListener {
            navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
        }

        puzzleSelection.setOnClickListener {
            showPuzzleSelectionDialog()
        }
        sharedEventModel = ViewModelProvider(this).get(SharedEventModel::class.java)

    }
    fun fadeToolbarAndTabLayout(fadeOut: Boolean) {
        val duration = 300L // Set your desired duration here
        val alphaEnd = if (fadeOut) 0.0f else 1.0f

        val appBarMain = findViewById<View>(R.id.app_bar_main)
        val appBarLayout = appBarMain.findViewById<AppBarLayout>(R.id.boom)

        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                appBarLayout.animate().alpha(alphaEnd).setDuration(duration).start()
            }
        }
    }

    private fun showPuzzleSelectionDialog() {
        val builder = AlertDialog.Builder(this, R.style.DeleteDialogStyle)
        val dialogView = layoutInflater.inflate(R.layout.eventselectiondialog, null)
        builder.setView(dialogView)
        val dialog = builder.create()

        //dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val dialogWidth = resources.displayMetrics.widthPixels

        // Set dialog window attributes
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)
        layoutParams.width = dialogWidth
        layoutParams.height = dialogWidth // Set dialog height to match width
        dialog.window?.attributes = layoutParams

        val layout3x3 = dialogView.findViewById<LinearLayout>(R.id.layout_3x3)
        val layout2x2 = dialogView.findViewById<LinearLayout>(R.id.layout_2x2)
        val layoutclock = dialogView.findViewById<LinearLayout>(R.id.layout_clock)
        val layoutpyra = dialogView.findViewById<LinearLayout>(R.id.layout_pyra)

        layout3x3.setOnClickListener {
            handlePuzzleSelection("3x3")
            dialog.dismiss()
        }
        layout2x2.setOnClickListener {
            handlePuzzleSelection("2x2")
            dialog.dismiss()
        }
        layoutclock.setOnClickListener {
            handlePuzzleSelection("Clock")
            dialog.dismiss()
        }
        layoutpyra.setOnClickListener {
            handlePuzzleSelection("Pyraminx")
            dialog.dismiss()
        }

        dialog.show()
    }
    private fun handlePuzzleSelection(selectedPuzzle: String) {
        if (selectedPuzzle == "3x3") {
            puzzleName.text = "3x3 Cube"
        }
        else if (selectedPuzzle == "2x2") {
            puzzleName.text = "2x2 Cube"
        }
        else if (selectedPuzzle == "Clock") {
            puzzleName.text = "Clock"
        }
        else {
            puzzleName.text = "Pyraminx"
        }

        SolveManager.saveCubeType(this, selectedPuzzle)
        sharedEventModel.eventUpdateListener?.updateEvent()
    }


    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
     */
}