package com.weather.app.weather

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.weather.app.LocationRepository
import kotlinx.coroutines.launch

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService = WeatherApiService.create()
    private val _weatherData = MutableLiveData<WeatherResponse?>()
    private val locationRepository = LocationRepository(application)
    private val _location = mutableStateOf<Location?>(null)
    val weatherData: MutableLiveData<WeatherResponse?> = _weatherData

    private fun fetchWeatherData(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val response = apiService.getWeatherData(
                    latitude,
                    longitude
                )
                _weatherData.value = response
            } catch (e: Exception) {
                Log.d("WeatherViewModel", "Error: $e")
            }
        }
    }

    fun startLocationUpdates() {
        locationRepository.startLocationUpdates { location ->
            _location.value = location
            if (location != null) {
                fetchWeatherData(location.latitude, location.longitude)
            }

        }
    }

    fun weatherCodeToEmoji(weatherCode: Int = -1): String {
        return when (weatherCode) {
            0 -> "☀️"
            in 1..3 -> "☁️"
            in listOf(45, 48) -> "🌫️"
            in listOf(51, 53, 55) -> "🌧️"
            in listOf(56, 57) -> "❄️"
            in listOf(61, 63, 65, 77) -> "🌧️"
            in listOf(66, 67) -> "❄️"
            in listOf(71, 73, 75) -> "🌨️"
            in listOf(80, 81, 82) -> "🌧️"
            in listOf(85, 86) -> "🌨️"
            in listOf(95, 96, 99) -> "⛈️"
            else -> "❓"
        }
    }

}