package com.zakhar.watersensorapp.bluetooth.messages.responses

import kotlinx.serialization.Serializable

@Serializable
open class SimpleResponse {
    val status: String = "Error"

    fun isOk(): Boolean {
        return status.toLowerCase() == "ok"
    }
}