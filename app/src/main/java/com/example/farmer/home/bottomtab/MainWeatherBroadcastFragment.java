package com.example.farmer.home.bottomtab;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.farmer.R;
import com.example.farmer.home.Weather.WeatherService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class MainWeatherBroadcastFragment extends Fragment {
    private WeatherService weatherService;
    private TextInputEditText editTextCity;
    private MaterialButton buttonGetWeather;
    private TextView textViewTemperature;
    private TextView textViewDescription;
    private TextView textViewHumidity;
    private TextView textViewWindSpeed;
    private TextView textViewCropRecommendation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_weather_broadcast, container, false);

        // Initialize views
        editTextCity = view.findViewById(R.id.editTextCity);
        buttonGetWeather = view.findViewById(R.id.buttonGetWeather);
        textViewTemperature = view.findViewById(R.id.textViewTemperature);
        textViewDescription = view.findViewById(R.id.textViewDescription);
        textViewHumidity = view.findViewById(R.id.textViewHumidity);
        textViewWindSpeed = view.findViewById(R.id.textViewWindSpeed);
        textViewCropRecommendation = view.findViewById(R.id.textViewCropRecommendation);

        weatherService = new WeatherService();

        buttonGetWeather.setOnClickListener(v -> {
            String city = editTextCity.getText().toString().trim();
            if (!city.isEmpty()) {
                fetchWeatherData(city);
            } else {
                editTextCity.setError("Please enter a city name");
            }
        });

        return view;
    }

    private void fetchWeatherData(String city) {
        weatherService.fetchWeatherData(city, new WeatherService.WeatherCallback() {
            @Override
            public void onWeatherReceived(WeatherService.WeatherData weatherData) {
                requireActivity().runOnUiThread(() -> {
                    updateWeatherUI(weatherData);
                    updateCropRecommendation(weatherData);
                });
            }

            @Override
            public void onError(String errorMessage) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                });
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
}