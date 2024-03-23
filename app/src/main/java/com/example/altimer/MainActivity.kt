package com.example.altimer

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ImageView
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

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbar : Toolbar
    private lateinit var navSettings : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = findViewById(R.id.custom_toolbar)
        navSettings = findViewById(R.id.nav_settings)

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