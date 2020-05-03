package com.zakhar.watersensorapp

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import kotlinx.coroutines.yield
import java.io.IOException

class CurrentBluetoothDevice {
    companion object {
        var device: BluetoothDevice? = null
        private var socket : BluetoothSocket? = null
        private val TAG: String = "BluetoothCommand"

        fun setSocket(socket: BluetoothSocket?) {
            if (this.socket != null) {
                this.socket?.close()
            }
            this.socket = socket
        }

        fun getSocket(): BluetoothSocket? {
            return this.socket
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