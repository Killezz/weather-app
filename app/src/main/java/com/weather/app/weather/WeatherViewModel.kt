package com.weather.app.weather

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.weather.app.LocationRepository
import com.weather.app.R
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService = WeatherApiService.create()
    private val _weatherData = MutableLiveData<WeatherResponse?>()
    private val locationRepository = LocationRepository(application)
    private val _location = mutableStateOf<Location?>(null)
    val weatherData: MutableLiveData<WeatherResponse?> = _weatherData
    val activeDay = mutableIntStateOf(0)

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

    @Composable
    fun weatherCodeToImage(weatherCode: Int = -1, isDay: Int): Painter {
        return when (weatherCode) {
            0 -> painterResource(id = if (isDay == 1) R.drawable.clear_day else R.drawable.clear_night)
            in 1..2 -> painterResource(id = if (isDay == 1) R.drawable.mainly_clear_day else R.drawable.mainly_clear_night)
            3 -> painterResource(id = R.drawable.overcast)
            in listOf(45, 48) -> painterResource(id = R.drawable.fog)
            in listOf(51, 53, 55, 56, 57) -> painterResource(id = R.drawable.drizzle)
            in listOf(61, 63, 65, 66, 67, 77, 80, 81, 82) -> painterResource(id = R.drawable.rain)
            in listOf(71, 73, 75, 85, 86) -> painterResource(id = R.drawable.snow_fall)
            in listOf(95, 96, 99) -> painterResource(id = R.drawable.thunderstorm)
            else -> painterResource(id = R.drawable.unknown_weather)
        }
    }

    fun generateDailyWeatherData(weatherResponse: WeatherResponse): List<DailyWeatherData> {
        val weatherDataList = mutableListOf<DailyWeatherData>()
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
        val currentDate = Date()
        for (dayIndex in weatherResponse.daily.time.indices) {
            val date = dateFormatter.parse(weatherResponse.daily.time[dayIndex])
            if (date != null) {
                val day = if (date <= currentDate) {
                    "Today"
                } else {
                    SimpleDateFormat("EEE dd.MM.", Locale.getDefault()).format(date)
                }
                val hourlyDataList = mutableListOf<DailyHourlyData>()
                for (hourIndex in weatherResponse.hourly.time.indices) {
                    val hourlyDate = dateTimeFormatter.parse(weatherResponse.hourly.time[hourIndex])
                    if (hourlyDate != null && dateFormatter.format(hourlyDate) == dateFormatter.format(
                            date
                        )
                    ) {
                        if (hourlyDate.before(currentDate)) {
                            continue
                        }
                        val hour = SimpleDateFormat("HH:mm", Locale.getDefault()).format(hourlyDate)
                        val hourlyData = DailyHourlyData(
                            hour = hour,
                            temperature = weatherResponse.hourly.temperature[hourIndex].toInt(),
                            weatherCode = weatherResponse.hourly.weatherCode[hourIndex],
                            windDirection = weatherResponse.hourly.windDirection[hourIndex].toInt(),
                            windSpeedMs = weatherResponse.hourly.windSpeed[hourIndex].toInt(),
                            isDay = weatherResponse.hourly.isDay[hourIndex]
                        )
                        hourlyDataList.add(hourlyData)
                    }
                }
                val weatherData = DailyWeatherData(
                    day = day,
                    minTemperature = weatherResponse.daily.minTemperature[dayIndex].toInt(),
                    maxTemperature = weatherResponse.daily.maxTemperature[dayIndex].toInt(),
                    weatherCode = weatherResponse.daily.weatherCode[dayIndex],
                    hourlyData = hourlyDataList
                )
                weatherDataList.add(weatherData)
            }
        }
        return weatherDataList
    }
}