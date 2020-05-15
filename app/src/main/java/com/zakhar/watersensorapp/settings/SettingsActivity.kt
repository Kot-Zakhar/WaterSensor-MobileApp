package com.zakhar.watersensorapp.settings

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.zakhar.watersensorapp.bluetooth.CurrentBluetoothDevice
import com.zakhar.watersensorapp.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val navView: BottomNavigationView = findViewById(R.id.activity_settings__nav_view)

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
        CurrentBluetoothDevice.setSocket(null)
        CurrentBluetoothDevice.device = null;
        super.onDestroy()
    }
}
