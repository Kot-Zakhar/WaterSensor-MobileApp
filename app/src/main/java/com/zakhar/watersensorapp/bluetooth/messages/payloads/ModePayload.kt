package com.zakhar.watersensorapp.bluetooth.messages.payloads

import kotlinx.serialization.Serializable

@Serializable
data class ModePayload (
    val config: Boolean
)