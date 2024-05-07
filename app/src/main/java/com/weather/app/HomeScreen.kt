package com.weather.app

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.weather.app.weather.WeatherResponse
import android.Manifest
import android.location.Geocoder
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun HomeScreen() {
    val weatherViewModel: WeatherViewModel = viewModel()
    val weatherData by weatherViewModel.weatherData.observeAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val allPermissionsGranted = permissions.entries.all { it.value }
            if (allPermissionsGranted) {
                weatherViewModel.startLocationUpdates()
            }
        }
    )

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
    if (weatherData == null) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Fetching weather data...", modifier = Modifier.padding(bottom = 20.dp))
            CircularProgressIndicator(modifier = Modifier.size(50.dp))
        }
    } else {
        CurrentWeather(weatherData!!)
    }
}

@Composable
fun CurrentWeather(weather: WeatherResponse) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CurrentCity(latitude = weather.latitude, longitude = weather.longitude)
        Text(
            text = "${(weather.current.temperature).roundToInt()}°",
            fontSize = 70.sp, modifier = Modifier.padding(10.dp)
        )
    }
}

@Suppress("DEPRECATION")
@Composable
fun CurrentCity(latitude: Double, longitude: Double) {
    val geocoder = Geocoder(LocalContext.current, Locale.getDefault())
    val address = geocoder.getFromLocation(latitude, longitude, 1)
    val city = address?.first()?.locality?.toString() ?: "Unknown city"
    Text(
        text = city, fontSize = 30.sp, modifier =
        Modifier.padding(top = 70.dp)
    )
}