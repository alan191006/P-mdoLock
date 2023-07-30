package vce.nhs.pomodolock.fragments.LoginFragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import vce.nhs.pomodolock.R

class LoginFragment : Fragment() {
    // ...

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        emailInput = view.findViewById(R.id.emailInput)
        passwordInput = view.findViewById(R.id.passwordInput)

        val loginButton: Button = view.findViewById(R.id.loginButton)
        loginButton.setOnClickListener {
            // Get the input from the EditText fields
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (isValidInput(email, password)) {
                // Implement your login logic here
                // For demonstration purposes, let's just show a toast indicating successful login
                Toast.makeText(requireContext(), "Logged in successfully!", Toast.LENGTH_SHORT).show()

                // Navigate to the edit profile page or perform other actions after login
            } else {
                // Handle invalid input (e.g., show error messages)
                Toast.makeText(requireContext(), "Invalid input. Please check your email and password.", Toast.LENGTH_SHORT).show()
            }
        }

        val logoutButton: Button = view.findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            // Handle the logout process
            clearUserData()

            // Perform any other actions after logout (e.g., navigate to the login page)
        }
    }

    private fun isValidInput(email: String, password: String): Boolean {
        // Implement your validation logic here
        // For example, you can check if the email and password meet certain criteria
        return email.isNotEmpty() && password.isNotEmpty()
    }

    private fun clearUserData() {
        // Clear user data from SharedPreferences when logging out
        val editor = sharedPreferences.edit()
        editor.remove("email")
        editor.remove("password")
        editor.remove("url")
        editor.apply()
    }
}
