package com.zakhar.watersensorapp.dataModels.messages.responses

import kotlinx.serialization.Serializable

@Serializable
open class SimpleResponse {
    val status: String = "Error"

    fun isOk(): Boolean {
        return status.toLowerCase() == "ok"
    }
}