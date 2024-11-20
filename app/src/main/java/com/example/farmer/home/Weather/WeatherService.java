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

    public void fetchWeatherData(String city, WeatherCallback callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        String url = String.format("%s?q=%s&appid=%s&units=metric", BASE_URL, city, API_KEY);

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

    private WeatherData parseWeatherData(String jsonData) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonData);

        // Main weather details
        JSONObject main = jsonObject.getJSONObject("main");
        double temperature = main.getDouble("temp");
        int humidity = main.getInt("humidity");

        // Weather description
        JSONObject weatherObj = jsonObject.getJSONArray("weather").getJSONObject(0);
        String description = weatherObj.getString("description");

        // Wind details
        JSONObject windObj = jsonObject.getJSONObject("wind");
        double windSpeed = windObj.getDouble("speed");

        // Soil health estimations
        double phLevel = estimatePHLevel(temperature, humidity);
        String nutrientLevel = estimateNutrientLevel(temperature, humidity);

        return new WeatherData(temperature, humidity, windSpeed, description, phLevel, nutrientLevel);
    }

    private double estimatePHLevel(double temperature, int humidity) {
        // Simple estimation based on temperature and humidity
        double basePH = 6.5;
        double adjustment = (temperature / 30) * 0.5 + (humidity / 100) * 0.5;
        return Math.min(Math.max(basePH + adjustment, 5.5), 7.5);
    }

    private String estimateNutrientLevel(double temperature, int humidity) {
        // Simple nutrient level estimation
        if (temperature > 25 && humidity > 60) return "High";
        if (temperature > 20 && humidity > 50) return "Moderate";
        return "Low";
    }

    public interface WeatherCallback {
        void onWeatherReceived(WeatherData weatherData);
        void onError(String errorMessage);
    }

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
