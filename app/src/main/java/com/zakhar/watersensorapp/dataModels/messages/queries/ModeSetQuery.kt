package com.zakhar.watersensorapp.dataModels.messages.queries

import com.zakhar.watersensorapp.dataModels.messages.payloads.ModePayload
import kotlinx.serialization.Serializable

@Serializable
data class ModeSetQuery (
    val payload: ModePayload
) {
    val command = "switch mode"
}