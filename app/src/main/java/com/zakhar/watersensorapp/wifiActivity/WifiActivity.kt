package com.zakhar.watersensorapp.wifiActivity

import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.zakhar.watersensorapp.CurrentBluetoothDevice
import com.zakhar.watersensorapp.R
import com.zakhar.watersensorapp.bluetooth.commands.wifi.EditWifiRecordCommand
import com.zakhar.watersensorapp.bluetooth.messages.queries.NewWifiRecordQuery
import com.zakhar.watersensorapp.bluetooth.messages.responses.SimpleResponse
import com.zakhar.watersensorapp.bluetooth.messages.responses.WifiRecordsResponse
import kotlinx.android.synthetic.main.activity_wifi.*
import kotlinx.coroutines.*
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


        wifiArrayAdapter = WifiArrayAdapter(this, EditWifiRecordCommand(), this)

        activity_wifi__wifi_records__list_view.adapter = wifiArrayAdapter

        launch {

            val serializedWifiRecords = CurrentBluetoothDevice.sendCommandWithFeedback("{\"command\":\"show\"}")
            Log.d(TAG, serializedWifiRecords)
            val recordsMessage = Json.parse(WifiRecordsResponse.serializer(), serializedWifiRecords)
            wifiArrayAdapter.addAll(recordsMessage.payload)
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

        val newWifiRecordMessage =
            NewWifiRecordQuery(
                WifiRecord(
                    ssid,
                    password
                )
            )

        val serializedRecord = Json.stringify(NewWifiRecordQuery.serializer(), newWifiRecordMessage);
        Log.d(TAG, serializedRecord)

        launch {
            val serializedResponse = CurrentBluetoothDevice.sendCommandWithFeedback(serializedRecord)
            val response = Json.parse(SimpleResponse.serializer(), serializedResponse)
            if (response.status.trim().toLowerCase() == "ok") {
                activity_wifi__new_record__ssid__edit_text.text.clear()
                activity_wifi__new_record__password__edit_text.text.clear()
                toggleNewWifiForm(activity_wifi__new_record__confirm__image_button)
                val adapter = activity_wifi__wifi_records__list_view.adapter as WifiArrayAdapter
                adapter.add(newWifiRecordMessage.payload)
            } else {
                Log.e(TAG, response.status)
            }
        }
    }
}