package com.weather.app

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class SnackbarManager {
    private val snackbarChannel = Channel<String>()
    val snackbarFlow = snackbarChannel.receiveAsFlow()

    fun showSnackbar(message: String) {
        snackbarChannel.trySend(message)
    }
}