package com.zakhar.watersensorapp.dataModels.messages.queries

import kotlinx.serialization.Serializable

@Serializable
data class RemoveWifiRecordByIndexQuery(
    val index: Int
) {
    val command = "remove wifi single"
}