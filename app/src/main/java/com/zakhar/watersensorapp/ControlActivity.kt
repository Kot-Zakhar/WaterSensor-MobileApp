package com.zakhar.watersensorapp

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_control.*
//import kotlinx.coroutines.*
import java.io.IOException
import java.lang.Exception
import java.util.*

class ControlActivity: AppCompatActivity() {

    companion object {
        var myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var bluetoothSocket: BluetoothSocket? = null
        lateinit var progress: ProgressDialog
        lateinit var bluetoothAdapter: BluetoothAdapter
        var isConnected: Boolean = false
        lateinit var address: String

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)

        address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS)


            try {
                connectToDevice(this).execute()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.i("Connection to bt failed", e.message)
                disconnectAndExit()
            }

        control_led_on.setOnClickListener{ sendCommand("on") }
        control_led_off.setOnClickListener{ sendCommand("off") }
        control_led_disconnect.setOnClickListener{ disconnectAndExit() }
    }

    private fun sendCommand(input: String) {
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket!!.outputStream.write(input.toByteArray())
            } catch (e: IOException ) {
                e.printStackTrace()
            }
        }
    }

    private fun disconnectAndExit() {
        Log.i("controlActivity", "Disconnecting")
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket!!.close()
                bluetoothSocket = null
                isConnected = false
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        finish()
    }

    private class connectToDevice(c: Context) : AsyncTask<Void, Void, String>() {
        private var connectSuccess: Boolean = true
        private val context: Context = c

        override fun onPreExecute() {
            super.onPreExecute()

            progress = ProgressDialog.show(context, "Connecting..", "please wait")
        }

        override fun doInBackground(vararg params: Void?): String? {
            try {
                if (bluetoothSocket == null || !isConnected) {
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(address)

                    bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID)
                    bluetoothSocket!!.connect()
                    bluetoothAdapter.cancelDiscovery()
                }
            } catch (e: IOException) {
                connectSuccess = false
                e.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            if (!connectSuccess) {
                Log.i("data", "Couldn't connect")

            } else {
                isConnected = true
            }

            progress.dismiss()
        }

    }
}