package com.zakhar.watersensorapp.bluetooth.messages.queries

import com.zakhar.watersensorapp.settings.email.EmailSettings
import kotlinx.serialization.Serializable

@Serializable
class EmailSettingsSetQuery (
    val payload: EmailSettings
) {
    val command = "set email"
}