package vce.nhs.pomodolock.fragments.CompassFragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager

import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import vce.nhs.pomodolock.databinding.FragmentCompassBinding

import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vce.nhs.pomodolock.R
import vce.nhs.pomodolock.utils.Compass
import java.text.SimpleDateFormat
import java.util.*


import java.io.StringReader
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class CompassFragment : Fragment(), Compass.TimetableLoadListener {
    private val hideHandler = Handler(Looper.myLooper()!!)

    @Suppress("InlinedApi")
    private val hidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        val flags =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        activity?.window?.decorView?.systemUiVisibility = flags
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
    }
    private val showPart2Runnable = Runnable {
        // Delayed display of UI elements
        fullscreenContentControls?.visibility = View.VISIBLE
    }
    private var visible: Boolean = false
    private val hideRunnable = Runnable { hide() }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val delayHideTouchListener = View.OnTouchListener { _, _ ->
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }

    private var dummyButton: Button? = null
    private var fullscreenContent: View? = null
    private var fullscreenContentControls: View? = null

    private var _binding: FragmentCompassBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCompassBinding.inflate(inflater, container, false)
        return binding.root

    }

    private var currentDate: LocalDate = LocalDate.now()

    private lateinit var recyclerView: RecyclerView
    private lateinit var compassAdapter: CompassAdapter

    private val timezone = 10

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        visible = true

        // Set up the user interaction to manually show or hide the system UI.
        fullscreenContent?.setOnClickListener { toggle() }

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        dummyButton?.setOnTouchListener(delayHideTouchListener)
        // Set up RecyclerView
        val recyclerView = binding.compassRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize the adapter with an empty list
        compassAdapter = CompassAdapter(emptyList())
        recyclerView.adapter = compassAdapter

        // Set the initial date to the current date
        currentDate = LocalDate.now()

        // Load timetable for the initial date
        loadTimetableForCurrentDate()

        // Replace with your actual arrow button IDs
        val previousArrow = view.findViewById<View>(R.id.previousArrow)
        val nextArrow = view.findViewById<View>(R.id.nextArrow)

        // Set click listeners for the arrow buttons
        previousArrow.setOnClickListener { loadTimetableForPreviousDay() }
        nextArrow.setOnClickListener { loadTimetableForNextDay() }

        // Get the TextView for displaying the date
        val dateTextView = view.findViewById<TextView>(R.id.dateTextView)
        updateDateTextView(dateTextView)
    }

    override fun onTimetableLoaded(timetableData: String) {
        val compassModels = parseTimetableData(timetableData, timezone)

        Log.d("CompassFragment", "Models $compassModels")

        if (compassModels.isEmpty()) {
            Log.d("CompassFragment", "No timetable data available for ${currentDate.format(DateTimeFormatter.ISO_DATE)}.")

            // Show the "noClassesTextView" and hide the RecyclerView when there are no classes
            activity?.runOnUiThread {
                view?.findViewById<TextView>(R.id.noClassesTextView)?.visibility = View.VISIBLE
                view?.findViewById<RecyclerView>(R.id.compassRecyclerView)?.visibility = View.GONE
            }
        } else {
            Log.d("CompassFragment", "Number of timetable items: ${compassModels.size}")

            // Hide the "noClassesTextView" and show the RecyclerView when there are classes
            activity?.runOnUiThread {
                view?.findViewById<TextView>(R.id.noClassesTextView)?.visibility = View.GONE
                view?.findViewById<RecyclerView>(R.id.compassRecyclerView)?.visibility = View.VISIBLE

                // Update the RecyclerView with the new data and the current date
                compassAdapter.updateTimetable(compassModels)
            }
        }
    }


    private fun loadTimetableForCurrentDate() {
        val timetableUrl = "https://nhs-vic.compass.education/download/sharedCalendar.aspx?uid=27127&key=5aa5f43e-e8ce-40ba-8825-cf527ab68555&c.ics"
        Compass.loadTimetableForDate(timetableUrl, currentDate, timezone, this)
    }

    private fun loadTimetableForPreviousDay() {
        currentDate = currentDate.minusDays(1)
        loadTimetableForCurrentDate()
        updateDateTextView(requireView().findViewById(R.id.dateTextView))
    }

    private fun loadTimetableForNextDay() {
        currentDate = currentDate.plusDays(1)
        loadTimetableForCurrentDate()
        updateDateTextView(requireView().findViewById(R.id.dateTextView))
    }

    private fun updateDateTextView(dateTextView: TextView) {
        dateTextView.text = currentDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }

    override fun onTimetableLoadFailed(errorMessage: String) {
        Log.wtf("CompassFragment", "Timetable Load Failed: $errorMessage")

    }

    override fun onResume() {
        super.onResume()
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }

    override fun onPause() {
        super.onPause()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        // Clear the systemUiVisibility flag
        activity?.window?.decorView?.systemUiVisibility = 0
        show()
    }

    override fun onDestroy() {
        super.onDestroy()
        dummyButton = null
        fullscreenContent = null
        fullscreenContentControls = null
    }

    private fun toggle() {
        if (visible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        fullscreenContentControls?.visibility = View.GONE
        visible = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    @Suppress("InlinedApi")
    private fun show() {
        // Show the system bar
        fullscreenContent?.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        visible = true

        // Schedule a runnable to display UI elements after a delay
        hideHandler.removeCallbacks(hidePart2Runnable)
        hideHandler.postDelayed(showPart2Runnable, UI_ANIMATION_DELAY.toLong())
        (activity as? AppCompatActivity)?.supportActionBar?.show()
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, delayMillis.toLong())
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private const val UI_ANIMATION_DELAY = 300
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}