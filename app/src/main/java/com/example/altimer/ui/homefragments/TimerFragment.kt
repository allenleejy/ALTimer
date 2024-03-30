package com.example.altimer.ui.homefragments

import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.caverock.androidsvg.SVG
import com.example.altimer.MainActivity
import com.example.altimer.R
import com.example.altimer.SharedEventModel
import com.example.altimer.SharedTimesModel
import com.example.altimer.SharedUpdateModel
import com.example.altimer.Solve
import com.example.altimer.SolveManager
import com.example.altimer.databinding.FragmentTimerBinding
import com.example.altimer.ui.gallery.GalleryViewModel
import com.example.altimer.ui.home.HomeFragment
import org.worldcubeassociation.tnoodle.puzzle.ClockPuzzle
import org.worldcubeassociation.tnoodle.puzzle.PyraminxPuzzle
import org.worldcubeassociation.tnoodle.puzzle.ThreeByThreeCubePuzzle
import org.worldcubeassociation.tnoodle.puzzle.TwoByTwoCubePuzzle
import org.worldcubeassociation.tnoodle.scrambles.Puzzle
import java.util.Locale

class TimerFragment() : Fragment(), SharedUpdateModel.StatsUpdateListener, SharedEventModel.EventUpdateListener, TextToSpeech.OnInitListener{

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!

    private lateinit var scramble: TextView
    private lateinit var scrambleImage: ImageView
    private lateinit var newScramble: ImageButton
    private lateinit var timerText: TextView
    private lateinit var plusTwo: Button
    private lateinit var dnf: ImageButton

    private var cube : Puzzle = ThreeByThreeCubePuzzle()
    private lateinit var generatedScramble: String
    private lateinit var cubeImage: String

    private var startTime: Long = 0
    private var running: Boolean = false

    private var centerX = 0
    private var centerY = 0
    private var isExpanded = false

    private var penaltyShown = false

    private var currentSolve: Solve = Solve("", 0f, "", "")
    private var currentSolvePenalty : String = "0"

    private lateinit var sharedViewModel : SharedTimesModel
    private lateinit var sharedUpdateModel: SharedUpdateModel
    private lateinit var sharedEventModel: SharedEventModel

    private var countdownTimer: CountDownTimer? = null
    private lateinit var currentEvent : String

    private lateinit var textToSpeech: TextToSpeech
    private lateinit var shareButton : ImageButton
    private lateinit var notificationManager: NotificationManager

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
        shareButton = binding.share

        plusTwo = binding.plustwo
        dnf = binding.dnf

        centerX = resources.displayMetrics.widthPixels / 2
        centerY = resources.displayMetrics.heightPixels / 2

        textToSpeech = TextToSpeech(requireActivity(), this)

        initialiseStats()


        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedTimesModel::class.java)

        sharedUpdateModel = ViewModelProvider(requireActivity()).get(SharedUpdateModel::class.java)
        sharedUpdateModel.statsUpdateListener = this

        sharedEventModel = ViewModelProvider(requireActivity()).get(SharedEventModel::class.java)
        sharedEventModel.eventUpdateListener = this

        notificationManager = requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel("com.example.altimer.news", "ALTimer",
            "Cubing Timer in Kotlin")

        currentEvent = SolveManager.getCubeType(requireContext())
        updateEvent()

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

        val mainActivity = requireActivity() as MainActivity
        mainActivity.updatePuzzleName()

        newScramble.setOnClickListener {
            generateScramble()
        }

        var downpressed = ""
        var inspstarted = false
        root.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    var inspection = SolveManager.getInspection(requireContext())

                    if (!isExpanded) {
                        if (!running) {
                            if (inspection == "false") {
                                timerRunnable.resetTimer()
                                binding.plustwo.visibility = View.INVISIBLE
                                binding.dnf.visibility = View.INVISIBLE
                                shareButton.visibility = View.INVISIBLE
                                penaltyShown = false
                                running = true
                                downpressed = "down"
                            }
                            else if (inspstarted == false) {
                                inspstarted = true
                            }
                            else if (inspstarted == true) {
                                inspstarted = false
                                running = true
                            }
                        } else {
                            if (downpressed != "moved") {
                                running = false
                                timerRunnable.stopTimer()
                                generateScramble()
                                fadeViews(false)
                                currentSolve.time = timerText.text.toString().toFloat()
                                currentSolve.event = currentEvent
                                currentSolve.penalty = currentSolvePenalty
                                if (currentSolve.time != 0f) {
                                    SolveManager.addSolve(requireContext(), currentSolve)
                                    if (SolveManager.isSinglePB(requireContext(), currentEvent, currentSolve.time)) {
                                        sendNotification("New $currentEvent PB", "You got a time of ${String.format("%.2f",currentSolve.time)}!")
                                    }
                                }
                                updateStats(currentEvent)
                                updateFirst()
                            }
                        }
                    }
                    true
                }

                MotionEvent.ACTION_UP -> {
                    var inspection = SolveManager.getInspection(requireContext())
                    if (isExpanded) {
                        zoomOut()
                    } else {
                        if (running) {
                            if (inspection == "true" && !inspstarted) {
                                countdownTimer?.cancel()
                                startTime = System.currentTimeMillis()
                                timerText.post(timerRunnable)
                                timerRunnable.startTimer()
                                fadeoutInspection()
                                currentSolve.event = currentEvent
                                currentSolve.penalty = "0"
                                currentSolve.scramble = scramble.text.toString()
                                downpressed = "up"
                            }
                            else if (inspection == "false"){
                                startTime = System.currentTimeMillis()
                                timerText.post(timerRunnable)
                                timerRunnable.startTimer()
                                fadeViews(true)
                                currentSolve.event = currentEvent
                                currentSolve.penalty = "0"
                                currentSolve.scramble = scramble.text.toString()
                                downpressed = "up"
                            }
                        }
                        else {
                            if (inspection == "true" && inspstarted) {
                                startCountdownTimer()
                                fadeViews(true)
                                fadeinInspection()

                            }
                        }
                    }
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    downpressed = "moved"
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
            updateStats(currentEvent)
            plusTwo.visibility = View.INVISIBLE
            Toast.makeText(requireContext(), "+2 Applied", Toast.LENGTH_SHORT).show()
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
            updateStats(currentEvent)
            updateFirst()
            Toast.makeText(requireContext(), "DNF Applied", Toast.LENGTH_SHORT).show()

        }
        shareButton.setOnClickListener {
            val message = "$currentEvent\n\n${currentSolve.time}\n\n${currentSolve.scramble}"
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(Intent.createChooser(intent, "Send Message via:"))
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
        if (currentEvent == "Clock") {
            scramble.textSize = 18f
        }

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
    private fun formatInspection(seconds: String) : SpannableStringBuilder {
        val builder = SpannableStringBuilder()

        val secondsPart = seconds
        val secondsSpan = SpannableString(secondsPart)
        secondsSpan.setSpan(RelativeSizeSpan(2f), 0, secondsPart.length, 0)
        builder.append(secondsSpan)

        return builder
    }

    private fun fadeViews(fadeOut: Boolean) {
        val duration = 300L
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
                binding.plustwo,
                binding.share
            )
            for (view in viewsToFade) {
                if (view.visibility == View.INVISIBLE) {
                    val alphaAnimation = AlphaAnimation(alphaStart, alphaEnd)
                    alphaAnimation.duration = duration
                    view.startAnimation(alphaAnimation)
                    view.visibility = View.VISIBLE
                    penaltyShown = true
                }
            }

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
                binding.dnf,
                binding.share
            )

            for (view in viewsToFade) {
                if (view.visibility == View.VISIBLE && !fadeOut) {
                }
                else {
                    val alphaAnimation = AlphaAnimation(alphaStart, alphaEnd)
                    alphaAnimation.duration = duration
                    view.startAnimation(alphaAnimation)
                    view.visibility = if (fadeOut) View.INVISIBLE else View.VISIBLE
                }
            }
            penaltyShown = true
        }
    }

    private fun zoomIn() {
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.zoom_in)
        animation.duration = 300
        animation.interpolator = AccelerateDecelerateInterpolator()

        val translationX = centerX - (scrambleImage.x + scrambleImage.width / 2)
        val translationY = scrambleImage.height - centerY.toFloat()



        scrambleImage.animate()
            .translationX(translationX)
            .translationY(translationY)
            .scaleX(2f)
            .scaleY(2f)
            .setDuration(animation.duration)
            .setInterpolator(animation.interpolator)
            .start()

        binding.plustwo.animate().alpha(0.0f)
            .setInterpolator(animation.interpolator)
            .setDuration(animation.duration)
            .setListener(object : AnimatorListenerAdapter() {
            })
            .start()

        binding.dnf.animate().alpha(0.0f)
            .setInterpolator(animation.interpolator)
            .setDuration(animation.duration)
            .start()
        binding.share.animate().alpha(0.0f).setInterpolator(animation.interpolator).setDuration(animation.duration).start()

        isExpanded = true
    }

    private fun zoomOut() {
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.zoom_out)
        animation.duration = 300
        animation.interpolator = AccelerateDecelerateInterpolator()

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
        if (penaltyShown) {
            binding.plustwo.visibility = View.VISIBLE
        }
        binding.dnf.animate().alpha(1.0f).setInterpolator(animation.interpolator)
            .setDuration(animation.duration).start()
        if (penaltyShown) {
            binding.dnf.visibility = View.VISIBLE
        }
        binding.share.animate().alpha(1.0f).setInterpolator(animation.interpolator)
            .setDuration(animation.duration).start()
        if (penaltyShown) {
            binding.share.visibility = View.VISIBLE
        }
        isExpanded = false

    }
    private fun updateFirst() {
        sharedViewModel.timesUpdateListener?.updateTimes()
    }

    @SuppressLint("SetTextI18n")
    private fun updateStats(event : String) {
        val averageFive = binding.averagefive
        val averageTwelve = binding.averagetwelve
        val mean = binding.mean
        val count = binding.count
        var averageOfFive : Float = 0f
        var averageOfTwelve : Float = 0f
        var averageOfAll : Float = 0f
        val aofivelist = ArrayList<Float>()
        val aotwelvelist = ArrayList<Float>()

        val solves = SolveManager.getSolves(requireContext())
        val eventSolves = ArrayList<Solve>()

        for (solve in solves) {
            if (solve.event == event) {
                eventSolves.add(solve)
            }
        }
        if (eventSolves.size < 12) {
            averageTwelve.text = "AO12\n-"
            if (eventSolves.size < 5) {
                averageFive.text = "AO5\n-"
            }
            else {
                averageOfFive = 0f
                var shortest = 0f
                var longest = 0f
                for (i in eventSolves.size - 5 until eventSolves.size) {
                    if (eventSolves.get(i).penalty != "DNF") {
                        aofivelist.add(eventSolves.get(i).time)
                        if (shortest == 0f) {
                            shortest = eventSolves.get(i).time
                            longest = eventSolves.get(i).time
                        }
                        if (eventSolves.get(i).time < shortest) {
                            shortest = eventSolves.get(i).time
                        }
                        if (eventSolves.get(i).time > longest) {
                            longest = eventSolves.get(i).time
                        }
                    }
                }
                if (aofivelist.size == 4) {
                    aofivelist.remove(shortest)
                    for (time in aofivelist) {
                        averageOfFive += time
                    }
                    averageOfFive /= 3
                    averageFive.text = "AO5\n" + String.format("%.2f", averageOfFive)
                    if (SolveManager.isAOFivePB(requireContext(), currentEvent, averageOfFive)) {
                        val fivesolves = SolveManager.returnLastFiveSolves(requireContext(), currentEvent)
                        val pairList = mutableListOf<Pair<String, String>>()
                        var counter = 0
                        for (solve in fivesolves) {
                            counter++
                            pairList.add(Pair("Solve $counter ", String.format("%.2f", solve.time)))
                        }
                        sendBundledNotification("New $currentEvent AO5 PB!", pairList, averageOfFive)
                    }
                }
                else if (aofivelist.size < 4) {
                    averageFive.text = "AO5\n-"
                }
                else {
                    aofivelist.remove(shortest)
                    aofivelist.remove(longest)
                    for (time in aofivelist) {
                        averageOfFive += time
                    }
                    averageOfFive /= 3
                    averageFive.text = "AO5\n" + String.format("%.2f", averageOfFive)

                    if (SolveManager.isAOFivePB(requireContext(), currentEvent, averageOfFive)) {
                        val fivesolves = SolveManager.returnLastFiveSolves(requireContext(), currentEvent)
                        val pairList = mutableListOf<Pair<String, String>>()
                        var counter = 0
                        for (solve in fivesolves) {
                            counter++
                            pairList.add(Pair("Solve $counter ", String.format("%.2f", solve.time)))
                        }
                        sendBundledNotification("New $currentEvent AO5 PB!", pairList, averageOfFive)
                    }
                }

            }
        }
        else {
            averageOfFive = 0f
            var shortest = 0f
            var longest = 0f
            for (i in eventSolves.size - 5 until eventSolves.size) {
                if (eventSolves.get(i).penalty != "DNF") {
                    aofivelist.add(eventSolves.get(i).time)
                    if (shortest == 0f) {
                        shortest = eventSolves.get(i).time
                        longest = eventSolves.get(i).time
                    }
                    if (eventSolves.get(i).time < shortest) {
                        shortest = eventSolves.get(i).time
                    }
                    if (eventSolves.get(i).time > longest) {
                        longest = eventSolves.get(i).time
                    }
                }
            }
            if (aofivelist.size == 4) {
                aofivelist.remove(shortest)
                for (time in aofivelist) {
                    averageOfFive += time
                }
                averageOfFive /= 3
                averageFive.text = "AO5\n" + String.format("%.2f", averageOfFive)
            }
            else if (aofivelist.size < 4) {
                averageFive.text = "AO5\n-"
            }
            else {
                aofivelist.remove(shortest)
                aofivelist.remove(longest)
                for (time in aofivelist) {
                    averageOfFive += time
                }
                averageOfFive /= 3
                averageFive.text = "AO5\n" + String.format("%.2f", averageOfFive)
            }

            averageOfTwelve = 0f
            shortest = 0f
            longest = 0f
            for (i in eventSolves.size - 12 until eventSolves.size) {
                if (eventSolves.get(i).penalty != "DNF") {
                    aotwelvelist.add(eventSolves.get(i).time)
                    if (shortest == 0f) {
                        shortest = eventSolves.get(i).time
                        longest = eventSolves.get(i).time
                    }
                    if (eventSolves.get(i).time < shortest) {
                        shortest = eventSolves.get(i).time
                    }
                    if (eventSolves.get(i).time > longest) {
                        longest = eventSolves.get(i).time
                    }
                }
            }
            if (aotwelvelist.size == 11) {
                aotwelvelist.remove(shortest)

                for (time in aotwelvelist) {
                    averageOfTwelve += time
                }
                averageOfTwelve /= 10
                averageTwelve.text = "AO12\n" + String.format("%.2f", averageOfTwelve)
            }
            else if (aotwelvelist.size < 11) {
                averageTwelve.text = "AO12\n-"
            }
            else {
                aotwelvelist.remove(shortest)
                aotwelvelist.remove(longest)
                for (time in aotwelvelist) {
                    averageOfTwelve += time
                }
                averageOfTwelve /= 10
                averageTwelve.text = "AO12\n" + String.format("%.2f", averageOfTwelve)
            }
        }

        if (eventSolves.size != 0) {
            count.text = "COUNT\n${eventSolves.size}"
        }
        else {
            count.text = "COUNT\n-"
        }
        averageOfAll = 0f
        for (solve in eventSolves) {
            averageOfAll += solve.time
        }
        var meanValid = 0
        for (solve in eventSolves) {
            if (solve.penalty != "DNF") {
                meanValid++
            }
        }
        if (meanValid >= 1) {
            averageOfAll /= meanValid
            mean.text = "MEAN\n" + String.format("%.2f", averageOfAll)
        }
        else {
            mean.text = "MEAN\n-"
        }
    }
    fun initialiseStats() {
        val averageFive = binding.averagefive
        val averageTwelve = binding.averagetwelve
        val mean = binding.mean
        val count = binding.count
        averageFive.text = "AO5\n-"
        averageTwelve.text = "AO12\n-"
        mean.text = "MEAN\n-"
        count.text = "COUNT\n-"
    }

    override fun updateStatistics() {
        if (SolveManager.eventHasSolve(requireContext(), currentEvent)) {
            updateStats(currentEvent)
            timerText.text = formatTime(0, 0)
            plusTwo.visibility = View.GONE
            dnf.visibility = View.GONE
            shareButton.visibility = View.GONE
            penaltyShown = false
        }
        else {
            initialiseStats()
        }
    }

    override fun updateEvent() {

        val context = context ?: return
        val event = SolveManager.getCubeType(context)
        if (event == "3x3") {
            cube = ThreeByThreeCubePuzzle()
            currentEvent = "3x3"
        }
        else if (event == "2x2") {
            cube = TwoByTwoCubePuzzle()
            currentEvent = "2x2"
        }
        else if (event == "Clock") {
            cube = ClockPuzzle()
            currentEvent = "Clock"
        }
        else {
            cube = PyraminxPuzzle()
            currentEvent = "Pyraminx"
        }
        generateScramble()
        updateFirst()
        updateStats(currentEvent)
    }
    private fun startCountdownTimer() {
        countdownTimer = object : CountDownTimer(17000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val remainingSeconds = millisUntilFinished / 1000
                if (remainingSeconds <= 1) {
                    timerText.text = formatInspection("+2")
                    currentSolvePenalty = "+2"
                }
                else {
                    if (remainingSeconds == 8.toLong()) {
                        speak("8 seconds")
                    }
                    if (remainingSeconds == 4.toLong()) {
                        speak("12 seconds")
                    }
                    timerText.text = formatInspection((remainingSeconds - 1).toString())
                    currentSolvePenalty = "0"
                }
            }

            override fun onFinish() {
                timerText.text = formatInspection("DNF")
                speak("DNF")
                currentSolvePenalty = "DNF"
                fadeoutInspection()
            }

        }
        countdownTimer?.start()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech!!.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language not supported!")
            }
        }
    }
    fun speak(text: String) {
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }
    fun fadeinInspection() {
        val fadein = AlphaAnimation(0f, 1f)
        fadein.duration = 200L
        binding.inspection.startAnimation(fadein)
        binding.inspection.visibility = View.VISIBLE
    }
    fun fadeoutInspection() {
        val fadeout = AlphaAnimation(1f, 0f)
        fadeout.duration = 200L
        binding.inspection.startAnimation(fadeout)
        binding.inspection.visibility = View.INVISIBLE
    }
    private fun createNotificationChannel(id: String, name: String, description: String){
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(id, name, importance)
        channel.description = description
        channel.enableLights(true)
        channel.lightColor = Color.RED
        channel.enableVibration(true)
        channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        notificationManager.createNotificationChannel(channel)
    }
    @SuppressLint("MissingPermission")
    private fun sendNotification(title: String, content: String) {
        val channelId = "com.example.altimer.news"

        val builder = NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(requireContext())) {
            notify("101" ,1, builder.build())
        }
    }
    @SuppressLint("MissingPermission")
    fun sendBundledNotification(bundledNotificationTitle: String, notifications: List<Pair<String, String>>, average: Float) {
        val channelId = "com.example.altimer.news"
        val notificationList = mutableListOf<Pair<String, String>>()

        notifications.forEach { (title, content) ->
            notificationList.add(title to content)
        }
        notificationList.add(bundledNotificationTitle to "Average of 5 time: ${String.format("%.2f", average)}")

        val summaryBuilder = NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(bundledNotificationTitle)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setGroup("com.example.altimer.news")
            .setGroupSummary(true)

        notificationList.forEachIndexed { index, notification ->
            val (title, content) = notification
            val notificationBuilder = NotificationCompat.Builder(requireContext(), channelId)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setContentText(content)
                .setGroup("com.example.altimer.news")
                .setGroupSummary(false)

            val notificationId = "bundle_$index"
            NotificationManagerCompat.from(requireContext()).notify(notificationId.hashCode(), notificationBuilder.build())
        }

        NotificationManagerCompat.from(requireContext()).notify(
            bundledNotificationTitle.hashCode(),
            summaryBuilder.build()
        )
    }


}