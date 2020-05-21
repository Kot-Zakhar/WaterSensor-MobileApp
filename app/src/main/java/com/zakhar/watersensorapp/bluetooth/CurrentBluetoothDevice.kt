package com.zakhar.watersensorapp.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import kotlinx.coroutines.yield
import java.io.IOException
import java.util.*

class CurrentBluetoothDevice {
    companion object {
        private val TAG: String = "CurrentBluetoothDevice"
        private var appUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

        var device: BluetoothDevice? = null
        var isConnected: Boolean = false
            get() = socket != null && socket!!.isConnected

        var socket : BluetoothSocket? = null

        fun createSocketAndConnect() {
            if (device == null)
                return
            socket = device!!.createRfcommSocketToServiceRecord(appUUID)
            socket!!.connect()
        }

        fun sendCommand(input: String) {
            if (socket?.isConnected == false) {
                Log.e(TAG, "Socket is null or not connected. Cannot send the command.")
                throw Error("Socket is null or not connected. Cannot send the command.")
            }
            try {
                socket!!.outputStream.write(input.toByteArray())
                socket!!.outputStream.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        suspend fun getAnswer(): String {
            if (socket?.isConnected == false) {
                Log.e(TAG, "Socket is null or not connected. Cannot send the command.")
                throw Error("Socket is null or not connected. Cannot send the command.")
            }
            try {
                while (socket!!.inputStream.available() == 0 && socket!!.isConnected) {
    //                Log.i(TAG, "Stream is empty")
                    yield()
                }
                val bytes = ByteArray(socket!!.inputStream.available())
                socket!!.inputStream.read(bytes)
                val line = String(bytes).trim()
                return line
            } catch (e: IOException) {
                e.printStackTrace()
                return ""
            }
        }

        suspend fun sendCommandWithFeedback(command: String): String {
            sendCommand(command)
            return getAnswer()
        }
    }
}