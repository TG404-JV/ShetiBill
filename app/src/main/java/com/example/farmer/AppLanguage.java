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
        setLocale(this, selectedLanguage);
    }

    // Retrieve the saved language from SharedPreferences
    private String getSelectedLanguage() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return preferences.getString(SELECTED_LANGUAGE_KEY, "hi"); // Default to Hindi if not set
    }

    // Apply the selected language globally using Lingver
    private void setLocale(Context context, String languageCode) {
        Locale locale;
        switch (languageCode) {
            case "hi":
                locale = Locale.forLanguageTag("hi"); // Hindi
                break;
            case "gu":
                locale = Locale.forLanguageTag("gu"); // Gujarati
                break;
            case "mr":
                locale = Locale.forLanguageTag("mr"); // Marathi
                break;
            case "bn":
                locale = Locale.forLanguageTag("bn"); // Bengali
                break;
            default:
                locale = Locale.getDefault(); // System default if no selection
                break;
        }

        // Set the default locale and initialize Lingver
        Lingver.init((Application) context, locale);
    }

    // Save the selected language to SharedPreferences
    public static void saveLanguage(Context context, String languageCode) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SELECTED_LANGUAGE_KEY, languageCode);
        editor.apply();
    }
}
