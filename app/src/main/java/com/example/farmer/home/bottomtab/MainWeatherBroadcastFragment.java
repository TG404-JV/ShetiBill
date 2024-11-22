package com.example.farmer.home.bottomtab;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.farmer.R;
import com.example.farmer.home.Weather.WeatherService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

public class MainWeatherBroadcastFragment extends Fragment {
    private static final String PREF_NAME = "WeatherHistory"; // Unified preference name
    private static final String KEY_CITY = "lastCity";
    private static final String KEY_TEMPERATURE = "lastTemperature";
    private static final String KEY_DESCRIPTION = "lastDescription";
    private static final String KEY_HUMIDITY = "lastHumidity";
    private static final String KEY_WIND_SPEED = "lastWindSpeed";

    private WeatherService weatherService;
    private MaterialButton buttonGetCurrentLocationWeather;
    private TextView textViewTemperature;
    private TextView textViewDescription;
    private TextView textViewHumidity;
    private TextView textViewWindSpeed;
    private TextView textViewCropRecommendation;

    private FusedLocationProviderClient fusedLocationClient;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_weather_broadcast, container, false);

        buttonGetCurrentLocationWeather = view.findViewById(R.id.buttonGetCurrentLocationWeather);
        textViewTemperature = view.findViewById(R.id.textViewTemperature);
        textViewDescription = view.findViewById(R.id.textViewDescription);
        textViewHumidity = view.findViewById(R.id.textViewHumidity);
        textViewWindSpeed = view.findViewById(R.id.textViewWindSpeed);
        textViewCropRecommendation = view.findViewById(R.id.textViewCropRecommendation);

        weatherService = new WeatherService();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Load the last weather data if available
        loadSavedWeatherData();

        // Fetch weather by current location
        buttonGetCurrentLocationWeather.setOnClickListener(v -> fetchWeatherDataByLocation());

        return view;
    }

    private void fetchWeatherDataByLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        weatherService.fetchWeatherDataByCoordinates(latitude, longitude, new WeatherService.WeatherCallback() {
                            @Override
                            public void onWeatherReceived(WeatherService.WeatherData weatherData) {
                                requireActivity().runOnUiThread(() -> {
                                    updateWeatherUI(weatherData);
                                    updateCropRecommendation(weatherData);
                                    saveWeatherData("Current Location", weatherData);
                                });
                            }

                            @Override
                            public void onError(String errorMessage) {
                                requireActivity().runOnUiThread(() ->
                                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show());
                            }
                        });
                    } else {
                        Toast.makeText(requireContext(), "Unable to fetch location. Try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateWeatherUI(WeatherService.WeatherData weatherData) {
        textViewTemperature.setText(String.format("%.1f°C", weatherData.temperature));
        textViewDescription.setText(weatherData.description);
        textViewHumidity.setText(String.format("%d%% Humidity", weatherData.humidity));
        textViewWindSpeed.setText(String.format("%.1f km/h Wind", weatherData.windSpeed));
    }

    private void updateCropRecommendation(WeatherService.WeatherData weatherData) {
        String recommendation = generateCropRecommendation(weatherData);
        textViewCropRecommendation.setText(recommendation);
    }

    private String generateCropRecommendation(WeatherService.WeatherData weatherData) {
        if (weatherData.temperature >= 20 && weatherData.temperature <= 30) {
            if (weatherData.humidity >= 50 && weatherData.humidity <= 70) {
                return "Best suited for: Corn, Soybeans, Wheat";
            } else if (weatherData.humidity < 50) {
                return "Best suited for: Sorghum, Millet, Sunflower";
            } else {
                return "Best suited for: Rice, Sugarcane";
            }
        } else if (weatherData.temperature > 30) {
            return "Best suited for: Cotton, Millet, Sorghum";
        } else {
            return "Best suited for: Winter Wheat, Barley, Rye";
        }
    }

    private void saveWeatherData(String city, WeatherService.WeatherData weatherData) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_CITY, city);
        editor.putString(KEY_TEMPERATURE, String.format("%.1f°C", weatherData.temperature));
        editor.putString(KEY_DESCRIPTION, weatherData.description);
        editor.putInt(KEY_HUMIDITY, weatherData.humidity);
        editor.putString(KEY_WIND_SPEED, String.format("%.1f km/h", weatherData.windSpeed));

        editor.apply();
    }

    private void loadSavedWeatherData() {
        String city = sharedPreferences.getString(KEY_CITY, null);
        String temperature = sharedPreferences.getString(KEY_TEMPERATURE, null);
        String description = sharedPreferences.getString(KEY_DESCRIPTION, null);
        int humidity = sharedPreferences.getInt(KEY_HUMIDITY, -1);
        String windSpeed = sharedPreferences.getString(KEY_WIND_SPEED, null);

        if (city != null && temperature != null && description != null && humidity != -1 && windSpeed != null) {
            textViewTemperature.setText(temperature);
            textViewDescription.setText(description);
            textViewHumidity.setText(String.format("%d%% Humidity", humidity));
            textViewWindSpeed.setText(windSpeed);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchWeatherDataByLocation();
            } else {
                Toast.makeText(requireContext(), "Permission denied to access location", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
