package com.zakhar.watersensorapp.dataModels

import kotlinx.serialization.Serializable

@Serializable
data class WifiRecord(
    var ssid: String = "",
    var password: String = ""
)