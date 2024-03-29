package com.example.altimer.ui.gallery

import AlgorithmReader
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.GridLayout
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.altimer.Algorithm
import com.example.altimer.MainActivity
import com.example.altimer.R
import com.example.altimer.SharedEventModel
import com.example.altimer.SolveManager
import com.example.altimer.UpdateAlgModel
import com.example.altimer.adapters.AlgAdapter
import com.example.altimer.databinding.FragmentGalleryBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class GalleryFragment : Fragment(), UpdateAlgModel.AlgUpdateListener {

    private var _binding: FragmentGalleryBinding? = null
    private lateinit var toolbar : Toolbar
    private lateinit var navSettings : ImageView

    private lateinit var layoutManager: GridLayoutManager
    private lateinit var algView: RecyclerView

    private val binding get() = _binding!!

    private var isFabVisible : Boolean = false
    private lateinit var fab : FloatingActionButton

    private lateinit var updateAlgModel: UpdateAlgModel
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
        algView = binding.algView

        updateAlgModel = ViewModelProvider(requireActivity()).get(UpdateAlgModel::class.java)
        updateAlgModel.updateAlgListener = this

        val algtype = SolveManager.getAlgType(requireContext())
        showAlgset(algtype)

        val mainActivity = requireActivity() as MainActivity
        mainActivity.updatePuzzleName()

        fab = root.findViewById(R.id.fab)
        fab.setOnClickListener {
            algView.smoothScrollToPosition(0)
        }
        fab.hide()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        algView = binding.algView
        layoutManager = GridLayoutManager(requireContext(), 2)
        algView.layoutManager = layoutManager

        algView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val threshold = 20

                if (dy > threshold && !isFabVisible) {
                    val fadeInAnimation = AlphaAnimation(0f, 1f)
                    fadeInAnimation.duration = 300L
                    fab.startAnimation(fadeInAnimation)
                    fab.show()
                    isFabVisible = true
                } else if (dy < -threshold && isFabVisible) {
                    val fadeOutAnimation = AlphaAnimation(1f, 0f)
                    fadeOutAnimation.duration = 300L
                    fab.startAnimation(fadeOutAnimation)
                    fab.hide()
                    isFabVisible = false
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun showAlgset(algset: String) {
        if (algset == "PLL") {
            val algs = ArrayList<Algorithm>()
            val savedAlgorithms = AlgorithmReader.readPLL(requireContext())
            for (i in 1..21) {
                algs.add(Algorithm("PLL $i", savedAlgorithms.get(i-1), "pll_$i"))
            }
            val adapter = AlgAdapter(requireContext(), algs)
            algView.adapter = adapter
        }
        else {
            val algs = ArrayList<Algorithm>()
            val savedAlgorithms = AlgorithmReader.readOLL(requireContext())
            for (i in 1..57) {
                algs.add(Algorithm("OLL $i", savedAlgorithms.get(i-1), "oll_$i"))
            }
            val adapter = AlgAdapter(requireContext(), algs)
            algView.adapter = adapter
        }
    }

    override fun updateAlgs() {
        showAlgset(SolveManager.getAlgType(requireContext()))
    }
}