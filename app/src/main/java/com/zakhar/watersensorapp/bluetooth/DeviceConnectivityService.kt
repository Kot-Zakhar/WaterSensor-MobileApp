package com.zakhar.watersensorapp.bluetooth

import android.app.IntentService
import android.content.Intent

class DeviceConnectivityService : IntentService(DeviceConnectivityService::class.simpleName) {
    override fun onHandleIntent(intent: Intent?) {
        val dataString = intent?.dataString

        // dataString can be one of the following values:
        // connect - used in main activity to simply connect to bt device and report about the successful connection
        // maintain - try to reconnect to device if possible and report, when disconnection timeout has passed
        //
    }
}