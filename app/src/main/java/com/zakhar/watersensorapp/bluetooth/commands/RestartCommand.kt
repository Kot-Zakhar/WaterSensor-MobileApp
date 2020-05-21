package com.zakhar.watersensorapp.bluetooth.commands

import android.widget.Button
import com.zakhar.watersensorapp.bluetooth.CurrentBluetoothDevice
import com.zakhar.watersensorapp.bluetooth.messages.responses.SimpleResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.Json

class RestartCommand (
    private val coroutineScope: CoroutineScope,
    private val button: Button
) {
    private val TAG = "RestartCommand"

    suspend fun execute(): Boolean {
        button.isEnabled = false
        var serializedResponse = CurrentBluetoothDevice.sendCommandWithFeedback("{\"command\":\"restart\"}")
        var res = Json.parse(SimpleResponse.serializer(), serializedResponse);
        button.isEnabled = true
        return res.isOk()
    }

}