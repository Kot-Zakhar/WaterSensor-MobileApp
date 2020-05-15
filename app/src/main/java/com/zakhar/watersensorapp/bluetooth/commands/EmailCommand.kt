package com.zakhar.watersensorapp.bluetooth.commands

import android.util.Log
import com.zakhar.watersensorapp.bluetooth.CurrentBluetoothDevice
import com.zakhar.watersensorapp.bluetooth.messages.responses.EmailSettingsResponse
import com.zakhar.watersensorapp.settings.email.EmailSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.Json

class EmailCommand(
    private val coroutineScope: CoroutineScope
) {
    private val TAG = "EmailCommand"

    suspend fun getSettings(): EmailSettings {
        val serializedSettings = CurrentBluetoothDevice.sendCommandWithFeedback("{\"command\":\"email\"}")
        Log.d(TAG, serializedSettings)
        val settings = Json.parse(EmailSettingsResponse.serializer(), serializedSettings)
        return settings.payload
    }
}