package com.zakhar.watersensorapp.activities.devicesList

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.zakhar.watersensorapp.R
import com.zakhar.watersensorapp.databinding.BluetoothDeviceListItemBinding


class BluetoothDeviceArrayAdapter : ArrayAdapter<BluetoothDevice> {
    constructor(context: Context) : super(context, R.layout.bluetooth_device_list_item )
    constructor(context: Context, textViewResourceId: Int) : super(context,
        R.layout.bluetooth_device_list_item, textViewResourceId)
    constructor(context: Context, objects: Array<out BluetoothDevice>) : super(context,
        R.layout.bluetooth_device_list_item, objects)
    constructor(context: Context, textViewResourceId: Int, objects: Array<out BluetoothDevice>) : super(context,
        R.layout.bluetooth_device_list_item, textViewResourceId, objects)
    constructor(context: Context, objects: MutableList<BluetoothDevice>) : super(context,
        R.layout.bluetooth_device_list_item, objects)
    constructor(context: Context, textViewResourceId: Int, objects: MutableList<BluetoothDevice> ) : super(context,
        R.layout.bluetooth_device_list_item, textViewResourceId, objects)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: BluetoothDeviceListItemBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.bluetooth_device_list_item,
                parent,
                false
            )

        binding.device = this.getItem(position)
        return binding.root
    }

    override fun add(device: BluetoothDevice?) {
        if (getPosition(device) < 0)
            super.add(device)
    }
}
