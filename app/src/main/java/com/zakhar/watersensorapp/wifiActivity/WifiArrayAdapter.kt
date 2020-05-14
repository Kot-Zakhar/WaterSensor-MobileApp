package com.zakhar.watersensorapp.wifiActivity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.zakhar.watersensorapp.R
import com.zakhar.watersensorapp.bluetooth.commands.wifi.EditWifiRecordCommand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class WifiArrayAdapter(
    private val coroutineScope: CoroutineScope,
    val editCommand: EditWifiRecordCommand,
    context: Context
) : ArrayAdapter<WifiRecord>(context, R.layout.wifi_list_item) {
    private val TAG = "WifiArrayAdapter"

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val wifiRecord = this.getItem(position)

        val inflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val rowView = inflater.inflate(R.layout.wifi_list_item, parent, false)

        val ssid = rowView.findViewById<TextView>(R.id.wifi_list_item__ssid__text_view)
        ssid.setText(wifiRecord?.ssid)

        val clearButton = rowView.findViewById<ImageButton>(R.id.wifi_list_item__clear__image_button)

        val arrayAdapter = this;

        clearButton.setOnClickListener{
            coroutineScope.launch {
                if (editCommand.removeRecord(position))
                    arrayAdapter.remove(wifiRecord)
            }
        }

        return rowView
    }


}