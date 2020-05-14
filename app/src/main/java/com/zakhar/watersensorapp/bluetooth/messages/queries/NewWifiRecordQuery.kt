package com.zakhar.watersensorapp.bluetooth.messages.queries

import com.zakhar.watersensorapp.wifiActivity.WifiRecord
import kotlinx.serialization.Serializable

@Serializable
data class NewWifiRecordQuery (
    val payload: WifiRecord
) {
    val command: String = "new wifi"
}