package com.zakhar.watersensorapp.models

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import com.zakhar.watersensorapp.activities.device.fragments.wifi.WifiArrayAdapter
import com.zakhar.watersensorapp.bluetooth.sendCommandWithFeedback
import com.zakhar.watersensorapp.dataModels.EmailSettings
import com.zakhar.watersensorapp.dataModels.WifiRecord
import com.zakhar.watersensorapp.dataModels.messages.payloads.ModePayload
import com.zakhar.watersensorapp.dataModels.messages.queries.EmailSettingsSetQuery
import com.zakhar.watersensorapp.dataModels.messages.queries.ModeSetQuery
import com.zakhar.watersensorapp.dataModels.messages.queries.NewWifiRecordQuery
import com.zakhar.watersensorapp.dataModels.messages.queries.RemoveWifiRecordByIndexQuery
import com.zakhar.watersensorapp.dataModels.messages.responses.*
import kotlinx.serialization.json.Json
import java.util.*
import java.util.concurrent.TimeUnit

class SensorDevice private constructor(val device: BluetoothDevice) {
    companion object {
        private val TAG = "SensorDevice"
        private var appUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        private var instance: SensorDevice? = null

        fun getInstance(): SensorDevice? = instance

        fun createSensorDevice(device: BluetoothDevice): SensorDevice {
            if (instance != null) {
                close()
            }
            instance = SensorDevice(device)
            return instance!!
        }

        fun close() {
            val localInstance = this.instance ?: return
            this.instance = null
            localInstance.close()
        }
    }

    private var socket: BluetoothSocket? = null

//    var wifiRecords: List<WifiRecord>? = null
//        get() {
//            return getWifiSettings()
//        }
//        private set(value) {
//
//        }

//    private var emailSettings: EmailSettings? = null

    private fun close() {
        socket?.close()
    }

    fun tryToConnect(onSuccess: ()->Unit, onFailure: ()->Unit): Boolean {
        socket = device.createRfcommSocketToServiceRecord(appUUID)
        socket!!.connect()
        val result = socket!!.isConnected
        if (result) {
            onSuccess()
        } else {
            onFailure()
        }
        return result
    }

    suspend fun getSensorValue(): Long? {
        val serializedSensorValueResponse = socket?.sendCommandWithFeedback("{\"command\":\"sensor value\"}") ?: return null
        val sensorValueResponse = Json.parse(SensorValueResponse.serializer(), serializedSensorValueResponse)
        return if (sensorValueResponse.status.toLowerCase() == "ok") {
            sensorValueResponse.payload
        } else {
            null
        }
    }

    suspend fun getWifiSettings(): List<WifiRecord> {
        val serializedWifiRecords = socket?.sendCommandWithFeedback("{\"command\":\"show\"}") ?: return emptyList()
        val recordsMessage = Json.parse(WifiRecordsResponse.serializer(), serializedWifiRecords)
        return recordsMessage.payload
    }

    suspend fun addWifiRecord(record: WifiRecord): Boolean {
        val recordQuery = NewWifiRecordQuery(record)
        val serializedRecord = Json.stringify(NewWifiRecordQuery.serializer(), recordQuery);
        val serializedResponse = socket?.sendCommandWithFeedback(serializedRecord) ?: return false
        val response = Json.parse(SimpleResponse.serializer(), serializedResponse)
        return response.status.trim().toLowerCase() == "ok"
    }

    suspend fun removeWifiRecord(position: Int): Boolean {
        val request = RemoveWifiRecordByIndexQuery(position);
        val serializedRequest = Json.stringify(RemoveWifiRecordByIndexQuery.serializer(), request);
        val serializedResponse = socket?.sendCommandWithFeedback(serializedRequest) ?: return false
        Log.d(TAG, serializedResponse)
        val answer= Json.parse(SimpleResponse.serializer(), serializedResponse)
        return answer.isOk()
    }

    suspend fun setEmailSettings(newSettings: EmailSettings): Boolean {
        val newSettingsRequest = EmailSettingsSetQuery(newSettings)
        val serializedRequest = Json.stringify(EmailSettingsSetQuery.serializer(), newSettingsRequest)
        val serializedResponse = socket?.sendCommandWithFeedback(serializedRequest) ?: return false
        val response = Json.parse(SimpleResponse.serializer(), serializedResponse)
        return response.status.trim().toLowerCase() == "ok"
    }

    suspend fun getEmailSettings(): EmailSettings? {
        val serializedSettings = socket?.sendCommandWithFeedback("{\"command\":\"email\"}") ?: return null
        Log.d(TAG, serializedSettings)
        val settings = Json.parse(EmailSettingsResponse.serializer(), serializedSettings)
        return settings.payload
    }

    suspend fun getMode(): Boolean? {
        var query = "{\"command\":\"get mode\"}"
        var serializedResponse = socket?.sendCommandWithFeedback(query) ?: return null
        var res = Json.parse(GetModeResponse.serializer(), serializedResponse)
        return res.payload.config
    }

    suspend fun setMode(isConfig: Boolean): Boolean {
        var query = ModeSetQuery(ModePayload(isConfig))
        var serializedQuery = Json.stringify(ModeSetQuery.serializer(), query)
        var serializedResponse = socket?.sendCommandWithFeedback(serializedQuery) ?: return false
        var result = Json.parse(SimpleResponse.serializer(), serializedResponse)
        return result.isOk()
    }

    suspend fun ping(): Long? {
        val startTime = System.nanoTime()
        val serializedResponse = socket?.sendCommandWithFeedback("{\"command\":\"ping\"}") ?: return null
        val stopTime = System.nanoTime()
        var result = Json.parse(SimpleResponse.serializer(), serializedResponse)
        return if (result.isOk()) {
            TimeUnit.MILLISECONDS.convert(stopTime - startTime, TimeUnit.NANOSECONDS)
        } else {
            null
        }
    }

    suspend fun restart(): Boolean {
        var serializedResponse = socket?.sendCommandWithFeedback("{\"command\":\"restart\"}") ?: return false
        var res = Json.parse(SimpleResponse.serializer(), serializedResponse);
        return res.isOk()
    }
}