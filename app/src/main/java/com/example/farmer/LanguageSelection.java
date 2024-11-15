package com.example.farmer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class LanguageSelection extends AppCompatActivity {

    private MaterialCardView hindiCard, gujaratiCard, marathiCard, bengaliCard;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);

        // Initialize UI components
        hindiCard = findViewById(R.id.hindiCard);
        gujaratiCard = findViewById(R.id.gujaratiCard);
        marathiCard = findViewById(R.id.marathiCard);
        bengaliCard = findViewById(R.id.bengaliCard);
        MaterialButton continueButton = findViewById(R.id.continueButton);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("LanguagePrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Set Click Listeners for Cards
        hindiCard.setOnClickListener(view -> setLanguage("hi"));
        gujaratiCard.setOnClickListener(view -> setLanguage("gu"));
        marathiCard.setOnClickListener(view -> setLanguage("mr"));
        bengaliCard.setOnClickListener(view -> setLanguage("bn-rIN"));

        // Continue Button Listener
        continueButton.setOnClickListener(view -> {
            String selectedLanguage = sharedPreferences.getString("SelectedLanguage", "hi");
            if (selectedLanguage != null) {
                // Apply language change immediately
                AppLanguage.saveLanguage(this, selectedLanguage);
                Toast.makeText(this, "Language Set to: " + selectedLanguage, Toast.LENGTH_SHORT).show();
                // Restart the app to apply the language change
                restartApp();
            } else {
                Toast.makeText(this, "Please select a language", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLanguage(String languageCode) {
        // Save the selected language to SharedPreferences
        AppLanguage.saveLanguage(this, languageCode);
    }

    private void restartApp() {
        // Create an intent to restart the current activity
        Intent intent = new Intent(LanguageSelection.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
