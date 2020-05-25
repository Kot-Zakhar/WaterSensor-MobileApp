package com.zakhar.watersensorapp.dataModels.messages.queries

import com.zakhar.watersensorapp.dataModels.EmailSettings
import kotlinx.serialization.Serializable

@Serializable
class EmailSettingsSetQuery (
    val payload: EmailSettings
) {
    val command = "set email"
}