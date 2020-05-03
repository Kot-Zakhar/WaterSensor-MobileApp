package com.zakhar.watersensorapp.mainActivity

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.zakhar.watersensorapp.R


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
        val device = this.getItem(position)

        val inflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val rowView = inflater.inflate(R.layout.bluetooth_device_list_item, parent, false)

        val nameView = rowView.findViewById<TextView>(R.id.bluetooth_device_list_item_name)
        val addressView = rowView.findViewById<TextView>(R.id.bluetooth_device_list_item_address)
        nameView.text = device?.name
        addressView.text = device?.address

        return rowView
    }

    override fun add(device: BluetoothDevice?) {
        if (getPosition(device) < 0)
            super.add(device)
    }
}
