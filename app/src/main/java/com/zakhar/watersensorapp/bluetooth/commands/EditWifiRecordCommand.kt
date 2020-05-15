package com.zakhar.watersensorapp.bluetooth.commands

import android.util.Log
import com.zakhar.watersensorapp.bluetooth.CurrentBluetoothDevice
import com.zakhar.watersensorapp.bluetooth.messages.queries.RemoveWifiRecordByIndexQuery
import com.zakhar.watersensorapp.bluetooth.messages.responses.SimpleResponse
import kotlinx.serialization.json.Json

class EditWifiRecordCommand {
    private val TAG = "EditWifiRecordCommand"

    suspend fun removeRecord(position: Int): Boolean {
        val request = RemoveWifiRecordByIndexQuery(position);
        val serializedRequest = Json.stringify(RemoveWifiRecordByIndexQuery.serializer(), request);
        val serializedResponse = CurrentBluetoothDevice.sendCommandWithFeedback(serializedRequest);
        Log.d(TAG, serializedResponse)
        val answer= Json.parse(SimpleResponse.serializer(), serializedResponse)
        return answer.isOk()
    }
}