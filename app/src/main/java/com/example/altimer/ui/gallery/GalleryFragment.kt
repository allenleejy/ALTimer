package com.example.altimer.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.altimer.R
import com.example.altimer.databinding.FragmentGalleryBinding
import com.google.android.material.navigation.NavigationView

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private lateinit var toolbar : Toolbar
    private lateinit var navSettings : ImageView
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        //toolbar = requireView().findViewById(R.id.custom_toolbar)
        //navSettings = requireView().findViewById(R.id.nav_settings)

        //(activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        //val navController = requireView().findNavController(R.id.nav_host_fragment_content_main)
        /*val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView


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

         */
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}