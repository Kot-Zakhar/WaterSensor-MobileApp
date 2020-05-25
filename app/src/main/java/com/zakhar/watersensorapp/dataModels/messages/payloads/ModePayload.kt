package com.zakhar.watersensorapp.dataModels.messages.payloads

import kotlinx.serialization.Serializable

@Serializable
data class ModePayload (
    val config: Boolean
)