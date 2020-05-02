package com.zakhar.watersensorapp

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.zakhar.watersensorapp.CurrentBluetoothDevice
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private var appUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var devicesArrayAdapter : BluetoothDeviceArrayAdapter

    private val REQUEST_ENABLE_BLUETOOTH = 1
    private val REQUEST_GRANT_PERMISSIONS = 2

    companion object {
        val EXTRA_DEVICE_NAME: String = "BT_device_name"
        val EXTRA_DEVICE_MAC: String = "BT_device_mac"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(activity_main__toolbar)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (!bluetoothAdapter.isEnabled) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
        }

        val devices = bluetoothAdapter.bondedDevices.toTypedArray()

        devicesArrayAdapter = BluetoothDeviceArrayAdapter(this, devices)


        content_main_list_view_device_list.adapter = devicesArrayAdapter
        content_main_list_view_device_list.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val device: BluetoothDevice = devicesArrayAdapter.getItem(position) ?: return@OnItemClickListener
            val socket = device.createRfcommSocketToServiceRecord(appUUID)

            GlobalScope.launch {
                try {
                    socket.connect()
                    if (socket.isConnected) {
                        CurrentBluetoothDevice.setSocket(socket)
                        val intent = Intent(this@MainActivity, ControlActivity::class.java)
                        intent.putExtra(EXTRA_DEVICE_NAME, device.name)
                        intent.putExtra(EXTRA_DEVICE_MAC, device.address)
                        startActivity(intent)
                    } else {
                        Log.i(TAG, "Socket is not connected.")
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "Can't connect to " + device.name + ": " + e.message)
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                if (bluetoothAdapter.isEnabled) {
                    Log.i(TAG,"bt has been enabled")
                } else {
                    Log.i(TAG, "bt has been disabled")
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i(TAG, "bt enabling has been canceled")
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            REQUEST_GRANT_PERMISSIONS -> {
                permissions.forEachIndexed { i, perm ->
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        Log.i("Permission request", perm + " is not granted.")
                    } else {
                        Log.i("Permission request", perm + " is granted.")
                    }
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }
}
