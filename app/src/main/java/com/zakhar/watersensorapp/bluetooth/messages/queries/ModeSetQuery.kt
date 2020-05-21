package com.zakhar.watersensorapp.bluetooth.messages.queries

import com.zakhar.watersensorapp.bluetooth.messages.payloads.ModePayload
import kotlinx.serialization.Serializable

@Serializable
data class ModeSetQuery (
    val payload: ModePayload
) {
    val command = "switch mode"
}