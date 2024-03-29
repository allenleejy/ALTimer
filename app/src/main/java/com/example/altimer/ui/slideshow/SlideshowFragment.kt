package com.example.altimer.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.altimer.MainActivity
import com.example.altimer.SolveManager
import com.example.altimer.databinding.FragmentSlideshowBinding

class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null

    private val binding get() = _binding!!
    private lateinit var inspectionSwitch : Switch
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        inspectionSwitch = binding.inspectionSwitch

        //(activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        val mainActivity = requireActivity() as MainActivity
        mainActivity.updatePuzzleName()

        SolveManager.saveInspection(requireContext(), "true")
        if (SolveManager.getInspection(requireContext()) == "true") {
            inspectionSwitch.isChecked = true
        }
        else {
            inspectionSwitch.isChecked = false
        }
        inspectionSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                SolveManager.saveInspection(requireContext(), "true")
            }
            else {
                SolveManager.saveInspection(requireContext(), "false")
            }
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //(requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}