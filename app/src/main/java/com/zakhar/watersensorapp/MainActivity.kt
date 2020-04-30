package com.zakhar.watersensorapp

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.RelativeLayout.CENTER_IN_PARENT
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.Socket
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private var appUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var devicesArrayAdapter : BluetoothDeviceArrayAdapter

    private val REQUEST_ENABLE_BLUETOOTH = 1
    private val REQUEST_GRANT_PERMISSIONS = 2

    private val devicesReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    devicesArrayAdapter.add(device)
                    val deviceHardwareAddress = device?.address
                    Log.i(TAG , "BT device discovery: " + "New device found: " + device?.name + " - " + deviceHardwareAddress)
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    content_main_button_refresh.isEnabled = false
                    Log.i(TAG, "BT device discovery is started")
                    devicesArrayAdapter.clear()
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    content_main_button_refresh.isEnabled = true
                    Log.i(TAG, "BT device discovery is finished")
                }
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    Log.i(TAG, "I don't fucking know what is going on....")
                }
                else -> {
                    Log.i(TAG, "Some unknown action:" + intent?.action)
                }
            }
        }
    }

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
//            bluetoothAdapter.enable()
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
        }

        content_main_button_refresh.setOnClickListener{
            if (!bluetoothAdapter.startDiscovery()) {
                Log.i(TAG, "Cannot start discovery")
            }
        }


        devicesArrayAdapter = BluetoothDeviceArrayAdapter(this, ArrayList<BluetoothDevice>())

        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_GRANT_PERMISSIONS)
        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.BLUETOOTH), REQUEST_GRANT_PERMISSIONS)
        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.BLUETOOTH_ADMIN), REQUEST_GRANT_PERMISSIONS)

        registerReceiver(devicesReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
        registerReceiver(devicesReceiver, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED))
        registerReceiver(devicesReceiver, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))

        content_main_list_view_device_list.adapter = devicesArrayAdapter
        content_main_list_view_device_list.onItemClickListener = AdapterView.OnItemClickListener{ _, _, position, _ ->

            bluetoothAdapter.cancelDiscovery()

            val device: BluetoothDevice = devicesArrayAdapter.getItem(position) ?: return@OnItemClickListener
            val socket = device.createRfcommSocketToServiceRecord(appUUID)

            GlobalScope.launch {
                try {
                    socket.connect()
                    if (socket.isConnected) {
                        SocketHandler.setSocket(socket)
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

        if (!bluetoothAdapter.startDiscovery()) {
            Log.i(TAG, "Cannot start first discovery.")
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(devicesReceiver)
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
