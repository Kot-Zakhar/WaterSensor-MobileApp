package com.zakhar.watersensorapp.dataModels.messages.responses

import com.zakhar.watersensorapp.dataModels.EmailSettings
import kotlinx.serialization.Serializable

@Serializable
class EmailSettingsResponse (
    val status: String,
    val payload: EmailSettings
)