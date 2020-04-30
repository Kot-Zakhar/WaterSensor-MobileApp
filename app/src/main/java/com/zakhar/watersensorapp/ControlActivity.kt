package com.zakhar.watersensorapp

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_control.*
import kotlinx.coroutines.*
import java.io.IOException

class ControlActivity: AppCompatActivity() {
    private val TAG = "ControlActivity"
    companion object {
        lateinit var adapter: BluetoothAdapter
        var deviceName: String? = null
        var deviceMac: String? = null
        var socket: BluetoothSocket? = null
    }

    fun bindControls() {
        control_ping.setOnClickListener {
            GlobalScope.launch {
                sendCommand("ping\n")
                val answer = getAnswer()
                if (answer == "pong") {
                    Log.i(TAG, "got response")
                } else {
                    Log.i(TAG, "Wrong response $answer")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)

        deviceName = intent.getStringExtra(MainActivity.EXTRA_DEVICE_NAME)
        deviceMac = intent.getStringExtra(MainActivity.EXTRA_DEVICE_MAC)

        socket = SocketHandler.getSocket()

        if (socket == null) {
            Log.i("ControlActivity", "Socket is null.")
            finish()
        }

        bindControls()
    }

    override fun onDestroy() {
        socket?.close()
        super.onDestroy()
    }

    private fun sendCommand(input: String) {
        try {
            socket!!.outputStream.write(input.toByteArray())
            socket!!.outputStream.flush()
        } catch (e: IOException ) {
            e.printStackTrace()
        }
    }

    private fun getAnswer(): String? {
        try {
            while (socket!!.inputStream.available() == 0 && socket!!.isConnected()) {
                Log.i(TAG, "Stream is empty")
            }
            val line = String(ByteArray(socket!!.inputStream.available()))
            return line
        } catch (e: IOException ) {
            e.printStackTrace()
            return null
        }
    }
}