package com.zakhar.watersensorapp.settings.wifi

import kotlinx.serialization.Serializable

@Serializable
data class WifiRecord(
    var ssid: String = "",
    var password: String = ""
)