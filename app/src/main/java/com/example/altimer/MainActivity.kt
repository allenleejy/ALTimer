package com.example.altimer

import AlgorithmReader
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.altimer.adapters.OnboardingAdapter
import com.example.lab4.onboardingFragments.FirstFragment
import com.example.lab4.onboardingFragments.SecondFragment
import com.example.lab4.onboardingFragments.ThirdFragment
import me.relex.circleindicator.CircleIndicator3
import android.Manifest
import android.os.Build

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbar : Toolbar
    private lateinit var navSettings : ImageView
    private lateinit var puzzleSelection: RelativeLayout
    private lateinit var puzzleName : TextView
    private lateinit var sharedEventModel: SharedEventModel
    private lateinit var updateAlgModel: UpdateAlgModel
    private val fragmentList = ArrayList<Fragment>()
    private lateinit var viewPager: ViewPager2
    private lateinit var indicator: CircleIndicator3
    private lateinit var drawerLayout: DrawerLayout
    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 123

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = findViewById(R.id.custom_toolbar)
        puzzleName = findViewById(R.id.puzzleName)
        puzzleSelection = findViewById(R.id.puzzleSelection)
        drawerLayout = binding.drawerLayout

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
        navView.setupWithNavController(navController)
        //fadeInLayout()

        puzzleSelection.setOnClickListener {
            val currentDestinationId = navController.currentDestination?.displayName

            if (currentDestinationId == "com.example.altimer:id/nav_home") {
                showPuzzleSelectionDialog()
            }
            else {
                showAlgsetSelectionDialog()
            }
        }
        sharedEventModel = ViewModelProvider(this).get(SharedEventModel::class.java)
        updateAlgModel = ViewModelProvider(this).get(UpdateAlgModel::class.java)

        val currentEvent = SolveManager.getCubeType(this)
        handlePuzzleSelection(currentEvent)
        sharedEventModel.eventUpdateListener?.updateEvent()

        if (OnboardingManager.getOnboardingState(this) != "false") {
            initaliseOnboarding()
            initaliseSharedPreferences()
            OnboardingManager.saveOnboardingState(this, "false")

        }
        else {
            drawerLayout.visibility = View.VISIBLE
        }



        updatePuzzleName()
    }
    fun fadeToolbarAndTabLayout(fadeOut: Boolean) {
        val duration = 300L
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
    private fun showAlgsetSelectionDialog() {
        val builder = AlertDialog.Builder(this, R.style.DeleteDialogStyle)
        val dialogView = layoutInflater.inflate(R.layout.algset_selection_dialog, null)
        builder.setView(dialogView)
        val dialog = builder.create()

        val dialogWidth = resources.displayMetrics.widthPixels

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)
        layoutParams.width = dialogWidth
        layoutParams.height = dialogWidth // Set dialog height to match width
        dialog.window?.attributes = layoutParams

        val layoutoll = dialogView.findViewById<LinearLayout>(R.id.layout_oll)
        val layoutpll = dialogView.findViewById<LinearLayout>(R.id.layout_pll)

        layoutoll.setOnClickListener {
            puzzleName.text = "3x3 OLL"
            SolveManager.saveAlgType(this, "OLL")
            updateAlgModel.updateAlgListener?.updateAlgs()

            dialog.dismiss()
        }
        layoutpll.setOnClickListener {
            puzzleName.text = "3x3 PLL"
            SolveManager.saveAlgType(this, "PLL")
            updateAlgModel.updateAlgListener?.updateAlgs()
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
    @SuppressLint("RestrictedApi")
    fun updatePuzzleName() {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val currentDestinationId = navController.currentDestination?.displayName

        if (currentDestinationId == "com.example.altimer:id/nav_gallery") {
            toolbar.findViewById<ImageView>(R.id.spinnerIcon).visibility = View.VISIBLE
            toolbar.findViewById<RelativeLayout>(R.id.puzzleSelection).isClickable = true
            puzzleName.text = "3x3 " + SolveManager.getAlgType(this)
        }
        else if (currentDestinationId == "com.example.altimer:id/nav_home"){

            toolbar.findViewById<ImageView>(R.id.spinnerIcon).visibility = View.VISIBLE
            toolbar.findViewById<RelativeLayout>(R.id.puzzleSelection).isClickable = true
            var cubetype = SolveManager.getCubeType(this)
            if (cubetype == "2x2") {
                cubetype = "2x2 Cube"
            }
            else if (cubetype == "3x3") {
                cubetype = "3x3 Cube"
            }
            puzzleName.text = cubetype
        }
        else {
            puzzleName.text = "Settings"
            toolbar.findViewById<ImageView>(R.id.spinnerIcon).visibility = View.INVISIBLE
            toolbar.findViewById<RelativeLayout>(R.id.puzzleSelection).isClickable = false
        }

    }


    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }
     */
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun initaliseOnboarding() {
        fragmentList.add(FirstFragment())
        fragmentList.add(SecondFragment())
        fragmentList.add(ThirdFragment())
        viewPager = binding.onboarding.viewPager2
        indicator = binding.onboarding.indicator
        viewPager.adapter = OnboardingAdapter(this, fragmentList)
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        indicator.setViewPager(viewPager)
    }

    private fun initaliseSharedPreferences() {
        SolveManager.saveInspection(this, "true")
        SolveManager.clearSolves(this)
        SolveManager.saveAlgType(this, "OLL")
        SolveManager.saveCubeType(this, "3x3")
    }
    fun removeOnboarding() {
        val onboarding = binding.onboarding.onboardlayout
        val fadeout = AlphaAnimation(1f, 0f)
        fadeout.duration = 300L
        fadeout.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                onboarding.visibility = View.GONE

                fadeInLayout()
                requestNotificationPermission()
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        })
        onboarding.startAnimation(fadeout)

    }
    fun fadeInLayout() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        drawerLayout.visibility = View.VISIBLE

        val fadein = AlphaAnimation(0f, 1f)
        fadein.duration = 300L

        fadein.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        })
        drawerLayout.startAnimation(fadein)

    }
    fun removeToolbar(remove: Boolean) {
        val appBarMain = findViewById<View>(R.id.app_bar_main)
        val appBarLayout = appBarMain.findViewById<AppBarLayout>(R.id.boom)

        if (remove) {
            appBarLayout.visibility = View.GONE
            supportActionBar?.hide()
        }
        else {
            appBarLayout.visibility = View.VISIBLE
            supportActionBar?.show()
        }
    }
    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_REQUEST_CODE)
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "You will receive Notifications for PBs!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "You will not receive Notifications for PBs.", Toast.LENGTH_LONG).show()
            }
        }
    }
}