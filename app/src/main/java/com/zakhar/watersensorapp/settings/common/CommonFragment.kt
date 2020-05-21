package com.zakhar.watersensorapp.settings.common

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.TextView

import com.zakhar.watersensorapp.R
import com.zakhar.watersensorapp.bluetooth.CurrentBluetoothDevice
import com.zakhar.watersensorapp.bluetooth.commands.PingCommand
import com.zakhar.watersensorapp.bluetooth.commands.RestartCommand
import com.zakhar.watersensorapp.bluetooth.messages.payloads.ModePayload
import com.zakhar.watersensorapp.bluetooth.messages.queries.ModeSetQuery
import com.zakhar.watersensorapp.bluetooth.messages.responses.GetModeResponse
import com.zakhar.watersensorapp.bluetooth.messages.responses.SimpleResponse
import kotlinx.android.synthetic.main.fragment_settings_common.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext

class CommonFragment : Fragment(), CoroutineScope {
    private val TAG = "CommonFragment"
    private lateinit var rootView: View

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var job = Job()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_settings_common, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSwitch()

        fragment_common__mode__switch.setOnCheckedChangeListener(::changeMode)
        fragment_common__restart__button.setOnClickListener(::restartCommand)
        PingCommand(this, fragment_common__ping__button, fragment_common__ping__text_view)
    }


    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    private fun initSwitch() {
        launch {
            var query = "{\"command\":\"get mode\"}"
            var serializedResponse = CurrentBluetoothDevice.sendCommandWithFeedback(query)
            var res = Json.parse(GetModeResponse.serializer(), serializedResponse)
            fragment_common__mode__switch.isChecked = res.payload.config
        }
    }

    private fun changeMode(buttonView: CompoundButton, isChecked: Boolean) {
        launch {
            var query = ModeSetQuery(ModePayload(isChecked))
            var serializedQuery = Json.stringify(ModeSetQuery.serializer(), query)
            var serializedResponse = CurrentBluetoothDevice.sendCommandWithFeedback(serializedQuery)
            var result = Json.parse(SimpleResponse.serializer(), serializedResponse)
            if (!result.isOk()) {
                buttonView.isChecked = !isChecked
            }
        }
    }

    private fun restartCommand(buttonView: View) {
        launch {
            var restart = RestartCommand(this, buttonView as Button).execute()
            if (restart) {
                activity?.finish()
            }
        }
    }
}
