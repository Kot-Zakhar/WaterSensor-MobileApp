package com.zakhar.watersensorapp.bluetooth.commands.wifi

import android.util.Log
import com.zakhar.watersensorapp.CurrentBluetoothDevice
import com.zakhar.watersensorapp.bluetooth.commands.BluetoothCommand
import com.zakhar.watersensorapp.bluetooth.messages.queries.RemoveWifiRecordByIndexQuery
import com.zakhar.watersensorapp.bluetooth.messages.responses.SimpleResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class EditWifiRecordCommand(
) : BluetoothCommand() {
    private val TAG = "EditWifiRecordCommand"

    suspend fun removeRecord(position: Int): Boolean {
        val request = RemoveWifiRecordByIndexQuery(position);
        val serializedRequest = Json.stringify(RemoveWifiRecordByIndexQuery.serializer(), request);
        val serializedResponse = CurrentBluetoothDevice.sendCommandWithFeedback(serializedRequest);
        Log.d(TAG, serializedResponse)
        val answer= Json.parse(SimpleResponse.serializer(), serializedResponse)
        return answer.isOk()
    }

    override fun execute() {
        TODO("Not yet implemented")
    }

}