package com.example.altimer.ui.slideshow

import android.app.PictureInPictureParams
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Rational
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.MediaController
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.altimer.AboutProgrammerActivity
import com.example.altimer.MainActivity
import com.example.altimer.R
import com.example.altimer.SolveManager
import com.example.altimer.databinding.FragmentSlideshowBinding
import com.google.android.material.appbar.AppBarLayout

class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null

    private val binding get() = _binding!!
    private lateinit var inspectionSwitch : Switch
    private lateinit var aboutprogrammer: Button
    private lateinit var clearSolves: Button

    private lateinit var videoView: VideoView
    private lateinit var mediaController: MediaController
    private lateinit var pipBtn: Button

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
        aboutprogrammer = binding.aboutprogrammer
        clearSolves = binding.clearsolves
        videoView = binding.videoView
        pipBtn = binding.pipBtn

        //(activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        val mainActivity = requireActivity() as MainActivity
        mainActivity.updatePuzzleName()

        if (SolveManager.getInspection(requireContext()) == "true") {
            inspectionSwitch.isChecked = true

        }
        else {
            inspectionSwitch.isChecked = false
        }
        inspectionSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                SolveManager.saveInspection(requireContext(), "true")
            }
            else {
                SolveManager.saveInspection(requireContext(), "false")
            }
        }
        aboutprogrammer.setOnClickListener {
            val intent = Intent(requireContext(), AboutProgrammerActivity::class.java)
            intent.putExtra("description", "Hi! I am Allen Lee, a CS student from NUSH and an avid cuber.")
            startActivity(intent)
        }
        clearSolves.setOnClickListener {
            SolveManager.clearSolves(requireContext())
            Toast.makeText(requireContext(), "All solves cleared", Toast.LENGTH_LONG).show()
        }

        val videoRes = R.raw.movie
        videoView.setVideoURI(Uri.parse("android.resource://com.example.altimer/$videoRes"))
        mediaController = MediaController(requireContext())
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        videoView.addOnLayoutChangeListener { _, left, top, right, bottom,
                                              oldLeft, oldTop, oldRight, oldBottom ->
            if (left != oldLeft || right != oldRight || top != oldTop || bottom != oldBottom) {
                val sourceRectHint = Rect()
                videoView.getGlobalVisibleRect(sourceRectHint)
                requireActivity().setPictureInPictureParams(PictureInPictureParams.Builder().setSourceRectHint(sourceRectHint).build())
            }
        }
        pipBtn.setOnClickListener {
            val aspectRatio = Rational(videoView.width, videoView.height)
            val pipBuilder = PictureInPictureParams.Builder()
            pipBuilder.setAspectRatio(aspectRatio)

            val sourceRect = Rect()
            videoView.getGlobalVisibleRect(sourceRect)
            pipBuilder.setSourceRectHint(sourceRect)
            val params = pipBuilder.build()
            videoView.visibility = View.VISIBLE
            binding.pipBtn.visibility = View.GONE
            binding.clearsolves.visibility = View.GONE
            binding.aboutprogrammer.visibility = View.GONE
            binding.inspectionSwitch.visibility = View.GONE
            binding.enableInspectionText.visibility = View.GONE
            binding.linearLayout.visibility = View.GONE
            val mainActivity = requireActivity() as MainActivity
            mainActivity.removeToolbar(true)

            val layoutParams = videoView.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin = 0
            layoutParams.marginStart = 0
            layoutParams.marginEnd = 0
            videoView.layoutParams = layoutParams
            requireActivity().enterPictureInPictureMode(params)

        }
        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode)
        if (!isInPictureInPictureMode) {
            videoView.visibility = View.VISIBLE
            binding.pipBtn.visibility = View.VISIBLE
            binding.clearsolves.visibility = View.VISIBLE
            binding.aboutprogrammer.visibility = View.VISIBLE
            binding.inspectionSwitch.visibility = View.VISIBLE
            binding.enableInspectionText.visibility = View.VISIBLE
            binding.linearLayout.visibility = View.VISIBLE

            val layoutParams = videoView.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin = 640
            layoutParams.marginStart = 16
            layoutParams.marginEnd = 16
            videoView.layoutParams = layoutParams

            val mainActivity = requireActivity() as MainActivity
            mainActivity.removeToolbar(false)

            videoView.setMediaController(mediaController)
        }
    }
}