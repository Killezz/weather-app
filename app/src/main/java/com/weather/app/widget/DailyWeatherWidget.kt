package com.weather.app.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.weather.app.SharedPreferencesUtils
import com.weather.app.weather.DailyHourlyData
import com.weather.app.weather.DailyWeatherData
import com.weather.app.weather.WeatherResponse
import com.weather.app.weather.WeatherViewModel
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
            viewModel.CurrentCity(
                latitude = weatherData.latitude,
                longitude = weatherData.longitude
            )
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
        DailyWeatherRow(weatherData, viewModel)
    }
    SettingsPopup(viewModel)
}

@Composable
fun DailyWeatherRow(data: WeatherResponse, viewModel: WeatherViewModel) {
    val weatherDataList = remember(data) { viewModel.generateDailyWeatherData(data) }
    Column {
        LazyRow(
            modifier = Modifier
                .padding(top = 5.dp),
            contentPadding = PaddingValues(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(weatherDataList) { index, weatherData ->
                WeatherCard(
                    weatherData,
                    viewModel,
                    index
                )
            }
        }
        LazyRow(
            modifier = Modifier
                .padding(top = 10.dp, bottom = 10.dp),
            contentPadding = PaddingValues(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            items(weatherDataList[viewModel.activeDay.intValue].hourlyData) { hourlyData ->
                HourlyWeatherCard(hourlyData, viewModel)
            }
        }
    }
}

@Composable
fun WeatherCard(weatherData: DailyWeatherData, viewModel: WeatherViewModel, id: Int) {
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
fun HourlyWeatherCard(hourlyData: DailyHourlyData, viewModel: WeatherViewModel) {
    Card(
        modifier = Modifier
            .border(1.dp, Color.Gray, RoundedCornerShape(10)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
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

@Composable
fun SettingsPopup(viewModel: WeatherViewModel) {
    var showModal by remember { mutableStateOf(false) }
    val selectedTemperatureUnit = remember { mutableStateOf(viewModel.temperatureUnit.value) }
    val context = LocalContext.current
    val selectedTheme = remember { mutableStateOf(SharedPreferencesUtils.getTheme(context)) }
    val temperatureExpandedState = remember { mutableStateOf(false) }
    val themeExpandedState = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 5.dp, end = 10.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Image(
            painter = painterResource(id = R.drawable.settings),
            contentDescription = "Settings icon",
            modifier = Modifier
                .padding(5.dp)
                .height(30.dp)
                .clickable { showModal = true }
        )

        if (showModal) {
            AlertDialog(
                title = {
                    Text(text = "Settings")
                },
                text = {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(5.dp)
                        ) {
                            Text(text = "Temperature unit: ")
                            Column {
                                Box(
                                    modifier = Modifier
                                        .clickable { temperatureExpandedState.value = true }
                                        .border(
                                            1.dp,
                                            Color.Gray,
                                            shape = RoundedCornerShape(5.dp)
                                        )
                                        .padding(5.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row {
                                        Text(text = selectedTemperatureUnit.value)
                                        Icon(
                                            imageVector = Icons.Outlined.KeyboardArrowDown,
                                            contentDescription = "Dropdown Icon"
                                        )
                                    }
                                }
                                DropdownMenu(
                                    expanded = temperatureExpandedState.value,
                                    onDismissRequest = { temperatureExpandedState.value = false },
                                ) {
                                    DropdownMenuItem(text = {
                                        Text("celsius")
                                    }, onClick = {
                                        selectedTemperatureUnit.value = "celsius"
                                        temperatureExpandedState.value = false
                                    })
                                    DropdownMenuItem(text = {
                                        Text("fahrenheit")
                                    }, onClick = {
                                        selectedTemperatureUnit.value = "fahrenheit"
                                        temperatureExpandedState.value = false
                                    })
                                }
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(5.dp)
                        ) {
                            Text(text = "Theme: ")
                            Column {
                                Box(
                                    modifier = Modifier
                                        .clickable { themeExpandedState.value = true }
                                        .border(
                                            1.dp,
                                            Color.Gray,
                                            shape = RoundedCornerShape(5.dp)
                                        )
                                        .padding(5.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row {
                                        Text(text = selectedTheme.value)
                                        Icon(
                                            imageVector = Icons.Outlined.KeyboardArrowDown,
                                            contentDescription = "Dropdown Icon"
                                        )
                                    }
                                }
                                DropdownMenu(
                                    expanded = themeExpandedState.value,
                                    onDismissRequest = { themeExpandedState.value = false },
                                ) {
                                    DropdownMenuItem(text = {
                                        Text("auto")
                                    }, onClick = {
                                        selectedTheme.value = "auto"
                                        themeExpandedState.value = false
                                    })
                                    DropdownMenuItem(text = {
                                        Text("dark")
                                    }, onClick = {
                                        selectedTheme.value = "dark"
                                        themeExpandedState.value = false
                                    })
                                    DropdownMenuItem(text = {
                                        Text("light")
                                    }, onClick = {
                                        selectedTheme.value = "light"
                                        themeExpandedState.value = false
                                    })
                                }
                            }
                        }
                    }
                },
                onDismissRequest = {
                    showModal = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.saveSettings(
                                selectedTemperatureUnit.value,
                                selectedTheme.value
                            )
                            showModal = false
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showModal = false
                        }
                    ) {
                        Text("Close")
                    }
                }
            )
        }
    }
}
