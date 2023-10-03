package vce.nhs.pomodolock.fragments.ProfileFragment


import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope

import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mindrot.jbcrypt.BCrypt
import vce.nhs.pomodolock.R
import vce.nhs.pomodolock.database.UserProfile.Profile
import vce.nhs.pomodolock.database.UserProfile.ProfileDatabase
import vce.nhs.pomodolock.databinding.FragmentSignupBinding
import vce.nhs.pomodolock.fragments.ProfileFragments.LoginFragment
import kotlin.random.Random

class SignUpFragment : Fragment() {
    private lateinit var profileDatabase: ProfileDatabase
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var urlInput: EditText
    private lateinit var signUpButton: Button
    private lateinit var loginButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the Room Database
        profileDatabase = Room.databaseBuilder(
            requireContext().applicationContext,
            ProfileDatabase::class.java,
            "user-database-name"
        ).fallbackToDestructiveMigration().build()

        emailInput = view.findViewById(R.id.emailInput)
        passwordInput = view.findViewById(R.id.passwordInput)
        confirmPasswordInput = view.findViewById(R.id.confirmPasswordInput)
        urlInput = view.findViewById(R.id.urlInput)

        signUpButton = view.findViewById(R.id.signUpButton)
        signUpButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()
            val url = urlInput.text.toString().trim()

            if (isValidInput(email, password, confirmPassword)) {
                // Generate a random salt
                val salt = BCrypt.gensalt()

                // Hash the password using the generated salt
                val hashedPassword = BCrypt.hashpw(password, salt)

                val user = Profile(email = email, password = hashedPassword, salt = salt, url = url)

                GlobalScope.launch(Dispatchers.IO) {
                    // Insert the user into the database
                    withContext(Dispatchers.Main) {
                        insertUserIntoDatabase(user)
                        logAllUsers()
                    }
                }

                // Navigate to the edit profile page or perform other actions after sign-up
                // For demonstration purposes, let's show a toast indicating successful sign-up
                Toast.makeText(requireContext(), "Sign-up successful!", Toast.LENGTH_SHORT).show()

                val loginFragment = LoginFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, loginFragment)
                    .addToBackStack(null)
                    .commit()
            } else {
                // Handle invalid input (e.g., show error messages)
                Toast.makeText(requireContext(), "Invalid input. Please check your entries.", Toast.LENGTH_SHORT).show()
            }
        }

        loginButton = view.findViewById(R.id.loginButton)
        loginButton.setOnClickListener() {
            val loginFragment = LoginFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, loginFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private suspend fun logAllUsers() {
        // Fetch all users from the database
        lifecycleScope.launch(Dispatchers.IO) {
            val allUsers = profileDatabase.profileDao().getAllUsers()
            withContext(Dispatchers.Main) {
                // Log the user data
                for (user in allUsers) {
                    Log.w("UserData", "Email: ${user.email}, Password: ${user.password}, URL: ${user.url ?: "N/A"}")
                }
            }
        }
    }

    private fun isValidInput(email: String, password: String, confirmPassword: String): Boolean {
        // Check if the email is valid, passwords match, etc.
        return email.isNotEmpty() && password.isNotEmpty() && password == confirmPassword
    }

    private suspend fun insertUserIntoDatabase(user: Profile) {
        // Insert the user into the database using Room
        profileDatabase.profileDao().insert(user)
    }

    private fun generateRandomId(): Long {
        // Generate a random ID for the user
        return Random.nextLong(1, Long.MAX_VALUE)
    }

    // Full screen (Hide action + top bar)

    private val hideHandler = Handler(Looper.myLooper()!!)
    private var fullscreenContentControls: View? = null

    @Suppress("InlinedApi")
    private val hidePart2Runnable = Runnable {
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
        fullscreenContentControls?.visibility = View.VISIBLE
    }
    private var visible: Boolean = false
    private val hideRunnable = Runnable { hide() }

    @SuppressLint("ClickableViewAccessibility")
    private val delayHideTouchListener = View.OnTouchListener { _, _ ->
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    override fun onResume() {
        super.onResume()
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        delayedHide(100)
    }

    override fun onPause() {
        super.onPause()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        activity?.window?.decorView?.systemUiVisibility = 0
        show()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun toggle() {
        if (visible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        fullscreenContentControls?.visibility = View.GONE
        visible = false
        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    @Suppress("InlinedApi")
    private fun show() {
        visible = true
        hideHandler.removeCallbacks(hidePart2Runnable)
        hideHandler.postDelayed(showPart2Runnable, UI_ANIMATION_DELAY.toLong())
        (activity as? AppCompatActivity)?.supportActionBar?.show()
    }

    private fun delayedHide(delayMillis: Int) {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, delayMillis.toLong())
    }

    companion object {
        private const val AUTO_HIDE = true
        private const val AUTO_HIDE_DELAY_MILLIS = 3000
        private const val UI_ANIMATION_DELAY = 300
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
