package com.zakhar.watersensorapp.bluetoothCommands

import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.widget.Button
import androidx.core.content.ContextCompat.startActivity
import com.zakhar.watersensorapp.wifiActivity.WifiActivity

class WifiCommand(
    private val packageContext: Context,
    private val button: Button
) : BluetoothCommand()
{
    private val TAG = "WifiCommand"
    init {
        button.setOnClickListener { execute() }
    }
    override fun execute() {
        val intent = Intent(packageContext, WifiActivity::class.java)
        startActivity(packageContext, intent, null)
    }
}