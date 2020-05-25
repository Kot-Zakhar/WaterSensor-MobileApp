package com.zakhar.watersensorapp.dataModels.messages.responses

import com.zakhar.watersensorapp.dataModels.messages.payloads.ModePayload
import kotlinx.serialization.Serializable

@Serializable
data class GetModeResponse (
    val status: String,
    val payload: ModePayload
)

