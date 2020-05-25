package com.zakhar.watersensorapp.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import com.zakhar.watersensorapp.models.SensorDevice
import kotlinx.coroutines.yield
import java.io.IOException
import java.util.*

fun BluetoothSocket.sendCommand(input: String) {
    if (!isConnected) {
        Log.e("BluetoothSocket", "Socket is null or not connected. Cannot send the command.")
        return
    }
    outputStream.write(input.toByteArray())
    outputStream.flush()
}

suspend fun BluetoothSocket.getAnswer(): String? {
    if (!isConnected) {
        Log.e("BluetoothSocket", "Socket is null or not connected. Cannot send the command.")
        return null
    }

    return try {
        while (inputStream.available() == 0 && isConnected) {
            yield()
        }
        val bytes = ByteArray(inputStream.available())
        inputStream.read(bytes)
        val line = String(bytes).trim()
        line
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

suspend fun BluetoothSocket.sendCommandWithFeedback(command: String): String? {
    sendCommand(command)
    return getAnswer()
}