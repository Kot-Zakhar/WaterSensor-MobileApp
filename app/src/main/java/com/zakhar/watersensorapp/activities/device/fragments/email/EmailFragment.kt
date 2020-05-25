package com.zakhar.watersensorapp.activities.device.fragments.email

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.zakhar.watersensorapp.R
import com.zakhar.watersensorapp.dataModels.EmailSettings
import com.zakhar.watersensorapp.databinding.FragmentSettingsEmailBinding
import com.zakhar.watersensorapp.models.SensorDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class EmailFragment: Fragment(), CoroutineScope {
    private val TAG = "EmailFragment"

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private var job = Job()

    private lateinit var binding: FragmentSettingsEmailBinding

    var emailSettings: EmailSettings? = null

    private lateinit var smtpPorts: Array<String>
    private lateinit var imapPorts: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_settings_email,
            container,
            false)

        smtpPorts = resources.getStringArray(R.array.possible_smtp_ports)
        imapPorts = resources.getStringArray(R.array.possible_imap_ports)

        initSettings()

        binding.saveButton.setOnClickListener {
            launch {
                val newSettings = readSettings()
                if (SensorDevice.getInstance()?.setEmailSettings(newSettings) == true) {
                    emailSettings = newSettings
                }
                showSettings(emailSettings!!)
            }
        }

        binding.resetButton.setOnClickListener {
            showSettings(emailSettings!!)
        }

        return binding.root
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    fun initSettings() {
        launch {
            emailSettings = SensorDevice.getInstance()?.getEmailSettings()
            showSettings(emailSettings!!);
        }
    }

    fun showSettings(settings: EmailSettings) {
        binding.emailSettings = settings
        binding.smtpPort.setSelection(smtpPorts.indexOf(settings.smtpPort))
        binding.imapPort.setSelection(imapPorts.indexOf(settings.imapPort))
        binding.notifyChange()
    }

    fun readSettings(): EmailSettings {
        // TODO: check for values to be null or empty
        binding.apply {
            return EmailSettings(
                smtpServer.text.toString(),
                smtpPorts[smtpPort.selectedItemPosition],
                imapServer.text.toString(),
                imapPorts[imapPort.selectedItemPosition],
                email.text.toString(),
                password.text.toString(),
                sender.text.toString(),
                recipient.text.toString()
            )

        }

    }
}