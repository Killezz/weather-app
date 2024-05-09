package com.weather.app.widget

import android.location.Geocoder
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weather.app.weather.DailyWeatherData
import com.weather.app.weather.WeatherResponse
import com.weather.app.weather.WeatherViewModel
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun DailyWeather(viewModel: WeatherViewModel, weatherData: WeatherResponse) {
    Column {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(Color(252, 224, 190, 255))
                .padding(all = 20.dp),
        ) {
            CurrentCity(latitude = weatherData.latitude, longitude = weatherData.longitude)
            Text(
                text = viewModel.weatherCodeToEmoji(weatherData.current.weatherCode),
                fontSize = 120.sp,
                modifier = Modifier.padding(top = 20.dp)
            )
            Row(
                modifier = Modifier
                    .padding(top = 30.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${(weatherData.current.temperature).roundToInt()}°",
                    fontSize = 90.sp, fontWeight = FontWeight(500)
                )
                Spacer(modifier = Modifier.weight(1f))
                Column {
                    Text(
                        text = "↑ ${(weatherData.daily.maxTemperature.first()).roundToInt()}°",
                        fontSize = 30.sp, color = Color(255, 0, 0, 255)
                    )
                    Text(
                        text = "↓ ${(weatherData.daily.minTemperature.first()).roundToInt()}°",
                        fontSize = 30.sp, color = Color(47, 116, 255, 255)
                    )
                }
            }
        }
        DailyWeatherRow(viewModel, weatherData)
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

@Composable
fun DailyWeatherRow(viewModel: WeatherViewModel, data: WeatherResponse) {
    val weatherDataList by remember { mutableStateOf(viewModel.generateDailyWeatherData(data)) }
    LazyRow(
        modifier = Modifier
            .padding(top = 5.dp),
        contentPadding = PaddingValues(horizontal = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(weatherDataList.size) { index ->
            val weatherData = weatherDataList[index]
            WeatherBox(weatherData, viewModel.weatherCodeToEmoji(weatherData.weatherCode))
        }
    }
}

@Composable
fun WeatherBox(weatherData: DailyWeatherData, weatherEmoji: String) {
    Box(
        modifier = Modifier
            .width(150.dp)
            .border(2.dp, Color.Gray, RoundedCornerShape(10)),

        ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = weatherData.day,
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier
                    .padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = weatherEmoji, fontSize = 50.sp)
                Spacer(modifier = Modifier.weight(1f))
                Column {
                    Text(
                        text = "↑ ${weatherData.maxTemperature}°",
                        fontSize = 25.sp, color = Color(255, 0, 0, 255)
                    )
                    Text(
                        text = "↓ ${weatherData.minTemperature}°",
                        fontSize = 25.sp, color = Color(47, 116, 255, 255)
                    )
                }
            }
        }
    }
}