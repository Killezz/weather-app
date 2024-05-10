package com.weather.app.widget

import android.location.Geocoder
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.weather.app.R
import com.weather.app.weather.DailyHourlyData
import com.weather.app.weather.DailyWeatherData
import com.weather.app.weather.WeatherResponse
import com.weather.app.weather.WeatherViewModel
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun DailyWeather(weatherData: WeatherResponse) {
    val viewModel: WeatherViewModel = viewModel()
    Column {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(Color(230, 242, 252, 255))
                .padding(all = 20.dp),
        ) {
            CurrentCity(latitude = weatherData.latitude, longitude = weatherData.longitude)
            Image(
                painter = viewModel.weatherCodeToImage(
                    weatherData.current.weatherCode,
                    weatherData.current.isDay
                ),
                contentDescription = "Weather icon",
                modifier = Modifier
                    .padding(top = 15.dp)
                    .height(150.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${(weatherData.current.temperature).roundToInt()}°",
                    fontSize = 90.sp, fontWeight = FontWeight.Bold
                )
            }
        }
        DailyWeatherRow(weatherData)
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
fun DailyWeatherRow(data: WeatherResponse) {
    val viewModel: WeatherViewModel = viewModel()
    val weatherDataList by remember { mutableStateOf(viewModel.generateDailyWeatherData(data)) }
    Column {
        LazyRow(
            modifier = Modifier
                .padding(top = 5.dp),
            contentPadding = PaddingValues(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(weatherDataList.size) { index ->
                val weatherData = weatherDataList[index]
                WeatherCard(
                    weatherData,
                    index
                )
            }
        }
        LazyRow(
            modifier = Modifier
                .padding(top = 10.dp, bottom = 10.dp),
            contentPadding = PaddingValues(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(weatherDataList[viewModel.activeDay.intValue].hourlyData.size) { index ->
                val hourlyData = weatherDataList[viewModel.activeDay.intValue].hourlyData[index]
                HourlyWeatherCard(
                    hourlyData
                )
            }
        }
    }
}

@Composable
fun WeatherCard(weatherData: DailyWeatherData, id: Int) {
    val viewModel: WeatherViewModel = viewModel()
    Card(
        modifier = Modifier
            .border(1.dp, Color.Gray, RoundedCornerShape(10))
            .clickable { viewModel.activeDay.intValue = id },
        colors = CardDefaults.cardColors(
            containerColor = if (id == viewModel.activeDay.intValue) Color(
                230,
                242,
                252,
                255
            ) else Color.White
        ),
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = weatherData.day,
                color = Color.Black,
                fontSize = 18.sp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Image(
                    painter = viewModel.weatherCodeToImage(weatherData.weatherCode, 1),
                    contentDescription = "Weather icon",
                    modifier = Modifier
                        .height(50.dp)
                        .padding(end = 10.dp)
                )
                Text(
                    text = "${weatherData.minTemperature}",
                    fontSize = 25.sp, color = Color(47, 116, 255, 255)
                )
                Text(text = "...", fontSize = 25.sp)
                Text(
                    text = "${weatherData.maxTemperature}°",
                    fontSize = 25.sp, color = Color(255, 0, 0, 255)
                )
            }
        }
    }
}


@Composable
fun HourlyWeatherCard(hourlyData: DailyHourlyData) {
    val viewModel: WeatherViewModel = viewModel()

    Card(
        modifier = Modifier
            .border(1.dp, Color.Gray, RoundedCornerShape(10)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = hourlyData.hour,
                color = Color.Black,
                fontSize = 18.sp
            )
            Image(
                painter = viewModel.weatherCodeToImage(
                    hourlyData.weatherCode,
                    hourlyData.isDay
                ),
                contentDescription = "Weather icon",
                modifier = Modifier
                    .height(50.dp)
                    .padding(5.dp)
            )
            Text(
                text = "${hourlyData.temperature}°",
                fontSize = 30.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 5.dp, bottom = 10.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.compass),
                contentDescription = "Compass icon",
                modifier = Modifier
                    .height(25.dp)
                    .rotate(hourlyData.windDirection.toFloat())
            )
            Text(
                text = "${hourlyData.windSpeedMs} m/s",
                fontSize = 20.sp
            )
        }
    }
}
