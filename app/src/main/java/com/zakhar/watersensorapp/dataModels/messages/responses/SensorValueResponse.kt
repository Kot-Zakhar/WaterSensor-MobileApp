package com.zakhar.watersensorapp.dataModels.messages.responses

import kotlinx.serialization.Serializable

@Serializable
data class SensorValueResponse (
    val status: String,
    val payload: Long
)