package com.zakhar.watersensorapp.settings

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.zakhar.watersensorapp.bluetooth.CurrentBluetoothDevice
import com.zakhar.watersensorapp.R
import com.zakhar.watersensorapp.bluetooth.DeviceConnectivityService.Companion.DISCONNECTION_BROADCAST_FILTER
import kotlinx.coroutines.channels.BroadcastChannel

class SettingsActivity : AppCompatActivity() {
    val TAG = "SettingsActivity"
    lateinit var mReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val navView: BottomNavigationView = findViewById(R.id.activity_settings__nav_view)

        mReceiver = object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.i(TAG, "Connection was lost: exiting the activity")
                finish()
            }
        }

        registerReceiver(mReceiver, IntentFilter(DISCONNECTION_BROADCAST_FILTER))

        val navController = findNavController(R.id.activity_settings__nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_settings_wifi,
                R.id.navigation_settings_email
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onDestroy() {
        unregisterReceiver(mReceiver)
        CurrentBluetoothDevice.device = null;
        super.onDestroy()
    }
}
