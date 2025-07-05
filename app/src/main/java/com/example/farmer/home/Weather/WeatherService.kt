package com.example.farmer.home.Weather

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min

class WeatherService {
    private val client: OkHttpClient

    init {
        this.client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    // Fetch weather data by city name
    fun fetchWeatherDataByCity(city: String?, callback: WeatherCallback) {
        val url = String.format("%s?q=%s&appid=%s&units=metric", BASE_URL, city, API_KEY)
        fetchWeatherData(url, callback)
    }

    // Fetch weather data by coordinates
    fun fetchWeatherDataByCoordinates(
        latitude: Double,
        longitude: Double,
        callback: WeatherCallback
    ) {
        val url = String.format(
            "%s?lat=%f&lon=%f&appid=%s&units=metric",
            BASE_URL,
            latitude,
            longitude,
            API_KEY
        )
        fetchWeatherData(url, callback)
    }

    // General method to fetch weather data
    fun fetchWeatherData(url: String, callback: WeatherCallback) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError("Network error: " + e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    callback.onError("Server error: " + response.code)
                    return
                }

                try {
                    val jsonData = response.body!!.string()
                    val weatherData = parseWeatherData(jsonData)
                    callback.onWeatherReceived(weatherData)
                } catch (e: JSONException) {
                    callback.onError("Parsing error: " + e.message)
                }
            }
        })
    }

    // Parse the JSON response into WeatherData
    private fun parseWeatherData(jsonData: String): WeatherData {
        val jsonObject = JSONObject(jsonData)

        // Extract main weather details
        val main = jsonObject.optJSONObject("main")
        val temperature = if (main != null) main.optDouble("temp", 0.0) else 0.0
        val humidity = if (main != null) main.optInt("humidity", 0) else 0

        // Extract weather description
        val weatherObj = jsonObject.getJSONArray("weather").optJSONObject(0)
        val description = if (weatherObj != null) weatherObj.optString(
            "description",
            "No description"
        ) else "No description"

        // Extract wind details
        val windObj = jsonObject.optJSONObject("wind")
        val windSpeed = if (windObj != null) windObj.optDouble("speed", 0.0) else 0.0

        // Estimate soil health data
        val phLevel = estimatePHLevel(temperature, humidity, windSpeed)
        val nutrientLevel = estimateNutrientLevel(temperature, humidity)

        return WeatherData(temperature, humidity, windSpeed, description, phLevel, nutrientLevel)
    }

    // PH level estimation based on temperature, humidity, and wind speed
    private fun estimatePHLevel(temperature: Double, humidity: Int, windSpeed: Double): Double {
        val basePH = 6.5
        val tempAdjustment = (temperature - 20) / 10
        val humidityAdjustment = (humidity - 50) / 50.0
        val windAdjustment = windSpeed / 10.0

        val estimatedPH = basePH + tempAdjustment + humidityAdjustment - windAdjustment
        return max(5.5, min(estimatedPH, 8.0)) // Ensure PH is within realistic bounds
    }

    // Nutrient level estimation based on temperature and humidity
    private fun estimateNutrientLevel(temperature: Double, humidity: Int): String {
        if (temperature > 30 && humidity > 60) return "High"
        if (temperature > 20 && humidity > 50) return "Moderate"
        return "Low"
    }

    // Callback interface
    interface WeatherCallback {
        fun onWeatherReceived(weatherData: WeatherData?)
        fun onError(errorMessage: String?)
    }

    // Weather data structure
    class WeatherData(
        val temperature: Double, val humidity: Int, val windSpeed: Double,
        val description: String?, val phLevel: Double, val nutrientLevel: String?
    )

    companion object {
        private const val API_KEY = "02ac04b938246d20bad8c2d267c9497e"
        private const val BASE_URL = "https://api.openweathermap.org/data/2.5/weather"
    }
}
