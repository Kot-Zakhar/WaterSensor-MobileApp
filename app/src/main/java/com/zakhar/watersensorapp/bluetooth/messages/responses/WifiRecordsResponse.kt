package com.zakhar.watersensorapp.bluetooth.messages.responses

import com.zakhar.watersensorapp.wifiActivity.WifiRecord
import kotlinx.serialization.Serializable

@Serializable
class WifiRecordsResponse(
    val payload: List<WifiRecord> = ArrayList<WifiRecord>()
) : SimpleResponse()