package vce.nhs.pomodolock

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SessionActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var backButton: FloatingActionButton

    private val countdownDuration: Long = 60000 // 1 minute in milliseconds
    private val countdownInterval: Long = 1000 // Update interval for countdown timer

    private lateinit var countdownTimer: CountDownTimer

    private var isTimerFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session)

        progressBar = findViewById(R.id.progressBar)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            if (isTimerFinished) {
                onBackPressed() // Navigate back to HomeFragment when the button is clicked and timer is finished
            } else {
                // Show a message when the timer is still running
                Toast.makeText(this, "Timer is still running!", Toast.LENGTH_SHORT).show()
            }
        }

        // Set up the countdown timer
        countdownTimer = object : CountDownTimer(countdownDuration, countdownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                val progress = ((countdownDuration - millisUntilFinished) / countdownInterval).toInt()
                progressBar.progress = progress
            }

            override fun onFinish() {
                isTimerFinished = true
            }
        }

        // Start the countdown timer
        countdownTimer.start()
    }

    override fun onBackPressed() {
        if (!isTimerFinished) {
            // Show a message when the user tries to go back before the timer finishes
            Toast.makeText(this, "Timer is still running!", Toast.LENGTH_SHORT).show()
            return
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        countdownTimer.cancel() // Cancel the countdown timer to prevent memory leaks
    }
}
