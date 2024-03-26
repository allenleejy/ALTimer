package com.example.altimer.ui.homefragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.altimer.SharedTimesModel
import com.example.altimer.SharedUpdateModel
import com.example.altimer.Solve
import com.example.altimer.SolveManager
import com.example.altimer.adapters.TimesAdapter
import com.example.altimer.databinding.FragmentTimesBinding
import com.example.altimer.ui.gallery.GalleryViewModel

class TimesFragment : Fragment(), SharedTimesModel.TimesUpdateListener, TimesAdapter.TimesButtonClickListener {

    private var _binding: FragmentTimesBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedViewModel : SharedTimesModel
    private lateinit var sharedUpdateModel: SharedUpdateModel

    private lateinit var layoutManager : LinearLayoutManager
    private lateinit var timesView : RecyclerView

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

        sharedUpdateModel = ViewModelProvider(requireActivity()).get(SharedUpdateModel::class.java)
        updateTimes()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun updateTimes() {
        Log.d("testing", "updating")
        timesView = binding.timesView
        layoutManager = LinearLayoutManager(requireContext())
        timesView.layoutManager = layoutManager
        var solveList = ArrayList<Solve>()
        val savedSolves = SolveManager.getSolves(requireContext())
        savedSolves.forEach { solve ->
            solveList.add(solve)
        }
        solveList.reverse()
        val adapter = TimesAdapter(requireContext(), solveList, this)
        timesView.adapter = adapter
    }


    override fun onDNFButtonClicked(position: Int) {
        Log.d("testing", "DNF button clicked at position $position")
        SolveManager.makeDNF(requireContext(), position)
        updateTimes()
        sharedUpdateModel.statsUpdateListener?.updateStatistics()
    }
    override fun onDelete(position: Int) {
        val firstPosition = layoutManager.findFirstCompletelyVisibleItemPosition()

        SolveManager.deleteSolve(requireContext(), position)
        updateTimes()

        timesView.scrollToPosition(firstPosition)
        sharedUpdateModel.statsUpdateListener?.updateStatistics()
    }
}