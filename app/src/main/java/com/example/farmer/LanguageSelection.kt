package com.example.farmer

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class LanguageSelection : AppCompatActivity() {
    private var hindiCard: MaterialCardView? = null
    private var gujaratiCard: MaterialCardView? = null
    private var marathiCard: MaterialCardView? = null
    private var bengaliCard: MaterialCardView? = null
    private var systemDefaultCard: MaterialCardView? = null
    private var systemDefaultRadio: RadioButton? = null
    private var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language_selection)

        // Hide the status bar and navigation bar
        getWindow().getDecorView().setSystemUiVisibility(
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Hides the navigation bar
                    or View.SYSTEM_UI_FLAG_FULLSCREEN // Hides the status bar
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) // Keeps it hidden until user swipes
        )

        // Initialize UI components
        hindiCard = findViewById<MaterialCardView>(R.id.hindiCard)
        gujaratiCard = findViewById<MaterialCardView>(R.id.gujaratiCard)
        marathiCard = findViewById<MaterialCardView>(R.id.marathiCard)
        bengaliCard = findViewById<MaterialCardView>(R.id.bengaliCard)
        systemDefaultCard = findViewById<MaterialCardView>(R.id.systemDefaultCard)
        systemDefaultRadio = findViewById<RadioButton>(R.id.systemDefaultRadio)
        val continueButton = findViewById<MaterialButton>(R.id.continueButton)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("LanguagePrefs", MODE_PRIVATE)

        // Retrieve and apply the saved language selection
        val savedLanguage: String = sharedPreferences!!.getString("SelectedLanguage", "en")!!
        applyLanguageSelectionEffect(savedLanguage)

        // Set Click Listeners for Cards
        hindiCard!!.setOnClickListener(View.OnClickListener { view: View? ->
            handleLanguageSelection(
                hindiCard,
                "hi"
            )
        })
        gujaratiCard!!.setOnClickListener(View.OnClickListener { view: View? ->
            handleLanguageSelection(
                gujaratiCard,
                "gu"
            )
        })
        marathiCard!!.setOnClickListener(View.OnClickListener { view: View? ->
            handleLanguageSelection(
                marathiCard,
                "mr"
            )
        })
        bengaliCard!!.setOnClickListener(View.OnClickListener { view: View? ->
            handleLanguageSelection(
                bengaliCard,
                "bn"
            )
        })
        systemDefaultCard!!.setOnClickListener(View.OnClickListener { view: View? -> handleSystemDefaultSelection() })

        // Continue Button Listener
        continueButton.setOnClickListener(View.OnClickListener { view: View? ->
            val selectedLanguage: String = sharedPreferences!!.getString("SelectedLanguage", "en")!!
            if (selectedLanguage != null) {
                AppLanguage.saveLanguage(this, selectedLanguage)
                Toast.makeText(this, "Language Set to: " + selectedLanguage, Toast.LENGTH_SHORT)
                    .show()
                restartApp()
            } else {
                Toast.makeText(this, "Please select a language", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * Handles the language selection and updates UI effects.
     */
    private fun handleLanguageSelection(selectedCardView: MaterialCardView?, languageCode: String) {
        applyLanguageSelectionEffect(languageCode)

        // Save the selected language to SharedPreferences
        AppLanguage.saveLanguage(this, languageCode)
    }

    /**
     * Handles the System Default selection.
     */
    private fun handleSystemDefaultSelection() {
        // Apply UI effect to indicate selection
        applyLanguageSelectionEffect("en")

        // Save "System Default" as the selected language
        AppLanguage.saveLanguage(this, "en")
    }

    /**
     * Applies visual feedback for the selected language card.
     */
    private fun applyLanguageSelectionEffect(languageCode: String) {
        // Reset all cards
        resetCardEffects()

        // Highlight the selected card
        when (languageCode) {
            "hi" -> highlightSelectedCard(hindiCard!!)
            "gu" -> highlightSelectedCard(gujaratiCard!!)
            "mr" -> highlightSelectedCard(marathiCard!!)
            "bn" -> highlightSelectedCard(bengaliCard!!)
            "en" -> {
                highlightSelectedCard(systemDefaultCard!!)
                systemDefaultRadio!!.setChecked(true)
            }

            else -> {}
        }
    }

    /**
     * Adds a highlight effect to the selected card.
     */
    private fun highlightSelectedCard(cardView: MaterialCardView) {
        cardView.setStrokeColor(getColor(R.color.purple_500)) // Highlight color
        cardView.setStrokeWidth(6) // Increase stroke width
    }

    /**
     * Resets visual effects on all language cards.
     */
    private fun resetCardEffects() {
        hindiCard!!.setStrokeColor(getColor(R.color.blockBGColor))
        gujaratiCard!!.setStrokeColor(getColor(R.color.blockBGColor))
        marathiCard!!.setStrokeColor(getColor(R.color.blockBGColor))
        bengaliCard!!.setStrokeColor(getColor(R.color.blockBGColor))
        systemDefaultCard!!.setStrokeColor(getColor(R.color.blockBGColor))

        hindiCard!!.setStrokeWidth(2)
        gujaratiCard!!.setStrokeWidth(2)
        marathiCard!!.setStrokeWidth(2)
        bengaliCard!!.setStrokeWidth(2)
        systemDefaultCard!!.setStrokeWidth(2)

        // Uncheck the System Default radio button
        systemDefaultRadio!!.setChecked(false)
    }

    private fun restartApp() {
        val intent = Intent(this@LanguageSelection, HomePageActivity::class.java)
        startActivity(intent)
        finish()
    }
}
