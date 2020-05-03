package com.zakhar.watersensorapp.wifiActivity

import android.bluetooth.BluetoothSocket
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.transition.Visibility
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.zakhar.watersensorapp.CurrentBluetoothDevice
import com.zakhar.watersensorapp.R
import kotlinx.android.synthetic.main.activity_wifi.*
import kotlinx.coroutines.*
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

    fun toggleNewWifiForm(view: View) {
        if (activity_wifi__new_record__layout.visibility == View.VISIBLE) {
            activity_wifi__new_record__layout.visibility = View.GONE
        } else {
            activity_wifi__new_record__layout.visibility = View.VISIBLE
        }
    }

    fun createNewWifiRecord(view: View) {
        val ssid = activity_wifi__new_record__ssid__edit_text.text.toString().trim()
        val password = activity_wifi__new_record__password__edit_text.text.toString().trim()
        if (ssid.isEmpty())
            return

        val newWifiRecord = WifiRecord(ssid, password)
        val serializedRecord = Json.stringify(WifiRecord.serializer(), newWifiRecord);
        Log.d(TAG, serializedRecord)

        launch {
            CurrentBluetoothDevice.sendCommand("json:new wifi")
            delay(1000)
            val result = CurrentBluetoothDevice.sendCommandWithFeedback(serializedRecord)
            if (result.trim().toLowerCase() == "ok") {
                activity_wifi__new_record__ssid__edit_text.text.clear()
                activity_wifi__new_record__password__edit_text.text.clear()
                toggleNewWifiForm(activity_wifi__new_record__confirm__image_button)
                val adapter = activity_wifi__wifi_records__list_view.adapter as WifiArrayAdapter
                adapter.add(newWifiRecord)
            } else {
                Log.e(TAG, result)
            }
        }
    }
}