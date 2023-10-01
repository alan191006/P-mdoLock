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

    fun setUsername(username: String) {
        sharedPreferences.edit().putString("username", username).apply()
    }

    fun getUsername(): String? {
        return sharedPreferences.getString("username", null)
    }

    fun setIcsUrl(url: String) {
        sharedPreferences.edit().putString("url", url).apply()
    }

    fun getIcsUrl(): String? {
        return sharedPreferences.getString("url", null)
    }
}