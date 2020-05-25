package com.zakhar.watersensorapp.activities.device.fragments.wifi

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.zakhar.watersensorapp.R
import com.zakhar.watersensorapp.dataModels.WifiRecord
import com.zakhar.watersensorapp.databinding.FragmentSettingsWifiBinding
import com.zakhar.watersensorapp.models.SensorDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class WifiFragment: Fragment(), CoroutineScope {
    private val TAG = "WifiFragment"

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private var job = Job()

    private lateinit var wifiArrayAdapter: WifiArrayAdapter

    private lateinit var binding: FragmentSettingsWifiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        wifiArrayAdapter = WifiArrayAdapter(this, requireContext())

        launch {
            val device = SensorDevice.getInstance()
            if (device == null) {
                activity?.finish()
                return@launch
            }
            val records = SensorDevice.getInstance()?.getWifiSettings() ?: emptyList()
            wifiArrayAdapter.addAll(records)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_settings_wifi,
            container,
            false)

        binding.apply {
            wifiRecords.adapter = wifiArrayAdapter
            newRecordConfirm.setOnClickListener(::createNewWifiRecord)
            addRecord.setOnClickListener(::toggleNewWifiForm)
        }
        return binding.root
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    // new wifi

    var newWifi: WifiRecord = WifiRecord()

    fun toggleNewWifiForm(view: View) {
        binding.newRecord.apply {
            visibility = if (visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
    }

    fun createNewWifiRecord(view: View) {
        val newWifiRecord = WifiRecord(
            binding.newRecordSsid.text.toString(),
            binding.newRecordPassword.text.toString()
        )

        launch {
            val device = SensorDevice.getInstance()
            if (device == null) {
                activity?.finish()
                return@launch
            }

            if (device?.addWifiRecord(newWifiRecord)) {
                binding.newRecordSsid.text.clear()
                binding.newRecordPassword.text.clear()
                toggleNewWifiForm(binding.newRecordConfirm)
                (binding.wifiRecords.adapter as WifiArrayAdapter).add(newWifiRecord)
            } else {
                Log.e(TAG, "Couldn't add wifi record")
                // todo: add what to do when wifi record is not created
            }
        }
    }
}