package com.zakhar.watersensorapp.dataModels.messages.queries

import com.zakhar.watersensorapp.dataModels.WifiRecord
import kotlinx.serialization.Serializable

@Serializable
data class NewWifiRecordQuery (
    val payload: WifiRecord
) {
    val command: String = "new wifi"
}