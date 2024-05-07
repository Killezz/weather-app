package com.weather.app

import android.app.Application
import android.location.Location
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.weather.app.weather.WeatherResponse
import kotlinx.coroutines.launch

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService = WeatherApiService.create()
    private val _weatherData = MutableLiveData<WeatherResponse>()
    private val locationRepository = LocationRepository(application)
    private val _location = mutableStateOf<Location?>(null)
    val weatherData: LiveData<WeatherResponse> = _weatherData

    private fun fetchWeatherData(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val response = apiService.getWeatherData(
                    latitude,
                    longitude
                )
                _weatherData.value = response
            } catch (e: Exception) {
                print(e.toString())
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

}