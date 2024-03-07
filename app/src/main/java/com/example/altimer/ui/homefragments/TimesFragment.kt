package com.example.altimer.ui.homefragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.altimer.databinding.FragmentGalleryBinding
import com.example.altimer.databinding.FragmentTimerBinding
import com.example.altimer.databinding.FragmentTimesBinding
import com.example.altimer.ui.gallery.GalleryViewModel

class TimesFragment : Fragment() {

    private var _binding: FragmentTimesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentTimesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textTimes

        galleryViewModel.text.observe(viewLifecycleOwner) {
            textView.text = "Times Fragment"
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}