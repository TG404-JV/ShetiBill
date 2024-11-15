package com.example.farmer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class LanguageSelection extends AppCompatActivity {

    private MaterialCardView hindiCard, gujaratiCard, marathiCard, bengaliCard, systemDefaultCard;
    private RadioButton systemDefaultRadio;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);

        // Initialize UI components
        hindiCard = findViewById(R.id.hindiCard);
        gujaratiCard = findViewById(R.id.gujaratiCard);
        marathiCard = findViewById(R.id.marathiCard);
        bengaliCard = findViewById(R.id.bengaliCard);
        systemDefaultCard = findViewById(R.id.systemDefaultCard);
        systemDefaultRadio = findViewById(R.id.systemDefaultRadio);
        MaterialButton continueButton = findViewById(R.id.continueButton);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("LanguagePrefs", MODE_PRIVATE);

        // Retrieve and apply the saved language selection
        String savedLanguage = sharedPreferences.getString("SelectedLanguage", "en");
        applyLanguageSelectionEffect(savedLanguage);

        // Set Click Listeners for Cards
        hindiCard.setOnClickListener(view -> handleLanguageSelection(hindiCard, "hi"));
        gujaratiCard.setOnClickListener(view -> handleLanguageSelection(gujaratiCard, "gu"));
        marathiCard.setOnClickListener(view -> handleLanguageSelection(marathiCard, "mr"));
        bengaliCard.setOnClickListener(view -> handleLanguageSelection(bengaliCard, "bn"));
        systemDefaultCard.setOnClickListener(view -> handleSystemDefaultSelection());

        // Continue Button Listener
        continueButton.setOnClickListener(view -> {
            String selectedLanguage = sharedPreferences.getString("SelectedLanguage", "en");
            if (selectedLanguage != null) {
                AppLanguage.saveLanguage(this, selectedLanguage);
                Toast.makeText(this, "Language Set to: " + selectedLanguage, Toast.LENGTH_SHORT).show();
                restartApp();
            } else {
                Toast.makeText(this, "Please select a language", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Handles the language selection and updates UI effects.
     */
    private void handleLanguageSelection(MaterialCardView selectedCardView, String languageCode) {
        applyLanguageSelectionEffect(languageCode);

        // Save the selected language to SharedPreferences
        AppLanguage.saveLanguage(this, languageCode);
    }

    /**
     * Handles the System Default selection.
     */
    private void handleSystemDefaultSelection() {
        // Apply UI effect to indicate selection
        applyLanguageSelectionEffect("en");

        // Save "System Default" as the selected language
        AppLanguage.saveLanguage(this, "en");
    }

    /**
     * Applies visual feedback for the selected language card.
     */
    private void applyLanguageSelectionEffect(String languageCode) {
        // Reset all cards
        resetCardEffects();

        // Highlight the selected card
        switch (languageCode) {
            case "hi":
                highlightSelectedCard(hindiCard);
                break;
            case "gu":
                highlightSelectedCard(gujaratiCard);
                break;
            case "mr":
                highlightSelectedCard(marathiCard);
                break;
            case "bn":
                highlightSelectedCard(bengaliCard);
                break;
            case "en": // System Default
                highlightSelectedCard(systemDefaultCard);
                systemDefaultRadio.setChecked(true);
                break;
            default:
                break;
        }
    }

    /**
     * Adds a highlight effect to the selected card.
     */
    private void highlightSelectedCard(MaterialCardView cardView) {
        cardView.setStrokeColor(getColor(R.color.purple_500)); // Highlight color
        cardView.setStrokeWidth(6); // Increase stroke width
    }

    /**
     * Resets visual effects on all language cards.
     */
    private void resetCardEffects() {
        hindiCard.setStrokeColor(getColor(R.color.blockBGColor));
        gujaratiCard.setStrokeColor(getColor(R.color.blockBGColor));
        marathiCard.setStrokeColor(getColor(R.color.blockBGColor));
        bengaliCard.setStrokeColor(getColor(R.color.blockBGColor));
        systemDefaultCard.setStrokeColor(getColor(R.color.blockBGColor));

        hindiCard.setStrokeWidth(2);
        gujaratiCard.setStrokeWidth(2);
        marathiCard.setStrokeWidth(2);
        bengaliCard.setStrokeWidth(2);
        systemDefaultCard.setStrokeWidth(2);

        // Uncheck the System Default radio button
        systemDefaultRadio.setChecked(false);
    }

    private void restartApp() {
        Intent intent = new Intent(LanguageSelection.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
