package com.weather.app.weather

import android.app.Application
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.weather.app.LocationRepository
import com.weather.app.R
import com.weather.app.SharedPreferencesUtils
import com.weather.app.SnackbarManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService = WeatherApiService.create()
    private val _weatherData = MutableLiveData<WeatherResponse?>()
    private val locationRepository = LocationRepository(application)
    private val _location = mutableStateOf<Location?>(null)
    private val _temperatureUnit =
        mutableStateOf(SharedPreferencesUtils.getTemperatureUnit(application))
    val snackbarManager = SnackbarManager()
    val weatherData: MutableLiveData<WeatherResponse?> = _weatherData
    val activeDay = mutableIntStateOf(0)
    val temperatureUnit: State<String>
        get() = _temperatureUnit

    private fun fetchWeatherData(latitude: Double, longitude: Double, temperatureUnit: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getWeatherData(
                    latitude,
                    longitude,
                    temperatureUnit
                )
                _weatherData.value = response
            } catch (e: Exception) {
                Log.d("WeatherViewModel", "Error: $e")
                viewModelScope.launch {
                    snackbarManager.showSnackbar("${e.message}\n\nTrying again in 1 minute.")
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    @Composable
    fun CurrentCity(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(LocalContext.current, Locale.getDefault())
        val address = geocoder.getFromLocation(latitude, longitude, 1)
        val city = address?.first()?.locality?.toString() ?: "Unknown city"
        Text(
            text = city, fontSize = 30.sp
        )
    }

    fun startLocationUpdates() {
        locationRepository.startLocationUpdates { location ->
            _location.value = location
            if (location != null) {
                fetchWeatherData(location.latitude, location.longitude, _temperatureUnit.value)
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

    fun saveSettings(temperatureUnit: String, theme: String) {
        val currentSelectedTheme = SharedPreferencesUtils.getTheme(getApplication())
        if (currentSelectedTheme != theme) {
            SharedPreferencesUtils.saveTheme(getApplication(), theme)
        }
        if (temperatureUnit != _temperatureUnit.value) {
            _temperatureUnit.value = temperatureUnit
            SharedPreferencesUtils.saveTemperatureUnit(getApplication(), temperatureUnit)
            fetchWeatherData(
                _location.value!!.latitude,
                _location.value!!.longitude,
                _temperatureUnit.value
            )
        }
    }
}