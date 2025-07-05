package com.example.farmer.home.bottomtab

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.farmer.R
import com.example.farmer.home.Weather.WeatherService
import com.example.farmer.home.Weather.WeatherService.WeatherCallback
import com.example.farmer.home.Weather.WeatherService.WeatherData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import java.util.Locale

class MainWeatherBroadcastFragment : Fragment() {
    private var weatherService: WeatherService? = null
    private var buttonGetCurrentLocationWeather: MaterialButton? = null
    private var textViewTemperature: TextView? = null
    private var textViewDescription: TextView? = null
    private var textViewHumidity: TextView? = null
    private var textViewWindSpeed: TextView? = null
    private var textViewCropRecommendation: TextView? = null
    private var textViewLocationName: TextView? = null

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var sharedPreferences: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_weather_broadcast, container, false)

        buttonGetCurrentLocationWeather =
            view.findViewById<MaterialButton>(R.id.buttonGetCurrentLocationWeather)
        textViewTemperature = view.findViewById<TextView>(R.id.textViewTemperature)
        textViewDescription = view.findViewById<TextView>(R.id.textViewDescription)
        textViewHumidity = view.findViewById<TextView>(R.id.textViewHumidity)
        textViewWindSpeed = view.findViewById<TextView>(R.id.textViewWindSpeed)
        textViewCropRecommendation = view.findViewById<TextView>(R.id.textViewCropRecommendation)
        textViewLocationName = view.findViewById<TextView>(R.id.textViewLocationName)

        weatherService = WeatherService()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        // Load the last weather data if available
        loadSavedWeatherData()

        // Fetch weather by current location
        buttonGetCurrentLocationWeather!!.setOnClickListener(View.OnClickListener { v: View? -> fetchWeatherDataByLocation() })

        return view
    }

    private fun fetchWeatherDataByLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }

        fusedLocationClient!!.getLastLocation()
            .addOnCompleteListener(OnCompleteListener { task: Task<Location?>? ->
                val location = task!!.getResult()
                if (location != null) {
                    val latitude = location.getLatitude()
                    val longitude = location.getLongitude()

                    // Fetch location name (e.g., city)
                    val locationName = getLocationName(latitude, longitude)
                    if (locationName != null) {
                        textViewLocationName!!.setText(locationName)
                    } else {
                        textViewLocationName!!.setText("Unknown Location")
                    }

                    // Fetch weather data
                    weatherService!!.fetchWeatherDataByCoordinates(
                        latitude,
                        longitude,
                        object : WeatherCallback {
                            override fun onWeatherReceived(weatherData: WeatherData?) {
                                requireActivity().runOnUiThread(Runnable {
                                    updateWeatherUI(weatherData)
                                    updateCropRecommendation(weatherData)
                                    saveWeatherData(
                                        if (locationName != null) locationName else "Unknown Location",
                                        weatherData
                                    )
                                })
                            }

                            override fun onError(errorMessage: String?) {
                                requireActivity().runOnUiThread(Runnable {
                                    Toast.makeText(
                                        requireContext(),
                                        errorMessage,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                })
                            }
                        })
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Unable to fetch location. Try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun getLocationName(latitude: Double, longitude: Double): String? {
        try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0)!!.getLocality() // Get city or locality name
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun updateWeatherUI(weatherData: WeatherData?) {
        textViewTemperature!!.setText(String.format("%.1f°C", weatherData?.temperature))
        textViewDescription!!.setText(weatherData?.description)
        textViewHumidity!!.setText(String.format("%d%% Humidity", weatherData?.humidity))
        textViewWindSpeed!!.setText(String.format("%.1f km/h Wind", weatherData?.windSpeed))
    }

    private fun updateCropRecommendation(weatherData: WeatherData?) {
        val recommendation = generateCropRecommendation(weatherData)
        textViewCropRecommendation!!.setText(recommendation)
    }

    private fun generateCropRecommendation(weatherData: WeatherData?): String {
        if (weatherData?.temperature!! >= 20 && weatherData?.temperature!! <= 30) {
            if (weatherData.humidity >= 50 && weatherData.humidity <= 70) {
                return "Best suited for: Corn, Soybeans, Wheat"
            } else if (weatherData.humidity < 50) {
                return "Best suited for: Sorghum, Millet, Sunflower"
            } else {
                return "Best suited for: Rice, Sugarcane"
            }
        } else if (weatherData.temperature > 30) {
            return "Best suited for: Cotton, Millet, Sorghum"
        } else {
            return "Best suited for: Winter Wheat, Barley, Rye"
        }
    }

    private fun saveWeatherData(city: String?, weatherData: WeatherData?) {
        val editor = sharedPreferences!!.edit()

        editor.putString(KEY_CITY, city)
        editor.putString(KEY_TEMPERATURE, String.format("%.1f°C", weatherData?.temperature))
        editor.putString(KEY_DESCRIPTION, weatherData?.description)
        editor.putInt(KEY_HUMIDITY, weatherData?.humidity ?: 0)
        editor.putString(KEY_WIND_SPEED, String.format("%.1f km/h", weatherData?.windSpeed))

        editor.apply()
    }

    private fun loadSavedWeatherData() {
        val city = sharedPreferences!!.getString(KEY_CITY, null)
        val temperature = sharedPreferences!!.getString(KEY_TEMPERATURE, null)
        val description = sharedPreferences!!.getString(KEY_DESCRIPTION, null)
        val humidity = sharedPreferences!!.getInt(KEY_HUMIDITY, -1)
        val windSpeed = sharedPreferences!!.getString(KEY_WIND_SPEED, null)

        if (city != null && temperature != null && description != null && humidity != -1 && windSpeed != null) {
            textViewLocationName!!.setText(city)
            textViewTemperature!!.setText(temperature)
            textViewDescription!!.setText(description)
            textViewHumidity!!.setText(String.format("%d%% Humidity", humidity))
            textViewWindSpeed!!.setText(windSpeed)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == 100) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchWeatherDataByLocation()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permission denied to access location",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    companion object {
        private const val PREF_NAME = "WeatherHistory" // Unified preference name
        private const val KEY_CITY = "lastCity"
        private const val KEY_TEMPERATURE = "lastTemperature"
        private const val KEY_DESCRIPTION = "lastDescription"
        private const val KEY_HUMIDITY = "lastHumidity"
        private const val KEY_WIND_SPEED = "lastWindSpeed"
    }
}
