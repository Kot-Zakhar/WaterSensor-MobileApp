package com.zakhar.watersensorapp.wifiActivity

import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.zakhar.watersensorapp.CurrentBluetoothDevice
import com.zakhar.watersensorapp.R
import kotlinx.android.synthetic.main.activity_wifi.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext

class WifiActivity: AppCompatActivity(), CoroutineScope {
    private val TAG = "WifiActivity"

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var job = Job()
    private var socket: BluetoothSocket? = null
    private lateinit var wifiArrayAdapter: WifiArrayAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi)

        socket = CurrentBluetoothDevice.getSocket()

        if (socket == null) {
            Log.i(TAG, "Socket is null")
            finish()
        }

        wifiArrayAdapter = WifiArrayAdapter(this)

        activity_wifi__wifi_records__list_view.adapter = wifiArrayAdapter

        launch {
            val serializedWifiRecords = CurrentBluetoothDevice.sendCommandWithFeedback("json:show")
            Log.d(TAG, serializedWifiRecords)
            val records = Json.parse(WifiRecord.serializer().list, serializedWifiRecords)
            wifiArrayAdapter.addAll(records)
        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}