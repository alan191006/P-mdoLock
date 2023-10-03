package vce.nhs.pomodolock.utils

import android.content.Context

class SharedPreferencesManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    fun setLoggedIn(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean("isLoggedIn", isLoggedIn).apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    fun setUserEmail(email: String) {
        sharedPreferences.edit().putString("email", email).apply()
    }

    fun getUserEmail(): String? {
        return sharedPreferences.getString("email", null)
    }

    fun setIcsUrl(url: String) {
        sharedPreferences.edit().putString("url", url).apply()
    }

    fun getIcsUrl(): String? {
        return sharedPreferences.getString("url", null)
    }
}