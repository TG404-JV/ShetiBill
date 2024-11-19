package com.example.farmer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.yariksoffice.lingver.Lingver;

import java.util.Locale;

public class AppLanguage extends Application {

    private static final String PREFS_NAME = "LanguagePrefs";
    private static final String SELECTED_LANGUAGE_KEY = "SelectedLanguage";

    @Override
    public void onCreate() {
        super.onCreate();


        // Get the selected language from SharedPreferences
        String selectedLanguage = getSelectedLanguage();

        // Set the locale (language) globally based on user selection
        setLocale(selectedLanguage);
    }

    // Retrieve the saved language from SharedPreferences
    private String getSelectedLanguage() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return preferences.getString(SELECTED_LANGUAGE_KEY, "hi"); // Default to Hindi if not set
    }

    // Apply the selected language globally using Lingver
    private void setLocale(String languageCode) {
        // Create a Locale object from the language code
        Locale locale = new Locale(languageCode);

        // Set the locale using Lingver
        Lingver.init(this, locale);  // Initialize Lingver with the context and locale
    }

    // Save the selected language to SharedPreferences
    public static void saveLanguage(Context context, String languageCode) {
        // Save the language preference in SharedPreferences
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SELECTED_LANGUAGE_KEY, languageCode);
        editor.apply();

        // Create the Locale object from the selected language code
        Locale locale = new Locale(languageCode);

        // Use Lingver to set the locale globally
        Lingver.getInstance().setLocale(context, locale); // Correct way to set the locale
    }
}
