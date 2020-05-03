package com.zakhar.watersensorapp.wifiActivity

import kotlinx.serialization.Serializable

@Serializable
data class WifiRecord(
    var ssid: String = "",
    var password: String = ""
)