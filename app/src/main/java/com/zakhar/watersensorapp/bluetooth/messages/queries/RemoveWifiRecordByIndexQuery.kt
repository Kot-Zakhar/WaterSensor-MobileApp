package com.zakhar.watersensorapp.bluetooth.messages.queries

import kotlinx.serialization.Serializable

@Serializable
data class RemoveWifiRecordByIndexQuery(
    val index: Int
) {
    val command = "remove wifi single"
}