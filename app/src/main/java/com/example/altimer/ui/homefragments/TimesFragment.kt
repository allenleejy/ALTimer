package com.example.altimer.ui.homefragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.altimer.R
import com.example.altimer.SharedTimesModel
import com.example.altimer.Solve
import com.example.altimer.SolveManager
import com.example.altimer.adapters.TimesAdapter
import com.example.altimer.databinding.FragmentGalleryBinding
import com.example.altimer.databinding.FragmentTimerBinding
import com.example.altimer.databinding.FragmentTimesBinding
import com.example.altimer.ui.gallery.GalleryViewModel

class TimesFragment : Fragment(), SharedTimesModel.TimesUpdateListener {

    private var _binding: FragmentTimesBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedViewModel : SharedTimesModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentTimesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedTimesModel::class.java)
        sharedViewModel.timesUpdateListener = this

        updateTimes()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun updateTimes() {
        Log.d("testing", "updating")
        val timesView = binding.timesView
        val layoutManager = LinearLayoutManager(requireContext())
        timesView.layoutManager = layoutManager
        var solveList = ArrayList<Solve>()
        val savedSolves = SolveManager.getSolves(requireContext())
        savedSolves.forEach { solve ->
            solveList.add(solve)
        }
        val adapter = TimesAdapter(solveList)
        timesView.adapter = adapter
    }
}