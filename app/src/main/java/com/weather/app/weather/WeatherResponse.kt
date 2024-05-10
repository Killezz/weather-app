package com.weather.app.weather

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val current: Current,
    val daily: Daily,
    val hourly: Hourly,
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
)

data class Current(
    @SerializedName("temperature_2m") val temperature: Double,
    val time: String,
    @SerializedName("weather_code") val weatherCode: Int,
    @SerializedName("is_day") val isDay: Int,
)


data class Hourly(
    @SerializedName("temperature_2m") val temperature: List<Double>,
    val time: List<String>,
    @SerializedName("wind_speed_10m") val windSpeed: List<Double>,
    @SerializedName("wind_direction_10m") val windDirection: List<Double>,
    @SerializedName("weather_code") val weatherCode: List<Int>,
    @SerializedName("is_day") val isDay: List<Int>,
)

data class Daily(
    @SerializedName("temperature_2m_max") val maxTemperature: List<Double>,
    @SerializedName("temperature_2m_min") val minTemperature: List<Double>,
    val time: List<String>,
    @SerializedName("weather_code") val weatherCode: List<Int>,
)

data class DailyWeatherData(
    val day: String,
    val minTemperature: Int,
    val maxTemperature: Int,
    val weatherCode: Int,
    val hourlyData: List<DailyHourlyData>,
)

data class DailyHourlyData(
    val hour: String,
    val temperature: Int,
    val weatherCode: Int,
    val windDirection: Int,
    val windSpeedMs: Int,
    val isDay: Int,
)