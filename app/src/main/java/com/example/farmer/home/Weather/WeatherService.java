package com.example.farmer.home.Weather;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherService {
    private static final String API_KEY = "02ac04b938246d20bad8c2d267c9497e";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    private final OkHttpClient client;

    public WeatherService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    // Fetch weather data by city name
    public void fetchWeatherDataByCity(String city, WeatherCallback callback) {
        String url = String.format("%s?q=%s&appid=%s&units=metric", BASE_URL, city, API_KEY);
        fetchWeatherData(url, callback);
    }

    // Fetch weather data by coordinates
    public void fetchWeatherDataByCoordinates(double latitude, double longitude, WeatherCallback callback) {
        String url = String.format("%s?lat=%f&lon=%f&appid=%s&units=metric", BASE_URL, latitude, longitude, API_KEY);
        fetchWeatherData(url, callback);
    }

    // General method to fetch weather data
    public void fetchWeatherData(String url, WeatherCallback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("Server error: " + response.code());
                    return;
                }

                try {
                    String jsonData = response.body().string();
                    WeatherData weatherData = parseWeatherData(jsonData);
                    callback.onWeatherReceived(weatherData);
                } catch (JSONException e) {
                    callback.onError("Parsing error: " + e.getMessage());
                }
            }
        });
    }

    // Parse the JSON response into WeatherData
    private WeatherData parseWeatherData(String jsonData) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonData);

        // Extract main weather details
        JSONObject main = jsonObject.optJSONObject("main");
        double temperature = main != null ? main.optDouble("temp", 0.0) : 0.0;
        int humidity = main != null ? main.optInt("humidity", 0) : 0;

        // Extract weather description
        JSONObject weatherObj = jsonObject.getJSONArray("weather").optJSONObject(0);
        String description = weatherObj != null ? weatherObj.optString("description", "No description") : "No description";

        // Extract wind details
        JSONObject windObj = jsonObject.optJSONObject("wind");
        double windSpeed = windObj != null ? windObj.optDouble("speed", 0.0) : 0.0;

        // Estimate soil health data
        double phLevel = estimatePHLevel(temperature, humidity, windSpeed);
        String nutrientLevel = estimateNutrientLevel(temperature, humidity);

        return new WeatherData(temperature, humidity, windSpeed, description, phLevel, nutrientLevel);
    }

    // PH level estimation based on temperature, humidity, and wind speed
    private double estimatePHLevel(double temperature, int humidity, double windSpeed) {
        double basePH = 6.5;
        double tempAdjustment = (temperature - 20) / 10;
        double humidityAdjustment = (humidity - 50) / 50.0;
        double windAdjustment = windSpeed / 10.0;

        double estimatedPH = basePH + tempAdjustment + humidityAdjustment - windAdjustment;
        return Math.max(5.5, Math.min(estimatedPH, 8.0)); // Ensure PH is within realistic bounds
    }

    // Nutrient level estimation based on temperature and humidity
    private String estimateNutrientLevel(double temperature, int humidity) {
        if (temperature > 30 && humidity > 60) return "High";
        if (temperature > 20 && humidity > 50) return "Moderate";
        return "Low";
    }

    // Callback interface
    public interface WeatherCallback {
        void onWeatherReceived(WeatherData weatherData);
        void onError(String errorMessage);
    }

    // Weather data structure
    public static class WeatherData {
        public final double temperature;
        public final int humidity;
        public final double windSpeed;
        public final String description;
        public final double phLevel;
        public final String nutrientLevel;

        public WeatherData(double temperature, int humidity, double windSpeed,
                           String description, double phLevel, String nutrientLevel) {
            this.temperature = temperature;
            this.humidity = humidity;
            this.windSpeed = windSpeed;
            this.description = description;
            this.phLevel = phLevel;
            this.nutrientLevel = nutrientLevel;
        }
    }
}
