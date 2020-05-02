package com.zakhar.watersensorapp.bluetoothCommands

import android.bluetooth.BluetoothSocket
import android.icu.text.UnicodeSet
import android.util.Log
import kotlinx.coroutines.yield
import java.io.IOException
import java.nio.charset.Charset

abstract class BluetoothCommand(bluetoothSocket: BluetoothSocket) {
    private val socket: BluetoothSocket = bluetoothSocket
    private val TAG: String = "BluetoothCommand"

    protected fun sendCommand(input: String) {
        if (!socket.isConnected) {
            Log.e(TAG, "Socket is null. Cannot send the command.")
            return
        }
        try {
            socket.outputStream.write(input.toByteArray())
            socket.outputStream.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    protected suspend fun getAnswer(): String {
        try {
            while (socket.inputStream.available() == 0 && socket.isConnected) {
//                Log.i(TAG, "Stream is empty")
                yield()
            }
            val bytes = ByteArray(socket.inputStream.available())
            socket.inputStream.read(bytes)
            val line = String(bytes).trim()
            return line
        } catch (e: IOException) {
            e.printStackTrace()
            return ""
        }
    }

    protected suspend fun sendCommandWithFeedback(command: String): String {
        sendCommand(command)
        return getAnswer()
    }
}