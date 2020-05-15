package com.zakhar.watersensorapp.bluetooth.messages.responses

import com.zakhar.watersensorapp.settings.email.EmailSettings
import kotlinx.serialization.Serializable

@Serializable
class EmailSettingsResponse (
    val status: String,
    val payload: EmailSettings
)