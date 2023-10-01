package vce.nhs.pomodolock.fragments.ProfileFragments

// Android OS related imports
import android.os.Bundle
import android.os.Looper
import android.os.Handler
import android.content.Context
import android.view.WindowManager
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Environment
import android.util.Log

// Android UI related imports
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.Toast
import android.widget.Button
import android.widget.EditText

// AndroidX AppCompat and Fragment related imports
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import androidx.room.Room

// Others
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt
import vce.nhs.pomodolock.R
import vce.nhs.pomodolock.database.UserProfile.ProfileDao
import vce.nhs.pomodolock.database.UserProfile.ProfileDatabase
import vce.nhs.pomodolock.databinding.FragmentLoginBinding
import vce.nhs.pomodolock.fragments.HomeFragment.HomeFragment
import vce.nhs.pomodolock.fragments.ProfileFragment.SignUpFragment
import vce.nhs.pomodolock.utils.SharedPreferencesManager
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class LoginFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var profileDatabase: ProfileDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferencesManager = SharedPreferencesManager(requireContext())

        profileDatabase = Room.databaseBuilder(
            requireContext().applicationContext,
            ProfileDatabase::class.java,
            "user-database-name"
        ).build()

        val profileDao: ProfileDao = profileDatabase.profileDao()

        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        emailInput = view.findViewById(R.id.emailInput)
        passwordInput = view.findViewById(R.id.passwordInput)

        val loginButton: Button = view.findViewById(R.id.loginButton)
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            lifecycleScope.launch {
                val profile = profileDao.getProfileByEmail(email)

                if (isValidInput(email, password, profileDao)) {
                    sharedPreferencesManager.setLoggedIn(true)
                    sharedPreferencesManager.setUsername(email)

                    if (profile != null) {
                        if (profile.url != null) {
                            sharedPreferencesManager.setIcsUrl(profile.url)
                        }
                    }

                    Toast.makeText(requireContext(), "Logged in successfully!", Toast.LENGTH_SHORT)
                        .show()

                    // Log the login information to a text file.
                    logLoginInformation(requireContext(), email)

                    val homeFragment = HomeFragment()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, homeFragment)
                        .addToBackStack(null)
                        .commit()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Invalid email or password. Please check your credentials.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }



        val signUpButton: AppCompatButton = view.findViewById(R.id.signUpButton)
        signUpButton.setOnClickListener {
            val signUpFragment = SignUpFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, signUpFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun logLoginInformation(context: Context, username: String) {
        try {
            val fileName = "login_logs.txt"
            val fileContent = "User $username logged in at ${System.currentTimeMillis()}\n"

            val directory = context.getExternalFilesDir(null)

            if (directory != null) {
                val file = File(directory, fileName)

                // Create the file if it doesn't exist.
                if (!file.exists()) {
                    file.createNewFile()
                }

                val fileOutputStream = FileOutputStream(file, true)
                fileOutputStream.write(fileContent.toByteArray())
                fileOutputStream.close()

                Log.d("LoginLog", fileContent)
            } else {
                Log.e("LoginLog", "External storage directory is null")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    private fun isUserLoggedIn(): Boolean {
        return sharedPreferences.contains("loggedInEmail")
    }

    private fun saveLoggedInUser(email: String) {
        val editor = sharedPreferences.edit()
        editor.putString("loggedInEmail", email)
        editor.apply()
    }

    private suspend fun isValidInput(email: String, password: String, profileDao: ProfileDao): Boolean {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            // Check if the email exists in the database
            val profile = profileDao.getProfileByEmail(email)

            if (profile != null) {
                // Retrieve the user's salt and hash the input password for comparison
                val salt = profile.salt
                val hashedInputPassword = BCrypt.hashpw(password, salt)

                // Compare the hashed input password to the stored hashed password
                return hashedInputPassword == profile.password
            } else {
                // Email not found in the database
                return false
            }
        } else {
            // Either email or password is empty
            return false
        }
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

    private var _binding: FragmentLoginBinding? = null
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
