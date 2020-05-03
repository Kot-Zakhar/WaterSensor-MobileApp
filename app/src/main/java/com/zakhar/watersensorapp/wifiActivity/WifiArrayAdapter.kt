package com.zakhar.watersensorapp.wifiActivity

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.zakhar.watersensorapp.R

class WifiArrayAdapter: ArrayAdapter<WifiRecord> {
    private val TAG = "WifiArrayAdapter"
    constructor(context: Context) : super(context, R.layout.wifi_list_item)
    constructor(context: Context, textViewResourceId: Int) : super(context, R.layout.wifi_list_item, textViewResourceId)
    constructor(context: Context, objects: Array<out WifiRecord>) : super(context, R.layout.wifi_list_item, objects)
    constructor(context: Context, textViewResourceId: Int, objects: Array<out WifiRecord>) : super(
        context,
        R.layout.wifi_list_item,
        textViewResourceId,
        objects
    )
    constructor(context: Context, objects: MutableList<WifiRecord>) : super(context, R.layout.wifi_list_item, objects)
    constructor(context: Context, textViewResourceId: Int, objects: MutableList<WifiRecord>) : super(
        context,
        R.layout.wifi_list_item,
        textViewResourceId,
        objects
    )

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val wifiRecord = this.getItem(position)

        val inflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val rowView = inflater.inflate(R.layout.wifi_list_item, parent, false)

        val ssid = rowView.findViewById<TextView>(R.id.wifi_list_item__ssid__text_view)
        ssid.setText(wifiRecord?.ssid)

        val clearButton = rowView.findViewById<ImageButton>(R.id.wifi_list_item__clear__image_button)
        clearButton.setOnClickListener{
            Log.i(TAG, "Clear button of ${wifiRecord?.ssid}")
        }

        return rowView
    }


}