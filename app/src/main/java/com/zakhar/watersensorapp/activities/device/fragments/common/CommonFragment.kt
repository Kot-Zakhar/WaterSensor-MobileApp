package com.zakhar.watersensorapp.activities.device.fragments.common

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import androidx.databinding.DataBindingUtil
import com.zakhar.watersensorapp.R
import com.zakhar.watersensorapp.databinding.FragmentSettingsCommonBinding
import com.zakhar.watersensorapp.models.SensorDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class CommonFragment : Fragment(), CoroutineScope {
    private val TAG = "CommonFragment"

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private var job = Job()

    private lateinit var binding: FragmentSettingsCommonBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_settings_common,
            container,
            false
        )

        initSwitch(binding.modeSwitch)

        binding.modeSwitch.setOnCheckedChangeListener(::changeMode)
        binding.restartBtn.setOnClickListener(::restartCommand)
        binding.pingBtn.setOnClickListener(::pingCommand)

        return binding.root
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    private fun initSwitch(switch: Switch) {
        launch {
            val mode = SensorDevice.getInstance()?.getMode()
            if (mode != null) {
                switch.isChecked = mode
            } else {
                Log.e(TAG, "Mode is null")
                throw Error("mode is null")
            }
        }
    }

    private fun changeMode(buttonView: CompoundButton, isChecked: Boolean) {
        launch {
            if (SensorDevice.getInstance()?.setMode(isChecked) == false) {
                Log.e(TAG, "Couldn't change mode.")
                buttonView.isChecked = !isChecked
            }
        }
    }

    private fun restartCommand(button: View) {
        launch {
            button.isEnabled = false
            if (SensorDevice.getInstance()?.restart() == false) {
                Log.e(TAG, "Error while restarting")
            } else {
                activity?.finish()
            }
            button.isEnabled = true
        }
    }

    private fun pingCommand(button: View) {
        launch {
            button.isEnabled = false
            val echo = SensorDevice.getInstance()?.ping()
            if (echo != null) {
                binding.pingTv.text = echo.toString() + "ms"
            } else {
                binding.pingTv.text = "Connection error"
            }
            button.isEnabled = true
        }
    }
}
