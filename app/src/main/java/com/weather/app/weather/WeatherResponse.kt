package com.weather.app.weather

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val current: Current,
    @SerializedName("current_units") val currentUnits: CurrentUnits,
    val elevation: Int,
    @SerializedName("generationtime_ms") val generationTimeMs: Double,
    val hourly: Hourly,
    @SerializedName("hourly_units") val hourlyUnits: HourlyUnits,
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    @SerializedName("timezone_abbreviation") val timezoneAbbreviation: String,
    @SerializedName("utc_offset_seconds") val utcOffsetSeconds: Int,
)

data class Current(
    val interval: Int,
    @SerializedName("relative_humidity_2m") val relativeHumidity: Int,
    @SerializedName("temperature_2m") val temperature: Double,
    val time: String,
    @SerializedName("weather_code") val weatherCode: Int,
)

data class HourlyUnits(
    @SerializedName("relative_humidity_2m") val relativeHumidity: String,
    @SerializedName("temperature_2m") val temperature: String,
    val time: String,
    @SerializedName("weather_code") val weatherCode: String,
)

data class Hourly(
    @SerializedName("relative_humidity_2m") val relativeHumidity: List<Int>,
    @SerializedName("temperature_2m") val temperature: List<Double>,
    val time: List<String>,
    @SerializedName("weather_code") val weatherCode: List<Int>,
)

data class CurrentUnits(
    val interval: String,
    @SerializedName("relative_humidity_2m") val relativeHumidity: String,
    @SerializedName("temperature_2m") val temperature: String,
    val time: String,
    @SerializedName("weather_code") val weatherCode: String,
)
