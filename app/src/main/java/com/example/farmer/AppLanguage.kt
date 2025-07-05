package com.example.farmer

import android.app.Application
import android.content.Context
import com.yariksoffice.lingver.Lingver
import java.util.Locale

class AppLanguage : Application() {
    override fun onCreate() {
        super.onCreate()


        // Get the selected language from SharedPreferences
        val selectedLanguage = this.selectedLanguage

        // Set the locale (language) globally based on user selection
        setLocale(selectedLanguage)
    }

    private val selectedLanguage: String
        // Retrieve the saved language from SharedPreferences
        get() {
            val preferences = getSharedPreferences(
                PREFS_NAME,
                MODE_PRIVATE
            )
            return preferences.getString(
                AppLanguage.Companion.SELECTED_LANGUAGE_KEY,
                "hi"
            )!! // Default to Hindi if not set
        }

    // Apply the selected language globally using Lingver
    private fun setLocale(languageCode: String) {
        // Create a Locale object from the language code
        val locale = Locale(languageCode)

        // Set the locale using Lingver
        Lingver.init(this, locale) // Initialize Lingver with the context and locale
    }

    companion object {
        private const val PREFS_NAME = "LanguagePrefs"
        private const val SELECTED_LANGUAGE_KEY = "SelectedLanguage"

        // Save the selected language to SharedPreferences
        fun saveLanguage(context: Context, languageCode: String) {
            // Save the language preference in SharedPreferences
            val preferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putString(SELECTED_LANGUAGE_KEY, languageCode)
            editor.apply()

            // Create the Locale object from the selected language code
            val locale = Locale(languageCode)

            // Use Lingver to set the locale globally
            Lingver.getInstance().setLocale(context, locale) // Correct way to set the locale
        }
    }
}
