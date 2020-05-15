package com.zakhar.watersensorapp.settings.email

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.zakhar.watersensorapp.bluetooth.CurrentBluetoothDevice
import com.zakhar.watersensorapp.R
import com.zakhar.watersensorapp.bluetooth.commands.EmailCommand
import com.zakhar.watersensorapp.bluetooth.messages.queries.EmailSettingsSetQuery
import com.zakhar.watersensorapp.bluetooth.messages.responses.SimpleResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext

class EmailFragment: Fragment(), CoroutineScope {
    private val TAG = "EmailFragment"
    private var rootView: View? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var job = Job()

    val emailCommand = EmailCommand(this)
    var emailSettings: EmailSettings? = null

    private lateinit var smtpPorts: Array<String>
    private lateinit var imapPorts: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_settings_email, container, false)

        smtpPorts = resources.getStringArray(R.array.possible_smtp_ports)
        imapPorts = resources.getStringArray(R.array.possible_imap_ports)

        initSettings()

        val saveButtonView = rootView!!.findViewById<Button>(R.id.fragment_settings_email__actions__save_button)
        val resetButtonView = rootView!!.findViewById<Button>(R.id.fragment_settings_email__actions__reset_button)

        saveButtonView.setOnClickListener {
            launch {
                var newSettingsRequest = EmailSettingsSetQuery(readSettings())
                var serializedRequest = Json.stringify(EmailSettingsSetQuery.serializer(), newSettingsRequest)
                var serializedResponse = CurrentBluetoothDevice.sendCommandWithFeedback(serializedRequest)
                var response = Json.parse(SimpleResponse.serializer(), serializedResponse)
                if (response.isOk()) {
                    emailSettings = newSettingsRequest.payload
                } else {
                    showSettings(emailSettings!!)
                }
            }
        }

        resetButtonView.setOnClickListener {
            showSettings(emailSettings!!);
        }
        return rootView
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    fun initSettings() {
        launch {
            emailSettings = emailCommand.getSettings()
            showSettings(emailSettings!!);
        }
    }

    fun showSettings(settings: EmailSettings) {
        rootView!!.findViewById<TextView>(R.id.fragment_settings_email__settings__smtp_server).text = settings.smtpServer
        rootView!!.findViewById<Spinner>(R.id.fragment_settings_email__settings__smtp_port).setSelection(smtpPorts.indexOf(settings.smtpPort))

        rootView!!.findViewById<TextView>(R.id.fragment_settings_email__settings__imap_server).text = settings.imapServer
        rootView!!.findViewById<Spinner>(R.id.fragment_settings_email__settings__imap_port).setSelection(imapPorts.indexOf(settings.imapPort))

        rootView!!.findViewById<TextView>(R.id.fragment_settings_email__settings__email).text = settings.email
        rootView!!.findViewById<TextView>(R.id.fragment_settings_email__settings__pass).text = settings.password
        rootView!!.findViewById<TextView>(R.id.fragment_settings_email__settings__sender).text = settings.sender
        rootView!!.findViewById<TextView>(R.id.fragment_settings_email__settings__recipient).text = settings.recipient
    }

    fun readSettings(): EmailSettings {
        // TODO: check for values to be null or empty
        return EmailSettings(
            rootView!!.findViewById<TextView>(R.id.fragment_settings_email__settings__smtp_server).text.toString(),
            smtpPorts[rootView!!.findViewById<Spinner>(R.id.fragment_settings_email__settings__smtp_port).selectedItemPosition],
            rootView!!.findViewById<TextView>(R.id.fragment_settings_email__settings__imap_server).text.toString(),
            imapPorts[rootView!!.findViewById<Spinner>(R.id.fragment_settings_email__settings__imap_port).selectedItemPosition],
            rootView!!.findViewById<TextView>(R.id.fragment_settings_email__settings__email).text.toString(),
            rootView!!.findViewById<TextView>(R.id.fragment_settings_email__settings__pass).text.toString(),
            rootView!!.findViewById<TextView>(R.id.fragment_settings_email__settings__sender).text.toString(),
            rootView!!.findViewById<TextView>(R.id.fragment_settings_email__settings__recipient).text.toString()
        )
    }
}