package com.zakhar.watersensorapp.activities.device.fragments.wifi

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.zakhar.watersensorapp.R
import com.zakhar.watersensorapp.dataModels.WifiRecord
import com.zakhar.watersensorapp.databinding.WifiListItemBinding
import com.zakhar.watersensorapp.models.SensorDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class WifiArrayAdapter(
    private val coroutineScope: CoroutineScope,
    context: Context
) : ArrayAdapter<WifiRecord>(context, R.layout.wifi_list_item) {
    private val TAG = "WifiArrayAdapter"

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val wifiRecord = this.getItem(position)

        val binding: WifiListItemBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.wifi_list_item,
                parent,
                false
            )
        binding.record = wifiRecord

        val arrayAdapter = this;

        binding.wifiListItemClearImageButton.setOnClickListener {
            coroutineScope.launch {
                if (SensorDevice.getInstance()?.removeWifiRecord(position) == true)
                    arrayAdapter.remove(wifiRecord)
            }
        }

        return binding.root
    }


}