package com.weather.app

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.weather.app.ui.theme.WeatherAppTheme
import com.weather.app.weather.WeatherViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: WeatherViewModel by viewModels()
        val context = this

        setContent {
            var theme by remember { mutableStateOf(SharedPreferencesUtils.getTheme(context)) }

            val listener =
                SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                    if (key == "theme") {
                        val newTheme = SharedPreferencesUtils.getTheme(context)
                        theme = newTheme
                    }
                }

            DisposableEffect(Unit) {
                SharedPreferencesUtils.registerOnSharedPreferenceChangeListener(context, listener)
                onDispose {
                    SharedPreferencesUtils.unregisterOnSharedPreferenceChangeListener(
                        context,
                        listener
                    )
                }
            }

            val darkTheme = when (theme) {
                "dark" -> true
                "light" -> false
                else -> null
            }

            WeatherAppTheme(
                darkTheme = darkTheme
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    HomeScreen(viewModel)
                }
            }
        }
    }
}