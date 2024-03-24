package com.example.altimer.ui.homefragments

import android.annotation.SuppressLint
import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.caverock.androidsvg.SVG
import com.example.altimer.MainActivity
import com.example.altimer.R
import com.example.altimer.SharedTimesModel
import com.example.altimer.Solve
import com.example.altimer.SolveManager
import com.example.altimer.databinding.FragmentTimerBinding
import com.example.altimer.ui.gallery.GalleryViewModel
import com.example.altimer.ui.home.HomeFragment
import org.worldcubeassociation.tnoodle.puzzle.ThreeByThreeCubePuzzle

class TimerFragment() : Fragment() {

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!

    private lateinit var scramble: TextView
    private lateinit var scrambleImage: ImageView
    private lateinit var newScramble: ImageButton
    private lateinit var timerText: TextView
    private lateinit var plusTwo: Button
    private lateinit var dnf: ImageButton

    private val cube = ThreeByThreeCubePuzzle()
    private lateinit var generatedScramble: String
    private lateinit var cubeImage: String

    private var startTime: Long = 0
    private var running: Boolean = false

    private var centerX = 0
    private var centerY = 0
    private var isExpanded = false

    private var penaltyShown = false

    private var currentSolve: Solve = Solve("", 0f, "", "")

    private lateinit var sharedViewModel : SharedTimesModel

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        scramble = binding.scramble
        scrambleImage = binding.scrambleimage
        newScramble = binding.newscramble
        timerText = binding.timer

        plusTwo = binding.plustwo
        dnf = binding.dnf

        centerX = resources.displayMetrics.widthPixels / 2
        centerY = resources.displayMetrics.heightPixels / 2

        SolveManager.clearSolves(requireContext())
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedTimesModel::class.java)

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        val textView = binding.timer

        val builder = SpannableStringBuilder()

        val part1 = "0"
        val span1 = SpannableString(part1)
        span1.setSpan(RelativeSizeSpan(2f), 0, part1.length, 0)
        builder.append(span1)

        val part2 = ".00"
        builder.append(part2)

        textView.text = builder

        generateScramble()

        newScramble.setOnClickListener {
            generateScramble()
        }
        root.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!isExpanded) {
                        if (!running) {
                            timerRunnable.resetTimer()
                            running = true
                        } else {
                            running = false
                            timerRunnable.stopTimer()
                            generateScramble()
                            fadeViews(false)
                            currentSolve.time = timerText.text.toString().toFloat()
                            Log.d("testing", "solve 1?")
                            SolveManager.addSolve(requireContext(), currentSolve)
                            Log.d("testing", SolveManager.getSolves(requireContext()).toString())
                            updateFirst()


                        }
                    }
                    true
                }

                MotionEvent.ACTION_UP -> {
                    if (isExpanded) {
                        zoomOut()
                    } else {
                        if (running) {
                            startTime = System.currentTimeMillis()
                            timerText.post(timerRunnable)
                            timerRunnable.startTimer()
                            fadeViews(true)
                            currentSolve.event = "3x3"
                            currentSolve.penalty = "0"
                            currentSolve.scramble = scramble.text.toString()
                        }
                    }
                    true
                }

                else -> false
            }
        }

        scrambleImage.setOnClickListener {
            if (isExpanded) {
                zoomOut()
            } else {
                zoomIn()
            }
        }
        plusTwo.setOnClickListener {
            timerText.text = formatTime(
                timerText.text.toString().dropLast(3).toLong() + 2,
                timerText.text.toString().takeLast(2).toLong()
            )
            currentSolve.time += 2f
            currentSolve.penalty = "+2"
            SolveManager.editLastSolve(requireContext(), currentSolve)
            updateFirst()
            plusTwo.visibility = View.INVISIBLE
        }
        dnf.setOnClickListener {
            val dnfString = SpannableString("DNF")
            val sizeSpan = RelativeSizeSpan(2f)
            dnfString.setSpan(sizeSpan, 0, dnfString.length, 0)
            timerText.text = dnfString
            dnf.visibility = View.INVISIBLE
            plusTwo.visibility = View.INVISIBLE
            penaltyShown = false
            currentSolve.penalty = "DNF"
            SolveManager.editLastSolve(requireContext(), currentSolve)
            updateFirst()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun generateScramble() {
        generatedScramble = cube.generateScramble()
        scramble.text = generatedScramble

        cubeImage = cube.drawScramble(
            generatedScramble,
            cube.parseColorScheme("304FFE" + "," + "FDD835" + "," + "02D040" + "," + "EF6C00" + "," + "EC0000" + "," + "FFFFFF")
        ).toString()
        scrambleImage.setImageDrawable(
            PictureDrawable(
                SVG.getFromString(cubeImage).renderToPicture()
            )
        )
    }

    private val timerRunnable = object : Runnable {
        private var lastElapsedTime: Long = 0

        override fun run() {
            if (running) {
                val elapsedTime = System.currentTimeMillis() - startTime
                val seconds = elapsedTime / 1000
                val milliseconds = (elapsedTime % 1000) / 10
                val timeText = formatTime(seconds, milliseconds)
                timerText.text = timeText
                timerText.postDelayed(this, 0.1.toLong())
            }
        }

        fun startTimer() {
            run()
        }

        fun stopTimer() {
            lastElapsedTime = System.currentTimeMillis() - startTime
            running = false
        }

        fun resetTimer() {
            timerText.text = formatTime(0, 0)
        }
    }

    private fun formatTime(seconds: Long, milliseconds: Long): SpannableStringBuilder {
        val builder = SpannableStringBuilder()

        val secondsPart = "$seconds"
        val secondsSpan = SpannableString(secondsPart)
        secondsSpan.setSpan(RelativeSizeSpan(2f), 0, secondsPart.length, 0)
        builder.append(secondsSpan)

        builder.append(".")

        val millisecondsPart = String.format("%02d", milliseconds)
        builder.append(millisecondsPart)

        return builder
    }

    private fun fadeViews(fadeOut: Boolean) {
        val duration = 300L // Set your desired duration here
        val alphaStart = if (fadeOut) 1.0f else 0.0f
        val alphaEnd = if (fadeOut) 0.0f else 1.0f

        val mainActivity = requireActivity() as MainActivity
        mainActivity.fadeToolbarAndTabLayout(fadeOut)

        (parentFragment as? HomeFragment)?.fadeTabLayout(fadeOut)
        if (!penaltyShown && fadeOut) {
            val viewsToFade = listOf(
                scramble,
                scrambleImage,
                newScramble,
                binding.fivelayout,
                binding.twelvelayout,
                binding.meanlayout,
                binding.countlayout
            )

            for (view in viewsToFade) {
                val alphaAnimation = AlphaAnimation(alphaStart, alphaEnd)
                alphaAnimation.duration = duration
                view.startAnimation(alphaAnimation)
                view.visibility = if (fadeOut) View.INVISIBLE else View.VISIBLE
            }
        } else if (!penaltyShown && !fadeOut) {
            val viewsToFade = listOf(
                scramble,
                scrambleImage,
                newScramble,
                binding.fivelayout,
                binding.twelvelayout,
                binding.meanlayout,
                binding.countlayout,
                binding.dnf,
                binding.plustwo
            )

            for (view in viewsToFade) {
                val alphaAnimation = AlphaAnimation(alphaStart, alphaEnd)
                alphaAnimation.duration = duration
                view.startAnimation(alphaAnimation)
                view.visibility = View.VISIBLE
            }
            penaltyShown = true
        } else {
            val viewsToFade = listOf(
                scramble,
                scrambleImage,
                newScramble,
                binding.fivelayout,
                binding.twelvelayout,
                binding.meanlayout,
                binding.countlayout,
                binding.plustwo,
                binding.dnf
            )

            for (view in viewsToFade) {
                val alphaAnimation = AlphaAnimation(alphaStart, alphaEnd)
                alphaAnimation.duration = duration
                view.startAnimation(alphaAnimation)
                view.visibility = if (fadeOut) View.INVISIBLE else View.VISIBLE
            }
            penaltyShown = true
        }
    }

    private fun zoomIn() {
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.zoom_in)
        animation.duration = 200
        animation.interpolator = LinearInterpolator()

        val translationX = centerX - (scrambleImage.x + scrambleImage.width / 2)
        //val translationY = centerY - (scrambleImage.y + scrambleImage.height / 2)

        //Log.d("test", "this is ${resources.displayMetrics.widthPixels}")

        scrambleImage.animate()
            .translationX(translationX)
            .translationY(-centerY.toFloat() + scrambleImage.height)
            .scaleX(2f)
            .scaleY(2f)
            .setDuration(animation.duration)
            .setInterpolator(animation.interpolator)
            .start()
        binding.plustwo.animate().alpha(0.0f).setInterpolator(animation.interpolator)
            .setDuration(animation.duration).start()
        binding.dnf.animate().alpha(0.0f).setInterpolator(animation.interpolator)
            .setDuration(animation.duration).start()
        isExpanded = true
    }

    private fun zoomOut() {
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.zoom_out)
        animation.duration = 200
        animation.interpolator = LinearInterpolator()

        val translationX = 0f
        val translationY = 0f

        scrambleImage.animate()
            .translationX(translationX)
            .translationY(translationY)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(animation.duration)
            .setInterpolator(animation.interpolator)
            .start()
        binding.plustwo.animate().alpha(1.0f).setInterpolator(animation.interpolator)
            .setDuration(animation.duration).start()
        binding.dnf.animate().alpha(1.0f).setInterpolator(animation.interpolator)
            .setDuration(animation.duration).start()

        isExpanded = false

    }
    private fun updateFirst() {
        sharedViewModel.timesUpdateListener?.updateTimes()
    }

}