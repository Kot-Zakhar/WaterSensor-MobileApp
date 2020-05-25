package com.zakhar.watersensorapp.dataModels.messages.responses

import com.zakhar.watersensorapp.dataModels.WifiRecord
import kotlinx.serialization.Serializable

@Serializable
class WifiRecordsResponse(
    val payload: List<WifiRecord> = ArrayList<WifiRecord>()
) : SimpleResponse()