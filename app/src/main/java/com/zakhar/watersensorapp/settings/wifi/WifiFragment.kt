package com.zakhar.watersensorapp.settings.wifi

import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.zakhar.watersensorapp.bluetooth.CurrentBluetoothDevice
import com.zakhar.watersensorapp.R
import com.zakhar.watersensorapp.bluetooth.commands.EditWifiRecordCommand
import com.zakhar.watersensorapp.bluetooth.messages.queries.NewWifiRecordQuery
import com.zakhar.watersensorapp.bluetooth.messages.responses.SimpleResponse
import com.zakhar.watersensorapp.bluetooth.messages.responses.WifiRecordsResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext
import kotlin.system.exitProcess

class WifiFragment: Fragment(), CoroutineScope {
    private val TAG = "WifiFragment"

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var job = Job()
    private var socket: BluetoothSocket? = null
    private lateinit var wifiArrayAdapter: WifiArrayAdapter
    private var rootView: View? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        socket = CurrentBluetoothDevice.socket

        if (socket == null) {
            Log.i(TAG, "Socket is null")
            exitProcess(1)
        }

        wifiArrayAdapter = WifiArrayAdapter(this,
            EditWifiRecordCommand(), requireContext())

        launch {

            val serializedWifiRecords = CurrentBluetoothDevice.sendCommandWithFeedback("{\"command\":\"show\"}")
            Log.d(TAG, serializedWifiRecords)
            val recordsMessage = Json.parse(WifiRecordsResponse.serializer(), serializedWifiRecords)
            wifiArrayAdapter.addAll(recordsMessage.payload)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_settings_wifi, container, false)

        rootView!!.findViewById<ListView>(R.id.fragment_settings_wifi__wifi_records__list_view).adapter = wifiArrayAdapter
        rootView!!.findViewById<ImageButton>(R.id.fragment_settings_wifi__new_record__confirm__image_button).setOnClickListener { createNewWifiRecord(it) }
        rootView!!.findViewById<ImageButton>(R.id.fragment_settings_wifi__add_record__image_button).setOnClickListener { toggleNewWifiForm(it) }
        return rootView
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    fun toggleNewWifiForm(view: View) {
        val listView = rootView!!.findViewById<LinearLayout>(R.id.fragment_settings_wifi__new_record__layout)
        if (listView.visibility == View.VISIBLE) {
            listView.visibility = View.GONE
        } else {
            listView.visibility = View.VISIBLE
        }
    }

    fun createNewWifiRecord(view: View) {
        val listView = rootView!!.findViewById<ListView>(R.id.fragment_settings_wifi__wifi_records__list_view)
        val ssidEditTextView = rootView!!.findViewById<EditText>(R.id.fragment_settings_wifi__new_record__ssid__edit_text)
        val passwordEditTextView = rootView!!.findViewById<EditText>(R.id.fragment_settings_wifi__new_record__password__edit_text)
        val confirmImageButton = rootView!!.findViewById<ImageView>(R.id.fragment_settings_wifi__new_record__confirm__image_button)

        val ssid = ssidEditTextView.text.toString().trim()
        val password = passwordEditTextView.text.toString().trim()
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
                ssidEditTextView.text.clear()
                passwordEditTextView.text.clear()
                toggleNewWifiForm(confirmImageButton)
                val adapter = listView.adapter as WifiArrayAdapter
                adapter.add(newWifiRecordMessage.payload)
            } else {
                Log.e(TAG, response.status)
            }
        }
    }
}