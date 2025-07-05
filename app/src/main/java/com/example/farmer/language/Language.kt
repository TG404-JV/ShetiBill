package com.example.farmer.language

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.farmer.MainActivity
import java.util.Locale

class Language : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load the saved language from SharedPreferences
        val prefs = getSharedPreferences("Settings", MODE_PRIVATE)
        val language: String = prefs.getString("My_Lang", "")!!
        if (!language.isEmpty()) {
            setLocale(language)
        }
    }

    fun setLocale(langCode: String) {
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        getBaseContext().getResources()
            .updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics())

        // Save language preference in SharedPreferences
        val editor = getSharedPreferences("Settings", MODE_PRIVATE).edit()
        editor.putString("My_Lang", langCode)
        editor.apply()
    }

    fun changeLanguageAndRestart(langCode: String) {
        setLocale(langCode)
        // Restart the application to apply the new language
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}
