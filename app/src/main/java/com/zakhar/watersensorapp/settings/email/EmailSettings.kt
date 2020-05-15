package com.zakhar.watersensorapp.settings.email

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmailSettings(
    @SerialName("smtp server")
    val smtpServer: String,
    @SerialName("smtp port")
    val smtpPort: String,
    @SerialName("imap server")
    val imapServer: String,
    @SerialName("imap port")
    val imapPort: String,
    @SerialName("login")
    val email: String,
    @SerialName("pass")
    val password: String,
    val sender: String,
    val recipient: String
)