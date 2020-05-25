package com.zakhar.watersensorapp.activities.devicesList

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.zakhar.watersensorapp.R
import com.zakhar.watersensorapp.activities.device.DeviceActivity
import com.zakhar.watersensorapp.models.SensorDevice
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.*
import java.lang.Exception
import java.util.*
import kotlin.coroutines.CoroutineContext

class DevicesListActivity : AppCompatActivity(), CoroutineScope {
    private val TAG = "MainActivity"

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private var job: Job = Job()

    private var appUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private var bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private lateinit var devicesArrayAdapter : BluetoothDeviceArrayAdapter

    private val REQUEST_ENABLE_BLUETOOTH = 1
    private val REQUEST_GRANT_PERMISSIONS = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //todo: add binding

        if (!bluetoothAdapter.isEnabled) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
        } else {
            getDevicesToAdapter()
        }

        content_main_list_view_device_list.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val currentDevice = devicesArrayAdapter.getItem(position)  ?: return@OnItemClickListener

            val device = SensorDevice.createSensorDevice(currentDevice)

            launch {
                try {
                    device.tryToConnect(
                        onSuccess = {
                            val intent = Intent(this@DevicesListActivity, DeviceActivity::class.java)
                            startActivity(intent)
                        },
                        onFailure = {
                            Log.i(TAG, "Socket is not connected.")
                        }
                    )
                } catch (e: Exception) {
                    Log.e(TAG, e.message)
                }
            }
        }
    }

    private fun getDevicesToAdapter() {
        val devices = bluetoothAdapter.bondedDevices.toTypedArray()
        devicesArrayAdapter = BluetoothDeviceArrayAdapter(this, devices)
        content_main_list_view_device_list.adapter = devicesArrayAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                if (bluetoothAdapter.isEnabled) {
                    Log.i(TAG,"bt has been enabled")
                    getDevicesToAdapter()
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

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}
