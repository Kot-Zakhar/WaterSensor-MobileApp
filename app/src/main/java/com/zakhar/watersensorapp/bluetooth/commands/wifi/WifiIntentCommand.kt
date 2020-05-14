package com.zakhar.watersensorapp.bluetooth.commands.wifi

import android.content.Context
import android.content.Intent
import android.widget.Button
import androidx.core.content.ContextCompat.startActivity
import com.zakhar.watersensorapp.bluetooth.commands.BluetoothCommand
import com.zakhar.watersensorapp.wifiActivity.WifiActivity

class WifiIntentCommand(
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