package com.example.altimer.ui.homefragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.altimer.R
import com.example.altimer.SharedEventModel
import com.example.altimer.SharedTimesModel
import com.example.altimer.SharedUpdateModel
import com.example.altimer.Solve
import com.example.altimer.SolveManager
import com.example.altimer.adapters.TimesAdapter
import com.example.altimer.databinding.FragmentTimesBinding
import com.example.altimer.ui.gallery.GalleryViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class TimesFragment : Fragment(), SharedTimesModel.TimesUpdateListener, TimesAdapter.TimesButtonClickListener{

    private var _binding: FragmentTimesBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedViewModel : SharedTimesModel
    private lateinit var sharedUpdateModel: SharedUpdateModel

    private lateinit var layoutManager : LinearLayoutManager
    private lateinit var timesView : RecyclerView
    private lateinit var fab : FloatingActionButton
    private lateinit var currentEvent : String

    private var isFabVisible : Boolean = false
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

        currentEvent = SolveManager.getCubeType(requireContext())

        fab = root.findViewById(R.id.fab)
        fab.setOnClickListener {
            timesView.smoothScrollToPosition(0)
        }
        fab.hide()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timesView = binding.timesView
        layoutManager = LinearLayoutManager(requireContext())
        timesView.layoutManager = layoutManager

        timesView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
    override fun updateTimes() {
        _binding?.let { binding ->
            currentEvent = SolveManager.getCubeType(requireContext())

            timesView = binding.timesView
            layoutManager = LinearLayoutManager(requireContext())
            timesView.layoutManager = layoutManager
            val solveList = SolveManager.getSolveFromEvent(requireContext(), currentEvent)
            solveList.reverse()
            val adapter = TimesAdapter(requireContext(), solveList, this)
            timesView.adapter = adapter
        }
    }

    override fun onDNFButtonClicked(position: Int, event: String) {
        val firstPosition = layoutManager.findFirstVisibleItemPosition()
        SolveManager.makeDNF(requireContext(), position, event)
        updateTimes()
        timesView.scrollToPosition(firstPosition)
        sharedUpdateModel.statsUpdateListener?.updateStatistics()
    }
    override fun onOkayPressed(scramble: String) {
        val firstPosition = layoutManager.findFirstVisibleItemPosition()
        SolveManager.removePenalty(requireContext(), scramble)
        updateTimes()
        timesView.scrollToPosition(firstPosition)
        sharedUpdateModel.statsUpdateListener?.updateStatistics()
    }

    override fun onPlusTwoPressed(scramble: String) {
        val firstPosition = layoutManager.findFirstVisibleItemPosition()
        SolveManager.givePlusTwo(requireContext(), scramble)
        updateTimes()
        timesView.scrollToPosition(firstPosition)
        sharedUpdateModel.statsUpdateListener?.updateStatistics()
    }
    override fun onDelete(scramble: String) {
        val firstPosition = layoutManager.findFirstCompletelyVisibleItemPosition()

        SolveManager.deleteSolve(requireContext(), scramble)
        updateTimes()

        if (timesView.computeVerticalScrollRange() <= timesView.height) {
            val fadeOutAnimation = AlphaAnimation(1f, 0f)
            fadeOutAnimation.duration = 300L
            fab.startAnimation(fadeOutAnimation)
            fab.hide()
            isFabVisible = false
        }

        timesView.scrollToPosition(firstPosition)
        sharedUpdateModel.statsUpdateListener?.updateStatistics()
        val snackbar = Snackbar.make(requireView(), "Solve Deleted", Snackbar.LENGTH_SHORT)
        snackbar.view.setBackgroundColor(Color.parseColor("#FFFEE773"))
        snackbar.show()
    }

}