package com.zakhar.watersensorapp.bluetooth.messages.responses

import com.zakhar.watersensorapp.bluetooth.messages.payloads.ModePayload
import kotlinx.serialization.Serializable

@Serializable
data class GetModeResponse (
    val status: String,
    val payload: ModePayload
)

