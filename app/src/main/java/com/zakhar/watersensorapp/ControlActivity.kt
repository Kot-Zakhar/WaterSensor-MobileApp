package com.zakhar.watersensorapp

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.zakhar.watersensorapp.bluetoothCommands.PingCommand
import com.zakhar.watersensorapp.CurrentBluetoothDevice
import kotlinx.android.synthetic.main.activity_control.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.io.IOException
import kotlin.coroutines.CoroutineContext

class ControlActivity: AppCompatActivity(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var job: Job

    private val TAG = "ControlActivity"
    companion object {
        lateinit var adapter: BluetoothAdapter
        var deviceName: String? = null
        var deviceMac: String? = null
        var socket: BluetoothSocket? = null
    }

    fun bindControls() {
        if (socket == null){
            Log.e(TAG, "Socket is null. Cannot bind controls.")
            return
        }

        PingCommand(this, socket!!, activity_control__ping__button, activity_control__ping__text_view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)
        job = Job()

        deviceName = intent.getStringExtra(MainActivity.EXTRA_DEVICE_NAME)
        deviceMac = intent.getStringExtra(MainActivity.EXTRA_DEVICE_MAC)

        socket = CurrentBluetoothDevice.getSocket()

        if (socket == null) {
            Log.i("ControlActivity", "Socket is null.")
            finish()
        }

        bindControls()
    }

    override fun onDestroy() {
        socket?.close()
        job.cancel()
        super.onDestroy()
    }

}